package jd7337.socialcontract.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.InstagramPost;

/**
 * Created by sam on 3/6/18.
 *
 */

public class InstagramPostAdapter  extends ArrayAdapter<InstagramPost> {
    private Activity context;
    private List<InstagramPost> posts;
    private String userId;

    public InstagramPostAdapter(Activity context, InstagramPost[] posts, boolean selectEnabled,
                                String userId) {
        super(context, R.layout.account_select_item, Arrays.asList(posts));
        this.context = context;
        this.posts = new ArrayList<>();
        this.posts.addAll(Arrays.asList(posts));
        this.userId = userId;
    }

    @NonNull
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        LinearLayout row = (LinearLayout)
                inflater.inflate(R.layout.instagram_post, null, false);
        ((ImageView) row.findViewById(R.id.instagram_post_media_iv))
                .setImageBitmap(posts.get(position).getBitmap());
        ((TextView) row.findViewById(R.id.instagram_post_caption_tv))
                .setText(posts.get(position).getCaption());

        return row;
    }

    @Override
    public int getCount() {
        return posts.size();
    }
}
