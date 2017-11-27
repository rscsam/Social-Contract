package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jd7337.socialcontract.R;

public class InitialConnectAccountFragment extends Fragment {
    private InitialConnectAccountFListener mListener;

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
