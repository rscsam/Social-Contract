package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jd7337.socialcontract.R;


public class EditInterestProfilePromptFragment extends Fragment {

    private EditInterestProfilePromptFListener mListener;

    public EditInterestProfilePromptFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_interest_profile_prompt, container,
                false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditInterestProfilePromptFListener) {
            mListener = (EditInterestProfilePromptFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditInterestProfilePromptFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface EditInterestProfilePromptFListener {
        void onClickEIPPEditProfile();
        void onClickEIPPSkip();
    }
}
