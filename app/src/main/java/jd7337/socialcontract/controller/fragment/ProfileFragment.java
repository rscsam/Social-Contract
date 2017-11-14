package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jd7337.socialcontract.R;

public class ProfileFragment extends Fragment {

    private ProfileFListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button accountManagementButton = (Button) view.findViewById(R.id.account_management_button);
        accountManagementButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickAccountManagement();
                    }
                }
        );

        Button editInterestProfileButton = (Button) view.findViewById(R.id.edit_interest_profile_button);
        editInterestProfileButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickInterestProfile();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFListener) {
            mListener = (ProfileFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfileFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ProfileFListener {
        void onClickAccountManagement();
        void onClickInterestProfile();
    }

}
