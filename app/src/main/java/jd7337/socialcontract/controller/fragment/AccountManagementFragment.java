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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import android.widget.ImageButton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.TwitterUserService;
import jd7337.socialcontract.model.UserQueryTwitterApiClient;
import retrofit2.Call;

public class AccountManagementFragment extends Fragment {

    private AccountManagementFListener mListener;
    private ProfilePictureView fbProfilePictureView;
    private String userID;  //the user id in our database
    private String fbUserId;
    private String twUserId;
    private String twAccessToken;
    private String insUrl;
    private RequestQueue queue;
    private InitialConnectAccountFragment initialConnectAccountFragment;
    private Button connectAccountButton;
    private AccessToken fbAccessToken;

    private ViewGroup viewTemp;
    private LinearLayout fbLayoutTemp, twLayoutTemp, igLayoutTemp; // temp
    private static final String TAG = "Error";

    private Context mContext;

    private String userId;

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

    public ImageButton deleteFacebookButton(String facebookId) {
        final String fId = facebookId;
        ImageButton deleteFacebookButton = new ImageButton(getActivity());
        deleteFacebookButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteFacebookAccount(fId);
                        viewTemp.removeView(fbLayoutTemp);
                    }
                }
        );
        return deleteFacebookButton;
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
            userId = getArguments().getString("userId");
        }
        Twitter.initialize(getContext());

        mContext = getActivity();
        final View view = inflater.inflate(R.layout.fragment_account_management, container, false);
        viewTemp = (ViewGroup) view;

        // set up connect button
//        connectAccountButton = (Button) view.findViewById(R.id.connectButton);
//        connectAccountButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mListener.onClickAccountConnect();
//                    }
//                }
//        );

        fbLayoutTemp = (LinearLayout) view.findViewById(R.id.fbLinearLayout);
        final LinearLayout fbLayout = (LinearLayout) view.findViewById(R.id.fbLinearLayout);

        userID = mListener.getSocialContractId();
        queue = Volley.newRequestQueue(getContext());

        // retrieve and set facebook account info
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/facebookAccounts";
        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userID);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //set facebook profile
                    fbUserId = response.getJSONArray("accounts").getJSONObject(0).getString("facebookId");
                    String fbToken = response.getJSONArray("accounts").getJSONObject(0).getString("accessToken");
                    String appId = response.getJSONArray("accounts").getJSONObject(0).getString("applicationId");
                    AccessToken fbaccessToken = new AccessToken(fbToken, appId, fbUserId, null, null, null, null, null);
                    setFBPic(fbaccessToken, container);
                    setFbName(fbaccessToken, container);
                    ImageButton fbDeleteButton = deleteFacebookButton(fbUserId);
                    fbDeleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    fbDeleteButton.setImageResource(R.drawable.ic_delete_black_24dp);
                    fbLayout.addView(fbDeleteButton);
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


        final LinearLayout instaLayout = (LinearLayout) view.findViewById(R.id.igLinearLayout);
        igLayoutTemp = instaLayout;
        // retrieve and set instagram account info
        String url2 = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/instagramAccounts";
        Map<String, String> params2 = new HashMap<>();
        params2.put("socialContractId", userID);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST, url2, new JSONObject(params2), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
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


        // retrieve and set twitter account info
        String retrieveTwitterIdUrl = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/twitterAccounts";
        Map<String, String> twitterParams = new HashMap<>();
        twitterParams.put("socialContractId", userID);
        JsonObjectRequest jsonObjectRequestTwitter = new JsonObjectRequest(Request.Method.POST, retrieveTwitterIdUrl, new JSONObject(twitterParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray accounts = response.getJSONArray("accounts");
                            if (accounts.length() > 0) {
                                JSONObject account = accounts.getJSONObject(0);
                                Long twitterId = account.getLong("twitterId");
                                setTwitterProfilePic(twitterId, container);
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

    public void revokePermissions(AccessToken accessToken, String userID) {
        GraphRequest delPermRequest = new GraphRequest(accessToken.getCurrentAccessToken(), "/" + userID + "/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if (graphResponse != null) {
                    FacebookRequestError error = graphResponse.getError();
                    if (error != null) {
                        Log.e(TAG, error.toString());
                    }
                }
            }
        });
        Log.d(TAG, "Executing revoke permissions with graph path" + delPermRequest.getGraphPath());
        delPermRequest.executeAsync();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AccountManagementFListener {
        String getSocialContractId();
        //void onClickAccountConnect();
    }


    private void setFBPic(AccessToken accessToken, final ViewGroup container) {
        Bundle params = new Bundle();
        //params.putString("fields", "name");
        params.putBoolean("redirect", false);
        String graphPath = "me/picture";
        System.out.println("AccessToken is: ");
        System.out.println(AccessToken.getCurrentAccessToken().toString());
        new GraphRequest(accessToken, graphPath, params, HttpMethod.GET,
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
                                        //Should work from here
                                        final Bitmap profilePic= BitmapFactory.decodeStream(picUrl.openStream());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ImageView fbProfilePic = container.findViewById(R.id.fbProfilePic);
                                                fbProfilePic.setImageBitmap(profilePic);
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
                }).executeAsync();

    }


//    private class MyNetworkTask extends AsyncTask<URL, Void, Bitmap> {
//
//        @Override
//        protected Bitmap doInBackground(URL... urls) {
//            URL url = urls[0];
//            try {
//                Bitmap profilePic= BitmapFactory.decodeStream(url.openStream());
//                return profilePic;
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (NetworkOnMainThreadException e) {
//                System.out.println("why the fuck");
//            }
//            return null;
//        }
//    }

   private void setFbName(AccessToken accessToken, final ViewGroup container) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String fbName = response.getJSONObject().getString("name");
                            TextView fbNameTxt = container.findViewById(R.id.fbName);
                            fbNameTxt.setText(fbName);
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

    private void setInsData(String url, final ViewGroup container, final String instaName) {

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
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getStackTrace();
                    }
                });
        queue.add(getRequest);

    }
    /**
     * Deletes a user's Instagram account
     */
    private void deleteInstagramAccount(String instagramId){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/deleteInstagram";

        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userID);

        params.put("instagramId", instagramId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(getActivity(), "Instagram account deleted", Toast.LENGTH_LONG).show();
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "An unexpected error has occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Cannot contact server", Toast.LENGTH_LONG).show();
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
        queue.add(jsonObjectRequest);
    }

    /**
     * Deletes a user's Facebook account
     */
    //private void deleteFacebookAccount(String facebookId, AccessToken accessToken){
    private void deleteFacebookAccount(String facebookId){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/deleteFacebook";

        final String fId = facebookId;
        //final AccessToken ac = accessToken;

        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userID);

        params.put("facebookId", facebookId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(getActivity(), "Facebook account deleted", Toast.LENGTH_LONG).show();
                                //revokePermissions(ac, fId);
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "An unexpected error has occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Cannot contact server", Toast.LENGTH_LONG).show();
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
        queue.add(jsonObjectRequest);
    }

    /**
     * Deletes a user's Twitter account
     */
    private void deleteTwitterAccount(String twitterId){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/deleteTwitter";

        Map<String, String> params = new HashMap<>();
        params.put("socialContractId", userID);

        params.put("twitterId", twitterId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(getActivity(), "Twitter account deleted", Toast.LENGTH_LONG).show();
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "An unexpected error has occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Cannot contact server", Toast.LENGTH_LONG).show();
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

        queue.add(jsonObjectRequest);
    }


}
