package jd7337.socialcontract.controller.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jd7337.socialcontract.R;

/**
 * Created by sam on 2/18/18.
 */

public class SocialMediaAccountAdapter extends ArrayAdapter<String> {

    @LayoutRes
    private int layout;

    /**
     * Calls the superclass constructor, and sets the subclass fields
     *
     * @param context  The application context
     * @param names  The names of both the folders and the files for this ListView
     */
    public SocialMediaAccountAdapter(@NonNull Context context, List<String> names) {
        super(context, R.layout.account_select_item, names.toArray(new String[names.size()]));
        layout = R.layout.account_select_item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(layout, parent, false);
//        String name = getItem(position);
//        TextView nameTV = (TextView) row.findViewById(R.id.name_tv);
//        nameTV.setText(name);
//        ImageView iconIV = (ImageView) row.findViewById(R.id.row_image_iv);
//        if (position < numDirs) {
//            iconIV.setImageResource(R.drawable.ic_is_folder);
//        }
        return row;
    }
}
