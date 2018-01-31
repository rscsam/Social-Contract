package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jd7337.socialcontract.R;

public class AccountManagementFragment extends Fragment {

    private AccountManagementFListener mListener;

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AccountManagementFListener {
    }
}
