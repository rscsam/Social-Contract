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
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.controller.listener.InstagramAuthenticationListener;
import jd7337.socialcontract.view.dialog.AuthenticationDialog;

public class InitialConnectAccountFragment extends Fragment implements InstagramAuthenticationListener {
    private InitialConnectAccountFListener mListener;

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
        twLoginButton = view.findViewById(R.id.tw_login_button);
        twLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                authToken = result.data.getAuthToken();
                token = authToken.token;
                secret = authToken.secret;
                userId = result.data.getUserId();
                userName = result.data.getUserName();
                String url = ServerDelegate.SERVER_URL + "/addTwitter";

                Map<String, String> params = new HashMap<>();
                params.put("authToken", token);
                params.put("socialContractId", mListener.getSocialContractId());
                params.put("twitterId", userId.toString());
                params.put("authSecret", secret);
                params.put("username", userName);
                ServerDelegate.postRequest(getContext(), url, params);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (twLoginButton != null) {
            super.onActivityResult(requestCode, resultCode, data);
            twLoginButton.onActivityResult(requestCode, resultCode, data);
        }
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

    public void onInstagramAuthTokenReceived(final String token) {  // this is the actual instagram token
        if (token == null) {
            auth_dialog.dismiss();
        } else {
            String url = "https://api.instagram.com/v1/users/self/?access_token=" + token;
            ServerDelegate.getRequest(getContext(), url, new ServerDelegate.OnResultListener() {
                @Override
                public void onResult(boolean success, JSONObject response) throws JSONException {
                    if (success) {
                        Map<String, String> params = new HashMap<>();
                        params.put("accessToken", token);
                        params.put("socialContractId", mListener.getSocialContractId());
                        params.put("instagramId", response.getJSONObject("data").getString("id"));
                        params.put("username", response.getJSONObject("data").getString("username"));
                        String url = ServerDelegate.SERVER_URL + "/addInstagram";
                        ServerDelegate.postRequest(getContext(), url, params);
                    }
                }
            });
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface InitialConnectAccountFListener {
        String getSocialContractId();
    }
}
