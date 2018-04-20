package jd7337.socialcontract.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.activity.MainActivity;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.SocialMediaAccount;
import jd7337.socialcontract.view.holder.AccountListItem;

public class AccountListAdapter extends ArrayAdapter<AccountListItem> {

    private final MainActivity context;
    private final List<AccountListItem> accounts;
    private boolean selectEnabled;
    private String userId;

    public AccountListAdapter(Activity context, AccountListItem[] accounts, boolean selectEnabled,
                              String userId) {
        super(context, R.layout.account_select_item, Arrays.asList(accounts));
        this.context = (MainActivity) context;
        this.accounts = new ArrayList<>();
        this.accounts.addAll(Arrays.asList(accounts));
        this.selectEnabled = selectEnabled;
        this.userId = userId;
    }

    @NonNull
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        LinearLayout row = (LinearLayout)
                inflater.inflate(R.layout.account_select_item, null, false);

        ImageView profilePic = row.findViewById(R.id.profile_picture);
        TextView userName = row.findViewById(R.id.account_username);
        ImageView smPlatform = row.findViewById(R.id.social_media_platform_picture);

        final AccountListItem account = accounts.get(position);
        profilePic.setImageBitmap(account.getProfilePicBitmap());
        userName.setText(account.getProfileName());
        smPlatform.setImageResource(account.getSmTypePicId());
        if (account.isShowingExtraSettings()) {
            ConstraintLayout additionalSettingsDropdown;
            final CheckBox[] checkBoxes;
            if (account.getSocialMediaType() == SocialMediaAccount.AccountType.TWITTER) {
                additionalSettingsDropdown = (ConstraintLayout) inflater.inflate(
                        R.layout.discover_account_selected_twitter, parent, false);
                checkBoxes = new CheckBox[3];
                checkBoxes[0] = additionalSettingsDropdown.findViewById(R.id.checkBox1);
                checkBoxes[1] = additionalSettingsDropdown.findViewById(R.id.checkBox3);
                checkBoxes[2] = additionalSettingsDropdown.findViewById(R.id.checkBox2);
            } else {  // if it's an instagram account
                additionalSettingsDropdown = (ConstraintLayout) inflater.inflate(
                        R.layout.discover_account_selected_instagram, parent, false);
                checkBoxes = new CheckBox[2];
                checkBoxes[0] = additionalSettingsDropdown.findViewById(R.id.checkBox1);
                checkBoxes[1] = additionalSettingsDropdown.findViewById(R.id.checkBox2);
            }
            Button goButton = additionalSettingsDropdown.findViewById(R.id.discover_go_button);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    byte[] selectedInteractions = new byte[checkBoxes.length];
                    for (int i = 0; i < checkBoxes.length; i++) {
                        selectedInteractions[i] = checkBoxes[i].isChecked() ? (byte) 0b1 : 0;
                    }
                    context.onClickDiscoverSettingsGo(account.getSocialMediaType().ordinal(), selectedInteractions, Long.parseLong(account.getSmId()));
                }
            });
            row.addView(additionalSettingsDropdown);
        }
        if (accounts.get(position).isShowDelete()) {
            View deleteIB = row.findViewById(R.id.delete_ib);
            deleteIB.setVisibility(View.VISIBLE);
            if (accounts.get(position).getSocialMediaType() == SocialMediaAccount.AccountType.INSTAGRAM) {
                deleteIB.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = ServerDelegate.SERVER_URL + "/deleteInstagram";

                                Map<String, String> params = new HashMap<>();
                                params.put("socialContractId", userId);
                                params.put("instagramId", accounts.get(position).getSmId());
                                ServerDelegate.postRequest(getContext(), url, params, new ServerDelegate.OnResultListener() {
                                    @Override
                                    public void onResult(boolean success, JSONObject response) throws JSONException {
                                        if (success) {
                                            Toast.makeText(context, "Instagram account deleted", Toast.LENGTH_LONG).show();
                                        } else {
                                            String message = response.getString("message");
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                accounts.remove(accounts.get(position));
                                notifyDataSetChanged();
                            }
                        }
                );
            } else {
                deleteIB.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = ServerDelegate.SERVER_URL + "/deleteTwitter";

                                Map<String, String> params = new HashMap<>();
                                params.put("socialContractId", userId);
                                params.put("twitterId", accounts.get(position).getSmId());
                                ServerDelegate.postRequest(getContext(), url, params, new ServerDelegate.OnResultListener() {
                                    @Override
                                    public void onResult(boolean success, JSONObject response) throws JSONException {
                                        if (success) {
                                            Toast.makeText(context, "Twitter account deleted", Toast.LENGTH_LONG).show();
                                        } else {
                                            String message = response.getString("message");
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                accounts.remove(accounts.get(position));
                                notifyDataSetChanged();
                            }
                        }
                );
            }
        }
        return row;
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public boolean isEnabled(int position) {
        return selectEnabled && super.isEnabled(position);
    }
}
