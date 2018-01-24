package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import jd7337.socialcontract.R;

public class InitialConnectAccountFragment extends Fragment {
    private InitialConnectAccountFListener mListener;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

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
        View connectAccountButton = view.findViewById(R.id.connect_account_button);
        connectAccountButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickICAFConnectAccount();
                    }
                }
        );
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        fbLoginButton.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        //callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast toast = Toast.makeText(getActivity(),"Logged In", Toast.LENGTH_SHORT);
                toast.show();
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
