package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.QueueItem;
import jd7337.socialcontract.model.SocialMediaAccount;
import jd7337.socialcontract.model.TwitterUserService;
import jd7337.socialcontract.model.UserQueryTwitterApiClient;
import retrofit2.Call;

public class DiscoverFragment extends Fragment {

    private DiscoverFListener mListener;

    private List<QueueItem> queue;
    private int index = 0;
    private SocialMediaAccount.AccountType type;
    private View likeButton, followButton, shareButton;
    private boolean showLikes, showFollows, showShares;
    private LinearLayout contentLL;

    private TwitterAuthClient client;

    public static DiscoverFragment newInstance(Bundle bundle) {
        DiscoverFragment fragment = new DiscoverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int socialMediaTypeOrdinal = getArguments().getInt("SocialMediaTypeOrdinal");
        if (socialMediaTypeOrdinal == 1) {
            type = SocialMediaAccount.AccountType.TWITTER;
        } else {
            type = SocialMediaAccount.AccountType.INSTAGRAM;
        }
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        contentLL = view.findViewById(R.id.content_ll);
        View imDoneButton = view.findViewById(R.id.im_done_button);
        imDoneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickDiscoverImDone();
                    }
                }
        );
        initButtons(view);
        verify(getArguments().getLong("twitterId"));
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", mListener.getSocialContractId());
        params.put("type", type.toString());
        final List<QueueItem> queue = new ArrayList<>();
        ServerDelegate.postRequest(getContext(), ServerDelegate.SERVER_URL + "/getDiscover", params,
                new ServerDelegate.OnResultListener() {
                    @Override
                    public void onResult(boolean success, JSONObject response) throws JSONException {
                        JSONArray jsonQueue = response.getJSONArray("queue");

                        for (int i = 0; i < jsonQueue.length(); i++) {
                            JSONObject object = jsonQueue.getJSONObject(i);
                            String mediaType = (type == SocialMediaAccount.AccountType.TWITTER) ? "twitterId" : "instagramId";
                            queue.add(new QueueItem(object.getString("requestId"),
                                    object.getString("requestingUser"),
                                    object.getString(mediaType),
                                    object.getString("mediaId"),
                                    object.getString("type")));
                        }
                        setQueue(queue);
                    }
                });
        view.findViewById(R.id.skip_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                setIndex();
            }
        });
        return view;
    }

    private void setQueue(List<QueueItem> queue) {
        index = 0;
        this.queue = queue;
        setIndex();
    }

    private void setIndex() {
        if (queue.size() > 0) {
            for (; index < queue.size(); index++) {
                QueueItem curr = queue.get(index);
                String typeStr = curr.getInteractionType();
                if ((showLikes && typeStr.equals("LIKE")) || (showFollows && typeStr.equals("FOLLOW"))
                    || (showShares && typeStr.equals("RETWEET"))) {
                    break;
                }
            }
        }
        if (index >= queue.size() || queue.size() == 0) {
            if (contentLL.getChildCount() > 1) {
                contentLL.removeViewAt(1);
            }
            contentLL.findViewById(R.id.no_content_tv).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.like_text).setVisibility(View.GONE);
            getActivity().findViewById(R.id.skip_text).setVisibility(View.GONE);
            getActivity().findViewById(R.id.coin_icon).setVisibility(View.GONE);
            getActivity().findViewById(R.id.interaction_ib).setVisibility(View.GONE);
        }
        if (index < queue.size() && type == SocialMediaAccount.AccountType.TWITTER) {
            final long tweetId = Long.parseLong(queue.get(index).getMediaId());
            final ImageView interactionIv = getActivity().findViewById(R.id.interaction_ib);
            QueueItem curr = queue.get(index);
            String typeStr = curr.getInteractionType();
            if (typeStr.equals("RETWEET")) {
                interactionIv.setImageResource(R.drawable.retweet_transparent);
                ((TextView) getActivity().findViewById(R.id.like_text)).setText("Retweet for 5");
            } else if (typeStr.equals("LIKE")) {
                interactionIv.setImageResource(R.drawable.like_transparent);
                ((TextView) getActivity().findViewById(R.id.like_text)).setText("Like for 1");
            } else if (typeStr.equals("FOLLOW")) {
                interactionIv.setImageResource(R.drawable.follow_transparent);
                ((TextView) getActivity().findViewById(R.id.like_text)).setText("Follow for 10");
                TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
                UserQueryTwitterApiClient userQueryTwitterApiClient = new UserQueryTwitterApiClient(activeSession);
                TwitterUserService service = userQueryTwitterApiClient.getTwitterUserService();
                Call<User> call = service.show(Long.parseLong(curr.getMediaId()));
                call.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        String bio = result.data.description;
                        String name = result.data.screenName;
                        final LinearLayout follow = (LinearLayout) getLayoutInflater().inflate(R.layout.follow_layout,
                                null);
                        follow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        ((TextView) follow.findViewById(R.id.bio_txt)).setText(bio);
                        ((TextView) follow.findViewById(R.id.name_txt)).setText(name);
                        if (contentLL.getChildCount() > 1) {
                            contentLL.removeViewAt(1);
                        }
                        contentLL.addView(follow);
                        final String photoUrl = result.data.profileImageUrl;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL picUrl = new URL(photoUrl);
                                    final Bitmap profilePic = BitmapFactory.decodeStream(picUrl.openStream());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ImageView) follow.findViewById(R.id.profile_iv)).setImageBitmap(profilePic);
                                        }
                                    });
                                } catch (MalformedURLException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } catch (java.io.IOException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();

                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.e("fail", exception.getMessage());
                    }
                });
            }
            if (!typeStr.equals("FOLLOW")) {
                TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        TweetView tweet = new TweetView(getContext(), result.data);
                        tweet.setTweetActionsEnabled(false);
                        if (contentLL.getChildCount() > 1) {
                            contentLL.removeViewAt(1);
                        }
                        contentLL.addView(tweet);
                        interactionIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                QueueItem curr = queue.get(index);
                                String typeStr = curr.getInteractionType();
                                TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
                                UserQueryTwitterApiClient userQueryTwitterApiClient = new UserQueryTwitterApiClient(activeSession);
                                if (typeStr.equals("RETWEET")) {
                                    StatusesService statusesService = userQueryTwitterApiClient.getStatusesService();
                                    Call<Tweet> call = statusesService.retweet(tweetId, false);
                                    call.enqueue(new Callback<Tweet>() {
                                        @Override
                                        public void success(Result<Tweet> result) {
                                            QueueItem curr = queue.get(index);
                                            Map<String, String> params = new HashMap<>();
                                            params.put("requestId", curr.getRequestId());
                                            params.put("socialContractId", mListener.getSocialContractId());
                                            params.put("mediaId", curr.getMediaId());
                                            params.put("type", type.toString());
                                            params.put("coins", "" + (mListener.getNumCoins() + 5));
                                            ServerDelegate.postRequest(getContext(), ServerDelegate.SERVER_URL + "/discover",
                                                    params, new ServerDelegate.OnResultListener() {
                                                        @Override
                                                        public void onResult(boolean success, JSONObject response) throws JSONException {
                                                            mListener.updateCoinNumber();
                                                        }
                                                    });
                                            index++;
                                            setIndex();
                                        }

                                        public void failure(TwitterException exception) {
                                            QueueItem curr = queue.get(index);
                                            Map<String, String> params = new HashMap<>();
                                            params.put("requestId", curr.getRequestId());
                                            params.put("socialContractId", mListener.getSocialContractId());
                                            params.put("mediaId", curr.getMediaId());
                                            params.put("type", type.toString());
                                            params.put("coins", "" + (mListener.getNumCoins() + 5));
                                            ServerDelegate.postRequest(getContext(), ServerDelegate.SERVER_URL + "/discover",
                                                    params, new ServerDelegate.OnResultListener() {
                                                        @Override
                                                        public void onResult(boolean success, JSONObject response) throws JSONException {
                                                            mListener.updateCoinNumber();
                                                        }
                                                    });
                                            index++;
                                            setIndex();
                                        }
                                    });
                                } else if (typeStr.equals("LIKE")) {
                                    FavoriteService favoriteService = userQueryTwitterApiClient.getFavoriteService();
                                    Call<Tweet> call = favoriteService.create(tweetId, false);
                                    call.enqueue(new Callback<Tweet>() {
                                        @Override
                                        public void success(Result<Tweet> result) {
                                            QueueItem curr = queue.get(index);
                                            Map<String, String> params = new HashMap<>();
                                            params.put("requestId", curr.getRequestId());
                                            params.put("socialContractId", mListener.getSocialContractId());
                                            params.put("mediaId", curr.getMediaId());
                                            params.put("type", type.toString());
                                            params.put("coins", "" + (mListener.getNumCoins() + 1));
                                            ServerDelegate.postRequest(getContext(), ServerDelegate.SERVER_URL + "/discover",
                                                    params, new ServerDelegate.OnResultListener() {
                                                        @Override
                                                        public void onResult(boolean success, JSONObject response) throws JSONException {
                                                            mListener.updateCoinNumber();
                                                        }
                                                    });
                                            index++;
                                            setIndex();
                                        }

                                        public void failure(TwitterException exception) {
                                            QueueItem curr = queue.get(index);
                                            Map<String, String> params = new HashMap<>();
                                            params.put("requestId", curr.getRequestId());
                                            params.put("socialContractId", mListener.getSocialContractId());
                                            params.put("mediaId", curr.getMediaId());
                                            params.put("type", type.toString());
                                            params.put("coins", "" + (mListener.getNumCoins() + 1));
                                            ServerDelegate.postRequest(getContext(), ServerDelegate.SERVER_URL + "/discover",
                                                    params, new ServerDelegate.OnResultListener() {
                                                        @Override
                                                        public void onResult(boolean success, JSONObject response) throws JSONException {
                                                            mListener.updateCoinNumber();
                                                        }
                                                    });
                                            index++;
                                            setIndex();
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // Toast.makeText(...).show();
                        int i = 0;
                    }
                });
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DiscoverFListener) {
            mListener = (DiscoverFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DiscoverFListener");
        }
    }

    private void initButtons(View view) {
        likeButton = view.findViewById(R.id.discover_like_icon);
        likeButton.setTag(R.drawable.like_transparent);
        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleLike();
                    }
                }
        );

        shareButton = view.findViewById(R.id.discover_retweet_icon);
        shareButton.setTag(R.drawable.retweet_transparent);
        shareButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleShare();
                    }
                }
        );

        followButton = view.findViewById(R.id.discover_follow_icon);
        followButton.setTag(R.drawable.follow_transparent);
        followButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleFollow();
                    }
                }
        );

        final byte[] selectedInteractions = getArguments().getByteArray("SelectedInteractions");
        if (selectedInteractions[0] == 1) {
            toggleLike();
        }
        if (selectedInteractions[1] == 1) {
            toggleFollow();
        }
        if (type == SocialMediaAccount.AccountType.TWITTER) {
            if (selectedInteractions[2] == 1) {
                toggleShare();
            }
        } else {
            shareButton.setVisibility(View.GONE);
        }
    }

    private void toggleLike() {
        ImageView imageView = (ImageView) likeButton;

        Integer tag = (Integer) imageView.getTag();
        tag = tag == null ? 0 : tag;

        switch(tag) {
            case R.drawable.like_transparent:
                imageView.setTag(R.drawable.like_selected);
                imageView.setImageResource(R.drawable.like_selected);
                showLikes = true;
                break;
            case R.drawable.like_selected:
                imageView.setTag(R.drawable.like_transparent);
                imageView.setImageResource(R.drawable.like_transparent);
                showLikes = false;
                break;
        }
    }

    private void toggleShare() {
        ImageView imageView = (ImageView) shareButton;

        Integer tag = (Integer) imageView.getTag();
        tag = tag == null ? 0 : tag;

        switch(tag) {
            case R.drawable.retweet_transparent:
                imageView.setTag(R.drawable.retweet_selected);
                imageView.setImageResource(R.drawable.retweet_selected);
                showShares = true;
                break;
            case R.drawable.retweet_selected:
                imageView.setTag(R.drawable.retweet_transparent);
                imageView.setImageResource(R.drawable.retweet_transparent);
                showShares = false;
                break;
        }
    }

    private void toggleFollow() {
        ImageView imageView = (ImageView) followButton;

        Integer tag = (Integer) imageView.getTag();
        tag = tag == null ? 0 : tag;

        switch(tag) {
            case R.drawable.follow_transparent:
                imageView.setTag(R.drawable.follow_selected);
                imageView.setImageResource(R.drawable.follow_selected);
                showFollows = true;
                break;
            case R.drawable.follow_selected:
                imageView.setTag(R.drawable.follow_transparent);
                imageView.setImageResource(R.drawable.follow_transparent);
                showFollows = false;
                break;
        }
    }

    private void verify(final Long twitterId) {
        TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (null == activeSession) {
            Toast.makeText(getContext(), "Please log in with the account you want to interact using",
                    Toast.LENGTH_LONG);
            client = new TwitterAuthClient();
            client.authorize(getActivity(), new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    //feedback
                    TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
                    UserQueryTwitterApiClient userQueryTwitterApiClient = new UserQueryTwitterApiClient(activeSession);
                    TwitterUserService twitterUserService = userQueryTwitterApiClient.getTwitterUserService();
                    Call<User> userCall = twitterUserService.show(twitterId);
                    userCall.enqueue(new Callback<User>() {
                        @Override
                        public void success(Result<User> userResult) {
                            verify(twitterId);
                        }

                        @Override
                        public void failure(TwitterException e) {
                            verify(twitterId);
                        }
                    });
                }

                @Override
                public void failure(TwitterException exception) {
                    mListener.onClickDiscoverImDone();
                }
            });
        }
    }

    public void onActivityResult(int request, int result, Intent data) {
        client.onActivityResult(request, result, data);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DiscoverFListener {
        void onClickDiscoverImDone();
        String getSocialContractId();
        void updateCoinNumber();
        int getNumCoins();
    }
}
