package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jd7337.socialcontract.R;

public class DiscoverSettingsFragment extends Fragment {

    private DiscoverSettingsFListener mListener;

    public DiscoverSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_settings, container,
                false);
        View goButton = view.findViewById(R.id.discover_go_button);
        goButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickDiscoverSettingsGo();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DiscoverSettingsFListener) {
            mListener = (DiscoverSettingsFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DiscoverSettingsFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DiscoverSettingsFListener {
        void onClickDiscoverSettingsGo();
    }
}
