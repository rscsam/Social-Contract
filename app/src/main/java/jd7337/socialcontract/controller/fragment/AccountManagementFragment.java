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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.TwitterUserService;
import jd7337.socialcontract.model.UserQueryTwitterApiClient;
import retrofit2.Call;

public class AccountManagementFragment extends Fragment {

    private AccountManagementFListener mListener;
    private String userID;  //the user id in our database

    private ViewGroup viewTemp;
    private LinearLayout twLayoutTemp, igLayoutTemp; // temp

    public AccountManagementFragment() {
        // Required empty public constructor
    }

    public static AccountManagementFragment newInstance(String userId) {
        AccountManagementFragment fragment = new AccountManagementFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageButton deleteTwitterButton(String twitterId) {
        final String tId = twitterId;
        ImageButton deleteTwitterButton = new ImageButton(getActivity());
        deleteTwitterButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTwitterAccount(tId);
                    viewTemp.removeView(twLayoutTemp);
                }
            }
        );
        return deleteTwitterButton;
    }

    public ImageButton deleteInstagramButton(String instagramId) {
        final String iId = instagramId;
        ImageButton deleteInstagramButton = new ImageButton(getActivity());
        deleteInstagramButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteInstagramAccount(iId);
                        viewTemp.removeView(igLayoutTemp);
                    }
                }
        );

        return deleteInstagramButton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            userID = getArguments().getString("userId");
        }
        Twitter.initialize(getContext());

        final View view = inflater.inflate(R.layout.fragment_account_management, container, false);
        viewTemp = (ViewGroup) view;

        view.findViewById(R.id.connectButtonAccountManagement).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickAccountConnect();
                    }
                }
        );

        userID = mListener.getSocialContractId();

        final LinearLayout instaLayout = (LinearLayout) view.findViewById(R.id.igLinearLayout);
        igLayoutTemp = instaLayout;
        // retrieve and set instagram account info
        String url2 = ServerDelegate.SERVER_URL + "/instagramAccounts";
        Map<String, String> params2 = new HashMap<>();
        params2.put("socialContractId", userID);
        ServerDelegate.postRequest(getContext(), url2, params2, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                //set instagram profile
                String instaAccessToken = response.getJSONArray("accounts").getJSONObject(0).getString("accessToken");
                String instaName = response.getJSONArray("accounts").getJSONObject(0).getString("username");
                String instaId = response.getJSONArray("accounts").getJSONObject(0).getString("instagramId");
                String insURL = "https://api.instagram.com/v1/users/self/?access_token=" + instaAccessToken;
                setInsData(insURL, container, instaName);

                ImageButton inDeleteButton = deleteInstagramButton(instaId);
                inDeleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                inDeleteButton.setImageResource(R.drawable.ic_delete_black_24dp);
                instaLayout.addView(inDeleteButton);
            }
        });

        // retrieve and set twitter account info
        String retrieveTwitterIdUrl = ServerDelegate.SERVER_URL + "/twitterAccounts";
        Map<String, String> twitterParams = new HashMap<>();
        twitterParams.put("socialContractId", userID);
        ServerDelegate.postRequest(getContext(), retrieveTwitterIdUrl, twitterParams,
                new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                JSONArray accounts = response.getJSONArray("accounts");
                if (accounts.length() > 0) {
                    JSONObject account = accounts.getJSONObject(0);
                    String twitterIdString = account.getString("twitterId");
                    Long twitterId = Long.parseLong(twitterIdString);
                    setTwitterProfilePic(twitterId, container);
                }
            }
        });

        return view;
    }

    private void setTwitterProfilePic(final Long twitterId, final ViewGroup container) {
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
                                    ImageView twProfilePic = container.findViewById(R.id.twProfilePic);
                                    twProfilePic.setImageBitmap(profilePic);
                                    TextView twNameTextView = container.findViewById(R.id.twaccountName);
                                    twNameTextView.setText(userName);
                                    ImageButton twDeleteButton = deleteTwitterButton(twitterId.toString());
                                    twDeleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    twDeleteButton.setImageResource(R.drawable.ic_delete_black_24dp);
                                    LinearLayout twitterLayout = (LinearLayout) container.findViewById(R.id.twLinearLayout);
                                    twLayoutTemp = twitterLayout;
                                    twitterLayout.addView(twDeleteButton);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccountManagementFListener) {
            mListener = (AccountManagementFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AccountManagementFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setInsData(String url, final ViewGroup container, final String instaName) {
        ServerDelegate.getRequest(getContext(), url, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, final JSONObject response) throws JSONException {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String picUrl = response.getJSONObject("data").getString("profile_picture");
                            URL url = new URL(picUrl);
                            final Bitmap profilePic= BitmapFactory.decodeStream(url.openStream());
                            //Bitmap profilePic = getBitmapFromURL(picUrl);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView igProfilePic = container.findViewById(R.id.igProfilePic);
                                    igProfilePic.setImageBitmap(profilePic);
                                    TextView igNameTxt = container.findViewById(R.id.igName);
                                    igNameTxt.setText(instaName);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    /**
     * Deletes a user's Instagram account
     */
    private void deleteInstagramAccount(String instagramId){
        String url = ServerDelegate.SERVER_URL + "/deleteInstagram";

        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userID);
        params.put("instagramId", instagramId);
        ServerDelegate.postRequest(getContext(), url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    Toast.makeText(getActivity(), "Instagram account deleted", Toast.LENGTH_LONG).show();
                    //revokePermissions(ac, fId);
                } else {
                    String message = response.getString("message");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Deletes a user's Twitter account
     */
    private void deleteTwitterAccount(String twitterId){
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/deleteTwitter";

        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userID);
        params.put("twitterId", twitterId);
        ServerDelegate.postRequest(getContext(), url, params, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    Toast.makeText(getActivity(), "Twitter account deleted", Toast.LENGTH_LONG).show();
                } else {
                    String message = response.getString("message");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public interface AccountManagementFListener {
        String getSocialContractId();
        void onClickAccountConnect();
    }

}
