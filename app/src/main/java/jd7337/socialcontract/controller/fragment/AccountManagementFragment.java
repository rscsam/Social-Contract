package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.mopub.volley.AuthFailureError;
import com.mopub.volley.Request;
import com.mopub.volley.RequestQueue;
import com.mopub.volley.Response;
import com.mopub.volley.VolleyError;
import com.mopub.volley.toolbox.JsonObjectRequest;
import com.mopub.volley.toolbox.Volley;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;
import retrofit2.Call;

public class AccountManagementFragment extends Fragment {

    private AccountManagementFListener mListener;
    private ProfilePictureView fbProfilePictureView;
    private String userID;  //the user id in our database
    private String fbUserId;
    private String fbAccessToken;
    private String twUserId;
    private String twAccessToken;




    public AccountManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_account_management, container, false);
        userID = mListener.getSocialContractId();
        System.out.println(userID);
        RequestQueue queue = Volley.newRequestQueue(getContext());

        //facebook account
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/facebookAccounts";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userID);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    //set facebook profile
                    fbUserId = response.getJSONArray("accounts").getJSONObject(0).getString("facebookId");
                    //fbAccessToken = response.getJSONArray("accounts").getJSONObject(0).getString("accessToken");
                    System.out.println(fbUserId);
//                    fbProfilePictureView = view.findViewById(R.id.fbProfilePic);
//                    fbProfilePictureView.setProfileId(fbUserId);
                    getFBPic(fbUserId, container);

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
        queue.add(jsonObjectRequest);


        // twitter account
        String url2 = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/twitterAccounts";
        Map<String, String> params2 = new HashMap<>();
        params.put("socialContractId", userID);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST, url2, new JSONObject(params2), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                //set facebook profile
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                AccountService accountService = twitterApiClient.getAccountService();
                Call<User> call = accountService.verifyCredentials(true, true, true);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {

                    }

                    @Override
                    public void failure(TwitterException exception) {
                    }
                });
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



        return inflater.inflate(R.layout.fragment_account_management, container, false);
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

    public interface AccountManagementFListener {
        String getSocialContractId();
    }


    private void getFBPic(String fbUserId, final ViewGroup container) {
        Bundle params = new Bundle();
        //params.putString("fields", "id,email,gender,cover,picture.type(large)");
        params.putBoolean("redirect", false);
        String graphPath = "me/picture";
        new GraphRequest(AccessToken.getCurrentAccessToken(), graphPath, params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                System.out.println(data);
                                String profilePicUrl = data.getJSONObject("data").getString("url");
                                URL picUrl = new URL(profilePicUrl);
                                System.out.println(profilePicUrl);
                                //Should work from here
//                                Bitmap profilePic= BitmapFactory.decodeStream(picUrl.openConnection().getInputStream());
//                                ImageView fbProfilePic = container.findViewById(R.id.fbProfilePic);
//                                fbProfilePic.setImageBitmap(profilePic);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }

}
