package jd7337.socialcontract.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.activity.MainActivity;
import jd7337.socialcontract.model.InstagramPost;
import jd7337.socialcontract.model.QueueListItem;

/**
 * Created by Ryan on 3/7/2018.
 */

public class QueueAdapter extends ArrayAdapter<QueueListItem> {

    private Activity context;
    private List<QueueListItem> queue;

    public QueueAdapter(Activity context, List<QueueListItem> queue) {
        super(context, R.layout.account_select_item, queue);
        this.context = (MainActivity) context;
        this.queue = (ArrayList<QueueListItem>) queue;
    }

    @NonNull
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        LinearLayout row = (LinearLayout)
                inflater.inflate(R.layout.queue_list_item, null, false);
        ((ImageView) row.findViewById(R.id.profile_icon))
                .setImageBitmap(queue.get(position).getBitmap());
        ((TextView) row.findViewById(R.id.account_username))
                .setText(queue.get(position).getUsername());
        ((ProgressBar) row.findViewById(R.id.downloadProgressBar))
                .setProgress(queue.get(position).getProgressToGoal());
        ((TextView) row.findViewById(R.id.num_request))
                .setText(queue.get(position).getText());
        ((ImageView) row.findViewById(R.id.social_icon))
                .setImageResource(queue.get(position).getImageResource());

        return row;
    }

    @Override
    public int getCount() {
        return queue.size();
    }

    public void add(QueueListItem item)
    {
        queue.add(item);
        notifyDataSetChanged();
    }
}
