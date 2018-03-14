package jd7337.socialcontract.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.controller.delegate.SocialMediaDelegate;
import jd7337.socialcontract.model.InstagramPost;
import jd7337.socialcontract.model.SocialMediaAccount;
import jd7337.socialcontract.view.adapter.InstagramPostAdapter;

public class InstagramFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_feed);
        final String userId = getIntent().getStringExtra("userId");
        final String accessToken = getIntent().getStringExtra("accessToken");

        String url = "https://api.instagram.com/v1/users/self/media/recent/?access_token="
                + accessToken;
        final Activity context = this;
        ServerDelegate.getRequest(this, url, new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                JSONArray data = response.getJSONArray("data");
                final InstagramPost[] posts = new InstagramPost[data.length()];
                for (int i = 0; i < data.length(); i++) {
                    JSONObject media = data.getJSONObject(i);
                    JSONObject images = media.getJSONObject("images");
                    JSONObject image = images.getJSONObject("standard_resolution");
                    String url = image.getString("url");
                    final String mediaId = media.getString("id");
                    String captionText = "";
                    if (!media.isNull("caption")) {
                        final JSONObject caption = media.getJSONObject("caption");
                        if (!caption.isNull("text"))
                            captionText = caption.getString("text");
                    }
                    final int j = i;
                    final String text = captionText;
                    SocialMediaDelegate.receiveBitmapFromInstagram(context, url,
                             new SocialMediaDelegate.OnBitmapReceivedListener() {
                                @Override
                                public void onBitmapReceived(final Bitmap response) {
                                    posts[j] = new InstagramPost(mediaId, text, response);
                                }
                            });
                }
                boolean wasteTime = true;
                while (wasteTime) {
                    wasteTime = false;
                    for (InstagramPost post : posts) {
                        if (post == null) {
                            wasteTime = true;
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                System.out.println("If it gets here it should have worked idk");
                final InstagramPostAdapter adapter = new InstagramPostAdapter(context, posts, userId);
                adapter.setListener(new InstagramPostAdapter.PostSelectListener() {
                    @Override
                    public void onPostSelect(String mediaId) {
                        Intent result = new Intent();
                        if (getIntent().getExtras() != null) {
                            result.putExtras(getIntent().getExtras());
                            result.putExtra("mediaId", mediaId);
                            setResult(Activity.RESULT_OK, result);
                        } else {
                            setResult(Activity.RESULT_CANCELED);
                        }
                        finish();
                    }
                });
                ListView postLV = findViewById(R.id.instagram_posts_lv);
                postLV.setAdapter(adapter);
            }
        });

    }
}
