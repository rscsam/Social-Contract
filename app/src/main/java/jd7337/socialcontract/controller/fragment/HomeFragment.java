package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jd7337.socialcontract.R;

public class HomeFragment extends Fragment {

    private HomeFListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container,
                false);

        View growButton = view.findViewById(R.id.grow_button);
        growButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickHomeGrow();
                    }
                }
        );

        View discoverButton = view.findViewById(R.id.discover_button);
        discoverButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickHomeDiscover();
                    }
                }
        );

        if (getArguments() != null) {
            String text = getArguments().getString("request");
            if (text != null) {
                setImage(view);
            }
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeFListener) {
            mListener = (HomeFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setImage(View view) {
        view.findViewById(R.id.home_no_request_tv).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.home_tap_discover_tv).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.home_or_tv).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.home_tap_grow_tv).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.home_request_image).setVisibility(View.VISIBLE);
    }

    public interface HomeFListener {
        void onClickHomeDiscover();
        void onClickHomeGrow();
    }
}
