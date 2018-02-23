package jd7337.socialcontract.view.adapter;

import android.app.Activity;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.activity.MainActivity;
import jd7337.socialcontract.view.holder.AccountListItem;

public class AccountListAdapter extends ArrayAdapter<AccountListItem> {

    private final MainActivity context;
    private final AccountListItem[] accounts;

    public AccountListAdapter(Activity context, AccountListItem[] accounts) {
        super(context, R.layout.account_select_item, accounts);
        this.context = (MainActivity) context;
        this.accounts = accounts;
    }

    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        LinearLayout row = (LinearLayout)
                inflater.inflate(R.layout.account_select_item, null, false);
        final LinearLayout additionalSettingsLl =
                (LinearLayout) inflater.inflate(R.layout.discover_settings_account_setting,
                        parent, false);
        Button goButton = additionalSettingsLl.findViewById(R.id.discover_go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.onClickDiscoverSettingsGo();
            }
        });

        ImageView profilePic = row.findViewById(R.id.profile_picture);
        TextView userName = row.findViewById(R.id.account_username);
        ImageView smPlatform = row.findViewById(R.id.social_media_platform_picture);

        AccountListItem account = accounts[position];
        profilePic.setImageBitmap(account.getProfilePicBitmap());
        userName.setText(account.getProfileName());
        smPlatform.setImageResource(account.getSmTypePicId());
        if (account.isShowingExtraSettings()) {
            row.addView(additionalSettingsLl);
        }

        return row;
    }

}
