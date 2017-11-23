package jd7337.socialcontract.view;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import jd7337.socialcontract.R;

public class AccountListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final Integer[] profilePicture;
    private final String[] username;
    private final Integer[] socialMediaPlatform;

    public AccountListAdapter(Activity context, Integer[] profilePicture, String[] username,
                              Integer[] socialMediaPlatform) {
        super(context, R.layout.account_select_item, username);
        this.context = context;
        this.profilePicture = profilePicture;
        this.username = username;
        this.socialMediaPlatform = socialMediaPlatform;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.account_select_item, null, false);

        ImageView profilePic = (ImageView) rowView.findViewById(R.id.profile_picture);
        TextView userName = (TextView) rowView.findViewById(R.id.account_username);
        ImageView smPlatform = (ImageView) rowView.findViewById(R.id.social_media_platform_picture);

        profilePic.setImageResource(profilePicture[position]);
        userName.setText(username[position]);
        smPlatform.setImageResource(socialMediaPlatform[position]);

        return rowView;
    }

}
