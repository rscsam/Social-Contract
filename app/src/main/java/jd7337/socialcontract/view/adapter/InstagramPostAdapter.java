package jd7337.socialcontract.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import jd7337.socialcontract.model.InstagramPost;
import jd7337.socialcontract.model.SocialMediaAccount;
import jd7337.socialcontract.view.holder.AccountListItem;

/**
 * Created by sam on 3/6/18.
 */

public class InstagramPostAdapter  extends ArrayAdapter<InstagramPost> {
    private Activity context;
    private List<InstagramPost> posts;
    private PostSelectListener listener;
    private String userId;

    public InstagramPostAdapter(Activity context, InstagramPost[] posts, String userId) {
        super(context, R.layout.account_select_item, Arrays.asList(posts));
        this.context = context;
        this.posts = new ArrayList<>();
        this.posts.addAll(Arrays.asList(posts));
        this.userId = userId;
    }

    @NonNull
    public View getView(final int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        LinearLayout row = (LinearLayout)
                inflater.inflate(R.layout.instagram_post, null, false);
        ((ImageView) row.findViewById(R.id.instagram_post_media_iv))
                .setImageBitmap(posts.get(position).getBitmap());
        ((TextView) row.findViewById(R.id.instagram_post_caption_tv))
                .setText(posts.get(position).getCaption());
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onPostSelect(posts.get(position).getMediaId());
                }
            }
        });
        return row;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    public void setListener(PostSelectListener listener) {
        this.listener = listener;
    }

    public interface PostSelectListener {
        void onPostSelect(String mediaId);
    }
}
