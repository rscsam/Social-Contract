package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import jd7337.socialcontract.R;

public class InitialConnectAccountFragment extends Fragment {
    private InitialConnectAccountFListener mListener;

    private TwitterLoginButton loginButton;
    private TwitterAuthToken authToken;
    private String token;
    private String secret;
    private Long userId;
    private String userName;

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
        loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_connect_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
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
        View connectAccountButton = view.findViewById(R.id.connect_account_button);
        connectAccountButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickICAFConnectAccount();
                    }
                }
        );
        return view;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
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
