package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jd7337.socialcontract.R;

public class EditInterestProfileFragment extends Fragment {
    private EditInterestProfileFListener mListener;

    public EditInterestProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_interest_profile, container,
                false);
        Button submitButton = (Button) view.findViewById(R.id.submit_ep_button);
        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickEIPSubmit();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditInterestProfileFListener) {
            mListener = (EditInterestProfileFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditInterestProfileFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface EditInterestProfileFListener {
        void onClickEIPSubmit();
    }
}
