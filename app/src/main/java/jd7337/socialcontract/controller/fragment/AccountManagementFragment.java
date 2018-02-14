package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;

import jd7337.socialcontract.R;

public class AccountManagementFragment extends Fragment {

    private AccountManagementFListener mListener;

    private static final String TAG = "Error";

    public AccountManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

    public void revokePermissions(AccessToken accessToken) {
        GraphRequest delPermRequest = new GraphRequest(accessToken.getCurrentAccessToken(), "/{user-id}/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if (graphResponse != null) {
                    FacebookRequestError error = graphResponse.getError();
                    if (error != null) {
                        Log.e(TAG, error.toString());
                    } else {
                        getActivity().finish();
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
    }
}
