package jd7337.socialcontract.controller.fragment;

import android.accounts.Account;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import jd7337.socialcontract.R;
import jd7337.socialcontract.view.adapter.AccountListAdapter;
import jd7337.socialcontract.view.holder.AccountListItem;

public class AccountSelectFragment extends Fragment {

    private AccountSelectFListener mListener;

    public AccountSelectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_select, container, false);
        //Temporary arrays to fill list
        AccountListItem[] accounts = new AccountListItem[1];
        accounts[0] = new AccountListItem(R.drawable.george_burdell, "George Burdell",
                R.drawable.twitter_icon);
        AccountListAdapter accountsAdapter = new AccountListAdapter(getActivity(), accounts);
        ListView accountList = view.findViewById(R.id.account_list);
        accountList.setAdapter(accountsAdapter);
        accountList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView paraent, View view, int position, long id) {
                        mListener.onClickAccount();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof AccountSelectFListener) {
            mListener = (AccountSelectFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AccountSelectFListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AccountSelectFListener {
        void onClickAccount();
    }
}
