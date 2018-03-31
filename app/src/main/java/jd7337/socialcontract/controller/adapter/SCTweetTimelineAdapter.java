package jd7337.socialcontract.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

/**
 * Created by sam on 3/4/18.
 *
 */

public class SCTweetTimelineAdapter extends TweetTimelineListAdapter {

    TweetSelectListener tweetSelectListener;

    /**
     * Constructs a TweetTimelineListAdapter for the given Tweet Timeline.
     *
     * @param context  the context for row views.
     * @param timeline a Timeline&lt;Tweet&gt; providing access to Tweet data items.
     * @throws IllegalArgumentException if context is null
     */
    public SCTweetTimelineAdapter(Context context, Timeline<Tweet> timeline) {
        super(context, timeline);
    }

    public void setTweetSelectListener(TweetSelectListener tweetSelectListener) {
        this.tweetSelectListener = tweetSelectListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (null != tweetSelectListener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tweet t = getItem(position);
                    tweetSelectListener.onTweetSelect(getItem(position).getId());
                }
            });
        }
        return view;
    }

    public interface TweetSelectListener {
        void onTweetSelect(Long tweetId);
    }

}
