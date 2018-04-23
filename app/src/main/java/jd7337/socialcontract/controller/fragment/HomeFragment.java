package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.QueueListItem;
import jd7337.socialcontract.model.SocialMediaAccount;
import jd7337.socialcontract.model.TwitterUserService;
import jd7337.socialcontract.model.UserQueryTwitterApiClient;
import jd7337.socialcontract.view.adapter.QueueAdapter;
import retrofit2.Call;

public class HomeFragment extends Fragment {

    private HomeFListener mListener;
    private String socialContractId;
    private QueueAdapter adapter;
    private ListView listView;
    private ViewGroup container;
    final Map<String, String> instagramAccess = new HashMap<>();
    final Map<String, String> instagramNames = new HashMap<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            socialContractId = getArguments().getString("userId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container,
                false);

        View growButton = view.findViewById(R.id.grow_button);
        growButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickHomeGrow();
                    }
                }
        );

        View discoverButton = view.findViewById(R.id.discover_button);
        discoverButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickHomeDiscover();
                    }
                }
        );

        String url = ServerDelegate.SERVER_URL + "/getQueue";
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("socialContractId", socialContractId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ServerDelegate.postRequest(this.getActivity(), url, requestParams, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                JSONArray twitter = response.getJSONArray("twitter");
                JSONArray instagram = response.getJSONArray("instagram");

                if(twitter.length() > 0 || instagram.length() > 0) {
                    hideText(view);
                    showQueue(twitter, instagram);
                }
            }
        });

        this.container = container;

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeFListener) {
            mListener = (HomeFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showQueue(JSONArray twitter, JSONArray instagram) {
        adapter = new QueueAdapter(getActivity(), new ArrayList<QueueListItem>());
        listView = container.findViewById(R.id.request_feed);
        listView.setAdapter(adapter);

        for (int i = 0; i < twitter.length(); i++) {
            try {
                JSONObject json = twitter.getJSONObject(i);
                QueueListItem item = new QueueListItem(null, null,
                        json.getInt("progress"), json.getInt("goal"),
                        json.getString("type"), SocialMediaAccount.AccountType.TWITTER);
                displayTwitterRequest(Long.parseLong(json.getString("twitterId")), item);

            } catch (JSONException e) {
                Log.e("HomeFragment", "Error parsing JSON");
            }
        }

        getInstagramInfo();

        for (int i = 0; i < instagram.length(); i++) {
            try {
                JSONObject json = instagram.getJSONObject(i);
                String instagramId = json.getString("instagramId");
                String username = instagramNames.get(instagramId);
                String instaAccessToken = instagramAccess.get(instagramId);
                String url = "https://api.instagram.com/v1/users/self/?access_token=" + instaAccessToken;

                QueueListItem item = new QueueListItem(null, username,
                        json.getInt("progress"), json.getInt("goal"),
                        json.getString("type"), SocialMediaAccount.AccountType.INSTAGRAM);

                displayInstagramRequest(url, item);

            } catch (JSONException e) {
                Log.e("HomeFragment", "Error parsing JSON");
            }
        }

    }

    /**
     * Retrieves and stores profile pic and username
     * @param twitterId - the user's twitter id
     * @param item - the QueueListItem
     */
    private void displayTwitterRequest(Long twitterId, final QueueListItem item) {
        TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (null == activeSession) {
            return;
        }

        UserQueryTwitterApiClient userQueryTwitterApiClient = new UserQueryTwitterApiClient(activeSession);
        TwitterUserService twitterUserService = userQueryTwitterApiClient.getTwitterUserService();
        Call<User> userCall = twitterUserService.show(twitterId);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                final String photoUrl = userResult.data.profileImageUrl;
                final String userName = userResult.data.screenName;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL picUrl = new URL(photoUrl);
                            final Bitmap profilePic = BitmapFactory.decodeStream(picUrl.openStream());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    item.setProfilePic(profilePic);
                                    item.setUsername(userName);
                                    adapter.add(item);
                                }
                            });
                        } catch (java.io.IOException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TwitterKit", "Set Twitter Image Error", e);

            }
        });
    }

    private void getInstagramInfo() {
        String url = ServerDelegate.SERVER_URL + "/instagramAccounts";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", socialContractId);
        ServerDelegate.postRequest(getContext(), url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                JSONArray accounts = response.getJSONArray("accounts");
                for (int i = 0; i < accounts.length(); i++) {
                    String instaAccessToken = accounts.getJSONObject(i).getString("accessToken");
                    String instaId = accounts.getJSONObject(i).getString("instagramId");
                    String instaName = accounts.getJSONObject(i).getString("username");
                    instagramAccess.put(instaId, instaAccessToken);
                    instagramNames.put(instaId, instaName);

                }

            }
        }, new ServerDelegate.OnJSONErrorListener() {
            @Override
            public void onJSONError(JSONException e) {
                Toast.makeText(getActivity(), "Failure parsing JSON", Toast.LENGTH_SHORT).show();
            }
        }, new ServerDelegate.OnErrorListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Error retrieving Instagram accounts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayInstagramRequest(String url, final QueueListItem item) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String picUrl = response.getJSONObject("data").getString("profile_picture");
                                    URL url = new URL(picUrl);
                                    final Bitmap profilePic= BitmapFactory.decodeStream(url.openStream());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            item.setProfilePic(profilePic);
                                            adapter.add(item);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
            }
        });
        queue.add(getRequest);
    }

    public void hideText(View view) {
        view.findViewById(R.id.home_no_request_tv).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.home_tap_discover_tv).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.home_or_tv).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.home_tap_grow_tv).setVisibility(View.INVISIBLE);
    }

    public interface HomeFListener {
        void onClickHomeDiscover();
        void onClickHomeGrow();
    }
}
