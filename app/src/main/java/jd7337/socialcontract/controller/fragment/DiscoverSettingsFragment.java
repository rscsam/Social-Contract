package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.SocialMediaAccount;
import jd7337.socialcontract.model.TwitterUserService;
import jd7337.socialcontract.model.UserQueryTwitterApiClient;
import jd7337.socialcontract.view.adapter.AccountListAdapter;
import jd7337.socialcontract.view.holder.AccountListItem;
import retrofit2.Call;

public class DiscoverSettingsFragment extends Fragment {

    private DiscoverSettingsFListener mListener;

    private boolean additionalSettingsVisible = false;

    public static DiscoverSettingsFragment newInstance(Bundle bundle) {
        DiscoverSettingsFragment fragment = new DiscoverSettingsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public DiscoverSettingsFragment() {
        // Required empty public constructor
    }

    private String userId;
    private List<String> twUserNameList = new ArrayList<>();
    private List<String> twIdList = new ArrayList<>();
    private List<Bitmap> twProfilePicList = new ArrayList<>();
    private List<String> inUserNameList = new ArrayList<>();
    private List<String> inIdList = new ArrayList<>();
    private List<Bitmap> inProfilePicList = new ArrayList<>();
    private List<String> inAccessTokenList = new ArrayList<>();
    private List<SocialMediaAccount> accountsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
        System.out.println("id:" + userId);
        twUserNameList.clear();
        twIdList.clear();
        twProfilePicList.clear();
        inUserNameList.clear();
        inIdList.clear();
        inProfilePicList.clear();
        accountsList.clear();
        Twitter.initialize(getContext());
        View view = inflater.inflate(R.layout.fragment_account_select, container, false);
        TextView instructionsTextView = view.findViewById(R.id.instruction_tv);
        instructionsTextView.setText(R.string.loading_accounts);
        getAccountsInit(container);
        return view;
    }

    public void getAccountsInit(final ViewGroup container) {
         getTwitterAccounts(container);
    }

    private void getTwitterAccounts(final ViewGroup container) {
        String retrieveTwitterIdUrl = ServerDelegate.SERVER_URL + "/twitterAccounts";
        Map<String, String> twitterParams = new HashMap<>();
        twitterParams.put("socialContractId", userId);
        ServerDelegate.postRequest(getContext(), retrieveTwitterIdUrl, twitterParams, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                JSONArray accounts = response.getJSONArray("accounts");
                for (int i = 0; i < accounts.length(); i++) {
                    JSONObject account = accounts.getJSONObject(i);
                    String twitterIdString = account.getString("twitterId");
                    Long twitterId = Long.parseLong(twitterIdString);
                    // retrieves the profile picture and account name
                    // sends true if this is the last Twitter profile
                    setTwitterProfile(twitterId, container, i == accounts.length() - 1);
                    twIdList.add(twitterIdString);
                }
                if (accounts.length() == 0) {
                    getInstagramAccount(container);
                }
            }
        }, new ServerDelegate.OnJSONErrorListener() {
            @Override
            public void onJSONError(JSONException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                // Continue chain of calls if there's a failure
                getInstagramAccount(container);
            }
        }, new ServerDelegate.OnErrorListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Error retrieving Twitter accounts", Toast.LENGTH_SHORT).show();
                // Continue chain of calls if there's a failure
                getInstagramAccount(container);
            }
        });
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
                            // Continue chain of calls if there's a failure
                            if (lastProfile) {
                                getInstagramAccount(container);
                            }
                        } catch (java.io.IOException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            // Continue chain of calls if there's a failure
                            if (lastProfile) {
                                getInstagramAccount(container);
                            }
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TwitterKit", "Set Twitter Image Error", e);
                // Continue chain of calls if there's a failure
                if (lastProfile) {
                    getInstagramAccount(container);
                }
            }
        });
    }

    private void getInstagramAccount(final ViewGroup container) {
        String url = ServerDelegate.SERVER_URL + "/instagramAccounts";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userId);
        ServerDelegate.postRequest(getContext(), url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                JSONArray accounts = response.getJSONArray("accounts");
                for (int i = 0; i < accounts.length(); i++) {
                    String instaAccessToken = accounts.getJSONObject(i).getString("accessToken");
                    String instaName = accounts.getJSONObject(i).getString("username");
                    String insURL = "https://api.instagram.com/v1/users/self/?access_token=" + instaAccessToken;
                    String instaId = accounts.getJSONObject(i).getString("instagramId");
                    // sends true for the last parameter if this is the last Instagram account
                    setInstagramData(insURL, instaName, container, i == accounts.length() - 1);
                    inIdList.add(instaId);
                    inAccessTokenList.add(instaAccessToken);
                }
                if (accounts.length() == 0) {
                    settleAccounts(container);
                }
            }
        }, new ServerDelegate.OnJSONErrorListener() {
            @Override
            public void onJSONError(JSONException e) {
                Toast.makeText(getActivity(), "Failure parsing JSON", Toast.LENGTH_SHORT).show();
                // Continue chain of calls if there's a failure
                settleAccounts(container);
            }
        }, new ServerDelegate.OnErrorListener() {
            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Error retrieving Instagram accounts", Toast.LENGTH_SHORT).show();
                // Continue chain of calls if there's a failure
                settleAccounts(container);
            }
        });
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
                                    // Continue chain of calls if there's a failure
                                    if (lastAccount) {
                                        settleAccounts(container);
                                    }
                                }
                            }
                        });
                        thread.start();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                // Continue chain of calls if there's a failure
                settleAccounts(container);
            }
        });
        queue.add(getRequest);
    }

    private void settleAccounts(final ViewGroup container) {
        int numAccounts = twUserNameList.size();
        numAccounts += inUserNameList.size();
        AccountListItem[] accounts = new AccountListItem[numAccounts];
        int i = 0;
        for (int x = 0; x < twUserNameList.size(); x++) {
            accountsList.add(new SocialMediaAccount(twIdList.get(x), twUserNameList.get(x), SocialMediaAccount.AccountType.TWITTER));
            accounts[i] = new AccountListItem(twProfilePicList.get(x), twUserNameList.get(x), twIdList.get(x), SocialMediaAccount.AccountType.TWITTER);
            i++;
        }
        for (int x = 0; x < inUserNameList.size(); x++) {
            accounts[i] = new AccountListItem(inProfilePicList.get(x), inUserNameList.get(x), inIdList.get(x), SocialMediaAccount.AccountType.INSTAGRAM);
            SocialMediaAccount account = new SocialMediaAccount(inIdList.get(x), inUserNameList.get(x), SocialMediaAccount.AccountType.INSTAGRAM);
            account.setAccessToken(inAccessTokenList.get(x));
            accountsList.add(account);

            i++;
        }
        // Set adapter
        AccountListAdapter accountsAdapter = new AccountListAdapter(getActivity(), accounts, true, userId);
        final ListView accountList = container.findViewById(R.id.account_list);
        accountList.setAdapter(accountsAdapter);
        accountList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        ConstraintLayout discoverDropdown = (ConstraintLayout) view.findViewById(R.id.discover_dropdown);
                        SocialMediaAccount account = accountsList.get(position);
                        SocialMediaAccount.AccountType type = account.getTypeResource();
                        View child;
                        if(type == SocialMediaAccount.AccountType.TWITTER) {
                            child = getLayoutInflater().inflate(R.layout.discover_account_selected_twitter, null, false);
                        } else {
                            child = getLayoutInflater().inflate(R.layout.discover_account_selected_instagram, null, false);
                        }
                        discoverDropdown.addView(child);

                        ViewGroup.LayoutParams params = accountList.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        accountList.setLayoutParams(params);
                        accountList.requestLayout();

                        Button goButton = container.findViewById(R.id.go_button);
                        goButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListener.onClickDiscoverSettingsGo();
                            }
                        });
                        //mListener.onClickDiscoverAccount(accountsList.get(position));
                    }
                }
        );
        TextView instructionTextView = container.findViewById(R.id.instruction_tv);
        // Change instruction text if no accounts are connected
        if (numAccounts == 0) {
            instructionTextView.setText(R.string.no_accounts_connected);
        } else {
        // Change instruction text back
            instructionTextView.setText(R.string.choose_how_you_want_to_discover_new_content);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DiscoverSettingsFListener) {
            mListener = (DiscoverSettingsFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DiscoverSettingsFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DiscoverSettingsFListener {
        void onClickDiscoverSettingsGo();
        void onClickDiscoverAccount(SocialMediaAccount account);
    }
}
