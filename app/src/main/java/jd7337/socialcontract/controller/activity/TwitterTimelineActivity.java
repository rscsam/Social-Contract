package jd7337.socialcontract.controller.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.adapter.SCTweetTimelineAdapter;

public class TwitterTimelineActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_timeline);

        String username = getIntent().getStringExtra("username");

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(username)
                .includeRetweets(false)
                .build();
        final TweetTimelineListAdapter adapter = new SCTweetTimelineAdapter(this, userTimeline);
        ((SCTweetTimelineAdapter) adapter).setTweetSelectListener(new SCTweetTimelineAdapter.TweetSelectListener() {
            @Override
            public void onTweetSelect(Long tweetId) {
                Intent result = new Intent();
                if (getIntent().getExtras() != null) {
                    result.putExtras(getIntent().getExtras());
                    result.putExtra("tweetId", tweetId);
                    setResult(Activity.RESULT_OK, result);
                } else {
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();
            }
        });
        setListAdapter(adapter);
        Toast.makeText(this, "Please select a tweet", Toast.LENGTH_LONG).show();
    }
}
