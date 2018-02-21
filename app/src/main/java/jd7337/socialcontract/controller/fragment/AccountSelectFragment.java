package jd7337.socialcontract.controller.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.TwitterUserService;
import jd7337.socialcontract.model.UserQueryTwitterApiClient;
import jd7337.socialcontract.view.adapter.AccountListAdapter;
import jd7337.socialcontract.view.holder.AccountListItem;
import retrofit2.Call;

public class AccountSelectFragment extends Fragment {

    private AccountSelectFListener mListener;

    private String userId;
    private List<Bitmap> fbProfilePicList = new ArrayList<>();
    private List<String> fbUserNameList = new ArrayList<>();
    private List<String> twUserNameList = new ArrayList<>();
    private List<Bitmap> twProfilePicList = new ArrayList<>();
    private List<String> inUserNameList = new ArrayList<>();
    private List<Bitmap> inProfilePicList = new ArrayList<>();

    public static AccountSelectFragment newInstance(Bundle bundle) {
        AccountSelectFragment fragment = new AccountSelectFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public AccountSelectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
        fbUserNameList.clear();
        fbProfilePicList.clear();
        twUserNameList.clear();
        twProfilePicList.clear();
        inUserNameList.clear();
        inProfilePicList.clear();
        Twitter.initialize(getContext());
        View view = inflater.inflate(R.layout.fragment_account_select, container, false);
        TextView instructionsTextView = view.findViewById(R.id.instruction_tv);
        instructionsTextView.setText(R.string.loading_accounts);
        getAccountsInit(container);
        return view;
    }

    public void getAccountsInit(final ViewGroup container) {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Retrieve Facebook account
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/facebookAccounts";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userId);
        JsonObjectRequest jsonObjectRequestFB = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray accounts = response.getJSONArray("accounts");
                            for (int i = 0; i < accounts.length(); i++) {
                                //set facebook profile
                                String fbUserId = response.getJSONArray("accounts").getJSONObject(0).getString("facebookId");
                                String fbToken = response.getJSONArray("accounts").getJSONObject(0).getString("accessToken");
                                String appId = response.getJSONArray("accounts").getJSONObject(0).getString("applicationId");
                                AccessToken fbaccessToken = new AccessToken(fbToken, appId, fbUserId, null, null, null, null, null);
                                // send true as the last parameter if this is the last Facebook account
                                getFBName(fbaccessToken, container, i == accounts.length() - 1);
                            }
                            if (accounts.length() == 0) {
                                getTwitterAccounts(container);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsonObjectRequestFB);
    }

    private void getFBName(final AccessToken fbAccessToken, final ViewGroup container, final boolean lastAccount) {
        GraphRequest request = GraphRequest.newMeRequest(fbAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String fbName = response.getJSONObject().getString("name");
                            setFBAccount(fbAccessToken, fbName, container, lastAccount);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void setFBAccount(final AccessToken fbAccessToken, final String fbName, final ViewGroup container, final boolean lastAccount) {
        // set FB pic
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        String graphPath = "me/picture";
        new GraphRequest(fbAccessToken, graphPath, params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(final GraphResponse response) {
                        if (response != null) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject data = response.getJSONObject();
                                        String profilePicUrl = data.getJSONObject("data").getString("url");
                                        URL picUrl = new URL(profilePicUrl);
                                        final Bitmap profilePic= BitmapFactory.decodeStream(picUrl.openStream());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fbProfilePicList.add(profilePic);
                                                fbUserNameList.add(fbName);
                                                if (lastAccount) {
                                                    getTwitterAccounts(container);
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }
                    }
                }
        ).executeAsync();

    }

    private void getTwitterAccounts(final ViewGroup container) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String retrieveTwitterIdUrl = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/twitterAccounts";
        Map<String, String> twitterParams = new HashMap<>();
        twitterParams.put("socialContractId", userId);
        JsonObjectRequest jsonObjectRequestTwitter = new JsonObjectRequest(Request.Method.POST, retrieveTwitterIdUrl, new JSONObject(twitterParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray accounts = response.getJSONArray("accounts");
                            for (int i = 0; i < accounts.length(); i++) {
                                JSONObject account = accounts.getJSONObject(i);
                                String twitterIdString = account.getString("twitterId");
                                Long twitterId = Long.parseLong(twitterIdString);
                                // retrieves the profile picture and account name
                                // sends true if this is the last Twitter profile
                                setTwitterProfile(twitterId, container, i == accounts.length() - 1);
                            }
                            if (accounts.length() == 0) {
                                getInstagramAccount(container);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsonObjectRequestTwitter);

    }

    /**
     * Retrieves and stores profile pic and username
     * @param twitterId - the user's twitter id
     * @param container - view group for the layout
     * @param lastProfile - true if this is the last Twitter profile and Instagram should be called next
     */
    private void setTwitterProfile(Long twitterId, final ViewGroup container, final boolean lastProfile) {
        TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
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
                                    twProfilePicList.add(profilePic);
                                    twUserNameList.add(userName);
                                    if (lastProfile) {
                                        getInstagramAccount(container);
                                    }
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
            public void failure(TwitterException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                Log.d("TwitterKit", "Set Twitter Image Error", e);
            }
        });
    }

    private void getInstagramAccount(final ViewGroup container) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/instagramAccounts";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userId);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //set instagram profile
                    JSONArray accounts = response.getJSONArray("accounts");
                    for (int i = 0; i < accounts.length(); i++) {
                        String instaAccessToken = response.getJSONArray("accounts").getJSONObject(0).getString("accessToken");
                        String instaName = response.getJSONArray("accounts").getJSONObject(0).getString("username");
                        String insURL = "https://api.instagram.com/v1/users/self/?access_token=" + instaAccessToken;
                        // sends true for the last parameter if this is the last Instagram account
                        setInstagramData(insURL, instaName, container, i == accounts.length() - 1);
                    }
                    if (accounts.length() == 0) {
                        settleAccounts(container);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public  Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsonObjectRequest2);
    }

    private void setInstagramData(String url, final String instaName, final ViewGroup container, final boolean lastAccount) {
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
                                            inProfilePicList.add(profilePic);
                                            inUserNameList.add(instaName);
                                            if (lastAccount) {
                                                settleAccounts(container);
                                            }
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

    private void settleAccounts(ViewGroup container) {
        int numAccounts = fbUserNameList.size();
        numAccounts += twUserNameList.size();
        numAccounts += inUserNameList.size();
        AccountListItem[] accounts = new AccountListItem[numAccounts];
        int i = 0;
        for (int x = 0; x < fbUserNameList.size(); x++) {
            accounts[i] = new AccountListItem(fbProfilePicList.get(x), fbUserNameList.get(x), R.drawable.facebookicon);
            i++;
        }
        for (int x = 0; x < twUserNameList.size(); x++) {
            accounts[i] = new AccountListItem(twProfilePicList.get(x), twUserNameList.get(x), R.drawable.twitter_icon);
            i++;
        }
        for (int x = 0; x < inUserNameList.size(); x++) {
            accounts[i] = new AccountListItem(inProfilePicList.get(x), inUserNameList.get(x), R.drawable.instagram_icon);
        }
        // Set adapter
        AccountListAdapter accountsAdapter = new AccountListAdapter(getActivity(), accounts);
        ListView accountList = container.findViewById(R.id.account_list);
        accountList.setAdapter(accountsAdapter);
        accountList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        mListener.onClickAccount();
                    }
                }
        );
        // Change instruction text back
        TextView instructionTextView = container.findViewById(R.id.instruction_tv);
        instructionTextView.setText(R.string.choose_which_account_to_grow);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof AccountSelectFListener) {
            mListener = (AccountSelectFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AccountSelectFListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AccountSelectFListener {
        void onClickAccount();
    }
}
