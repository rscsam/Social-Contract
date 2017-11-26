package jd7337.socialcontract.view.adapter;

import android.app.Activity;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import jd7337.socialcontract.R;
import jd7337.socialcontract.view.holder.AccountListItem;

public class AccountListAdapter extends ArrayAdapter<AccountListItem> {

    private final Activity context;
    private final AccountListItem[] accounts;

    public AccountListAdapter(Activity context, AccountListItem[] accounts) {
        super(context, R.layout.account_select_item, accounts);
        this.context = context;
        this.accounts = accounts;
    }

    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.account_select_item, null, false);

        ImageView profilePic = row.findViewById(R.id.profile_picture);
        TextView userName = row.findViewById(R.id.account_username);
        ImageView smPlatform = row.findViewById(R.id.social_media_platform_picture);

        profilePic.setImageResource(accounts[position].getProfilePicId());
        userName.setText(accounts[position].getProfileName());
        smPlatform.setImageResource(accounts[position].getSmTypePicId());

        return row;
    }

}
