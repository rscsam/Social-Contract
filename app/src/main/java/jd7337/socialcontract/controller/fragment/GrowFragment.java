package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jd7337.socialcontract.R;

public class GrowFragment extends Fragment {

    private GrowFListener mListener;

    public GrowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grow, container, false);
        View connectAccountButton = view.findViewById(R.id.purchase_button);
        connectAccountButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickGrowPurchase();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GrowFListener) {
            mListener = (GrowFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GrowFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface GrowFListener {
        void onClickGrowPurchase();
    }
}
