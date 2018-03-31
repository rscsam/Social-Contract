package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.QueueItem;
import jd7337.socialcontract.model.SocialMediaAccount;

public class DiscoverFragment extends Fragment {

    private DiscoverFListener mListener;

    private List<QueueItem> queue;
    private int index = 0;
    private SocialMediaAccount.AccountType type;
    private View likeButton, followButton, shareButton;
    private boolean showLikes, showFollows, showShares;
    private LinearLayout contentLL;

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
        }
        final Callback<Tweet> actionCallback = new Callback<Tweet>() {
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

            @Override
            public void failure(TwitterException exception) {
            }
        };
        if (index < queue.size() && type == SocialMediaAccount.AccountType.TWITTER) {
            final long tweetId = Long.parseLong(queue.get(index).getMediaId());
            TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    TweetView tweet = new TweetView(getContext(), result.data);
                    tweet.setTweetActionsEnabled(true);
                    tweet.setOnActionCallback(actionCallback);
                    if (contentLL.getChildCount() > 1) {
                        contentLL.removeViewAt(1);
                    }
                    contentLL.addView(tweet);
                }

                @Override
                public void failure(TwitterException exception) {
                    // Toast.makeText(...).show();
                    int i = 0;
                }
            });
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
