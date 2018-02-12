package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.activity.LoginActivity;
import jd7337.socialcontract.controller.listener.InstagramAuthenticationListener;
import jd7337.socialcontract.view.dialog.AuthenticationDialog;

public class InitialConnectAccountFragment extends Fragment implements InstagramAuthenticationListener {
    private InitialConnectAccountFListener mListener;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    private TwitterLoginButton twLoginButton;
    private TwitterAuthToken authToken;
    private String token;
    private String secret;
    private Long userId;
    private String userName;

    private AuthenticationDialog auth_dialog;
    private ImageButton inLoginButton;

    public InitialConnectAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_initial_connect_account, container, false);

        // Twitter connection
        twLoginButton = (TwitterLoginButton) view.findViewById(R.id.tw_login_button);
        twLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(getContext(), "Successful log in", Toast.LENGTH_SHORT).show();
                authToken = result.data.getAuthToken();
                token = authToken.token;
                secret = authToken.secret;
                userId = result.data.getUserId();
                userName = result.data.getUserName();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Facebook connection
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
        fbLoginButton.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        //callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                ProfileTracker profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        this.stopTracking();
                        Profile.setCurrentProfile(currentProfile);
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/addFacebook";

                        Map<String, String> params = new HashMap<>();
                        params.put("accessToken", loginResult.getAccessToken().getToken());
                        params.put("socialContractId", mListener.getSocialContractId());
                        params.put("facebookId", Profile.getCurrentProfile().getId());
                        params.put("applicationId", getString(R.string.facebook_app_id));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            boolean success = response.getBoolean("success");
                                            if (success) {
                                                Toast.makeText(getContext(), "It worked", Toast.LENGTH_SHORT).show();
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
                };
            }

            @Override
            public void onCancel() {
                // App code
                Toast toast = Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onError(FacebookException exception) {

            }


        });

        // Instagram connection
        inLoginButton = view.findViewById(R.id.in_login_button);

        final InstagramAuthenticationListener l = this;
        inLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth_dialog = new AuthenticationDialog(getContext(), l);
                auth_dialog.setCancelable(true);
                auth_dialog.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        twLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InitialConnectAccountFListener) {
            mListener = (InitialConnectAccountFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InitialConnectAccountFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onCodeReceived(String access_token) {  // this is the actual instagram token
        System.out.println("Code Received");
        if (access_token == null) {
            auth_dialog.dismiss();
        } else {

            RequestQueue queue = Volley.newRequestQueue(getContext());

            String url = "https://api.instagram.com/v1/users/self/?access_token=" + access_token;

            final String token = access_token;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    String url = "http://ec2-18-220-246-27.us-east-2.compute.amazonaws.com:3000/addInstagram";

                                    Map<String, String> params = new HashMap<>();
                                    params.put("accessToken", token);
                                    params.put("socialContractId", mListener.getSocialContractId());
                                    params.put("instagramId", response.getString("id"));
                                    params.put("username", response.getString("username"));
                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        boolean success = response.getBoolean("success");
                                                        if (success) {
                                                            Toast.makeText(getContext(), "It worked", Toast.LENGTH_SHORT).show();
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
                                    }
                                    ) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            HashMap<String, String> headers = new HashMap<>();
                                            headers.put("Content-Type", "application/json; charset=utf-8");
                                            return headers;
                                        }
                                    };
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface InitialConnectAccountFListener {
        void onClickICAFConnectAccount();
        String getSocialContractId();
    }
}
