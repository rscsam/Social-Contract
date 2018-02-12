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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import jd7337.socialcontract.R;
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

    //facebook Related
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private AccessToken fbAccessToken;
    private String fbAppID;
    private String fbUserID;

    private AuthenticationDialog auth_dialog;
    private ImageButton inLoginButton;

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

        //callback registration for facebook
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(getActivity(),"Logged In", Toast.LENGTH_SHORT);
                toast.show();
                fbAccessToken = loginResult.getAccessToken();
                fbAppID =  fbAccessToken.getApplicationId();
                fbUserID = fbAccessToken.getUserId();
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
        if (access_token == null) {
            auth_dialog.dismiss();
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
    }
}
