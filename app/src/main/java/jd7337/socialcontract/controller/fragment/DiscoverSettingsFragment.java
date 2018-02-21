package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import jd7337.socialcontract.R;
import jd7337.socialcontract.view.adapter.AccountListAdapter;
import jd7337.socialcontract.view.holder.AccountListItem;

public class DiscoverSettingsFragment extends Fragment {

    private DiscoverSettingsFListener mListener;

    private boolean additionalSettingsVisible = false;

    public DiscoverSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_settings, container,
                false);

        //Temporary arrays to fill list
        /* Don't render list for now
        AccountListItem[] accounts = new AccountListItem[1];
        accounts[0] = new AccountListItem(R.drawable.george_burdell, "George Burdell",
                R.drawable.twitter_icon);
        AccountListAdapter accountsAdapter = new AccountListAdapter(getActivity(), accounts);
        final ListView accountList = view.findViewById(R.id.account_list);
        accountList.setAdapter(accountsAdapter);
        accountList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        AccountListItem item = (AccountListItem) parent.getItemAtPosition(position);
                        item.setShowingExtraSettings(true);
                        accountList.invalidateViews();
                    }
                }
        );
        */
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
