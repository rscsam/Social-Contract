package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jd7337.socialcontract.R;

public class DiscoverFragment extends Fragment {

    private DiscoverFListener mListener;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        View imDoneButton = view.findViewById(R.id.im_done_button);
        imDoneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onClickDiscoverImDone();
                    }
                }
        );

        View likeIcon = view.findViewById(R.id.discover_like_icon);
        likeIcon.setTag(R.drawable.like_selected);
        likeIcon.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view;

                    Integer tag = (Integer) imageView.getTag();
                    tag = tag == null ? 0 : tag;

                    switch(tag) {
                        case R.drawable.like_transparent:
                            imageView.setTag(R.drawable.like_selected);
                            imageView.setImageResource(R.drawable.like_selected);
                            break;
                        case R.drawable.like_selected:
                            imageView.setTag(R.drawable.like_transparent);
                            imageView.setImageResource(R.drawable.like_transparent);
                            break;
                    }

                }
            }
        );

        View retweetIcon = view.findViewById(R.id.discover_retweet_icon);
        retweetIcon.setTag(R.drawable.retweet_transparent);
        retweetIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageView imageView = (ImageView) view;

                        Integer tag = (Integer) imageView.getTag();
                        tag = tag == null ? 0 : tag;

                        switch(tag) {
                            case R.drawable.retweet_transparent:
                                imageView.setTag(R.drawable.retweet_selected);
                                imageView.setImageResource(R.drawable.retweet_selected);
                                break;
                            case R.drawable.retweet_selected:
                                imageView.setTag(R.drawable.retweet_transparent);
                                imageView.setImageResource(R.drawable.retweet_transparent);
                                break;
                        }

                    }
                }
        );

        View followIcon = view.findViewById(R.id.discover_follow_icon);
        followIcon.setTag(R.drawable.follow_transparent);
        followIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageView imageView = (ImageView) view;

                        Integer tag = (Integer) imageView.getTag();
                        tag = tag == null ? 0 : tag;

                        switch(tag) {
                            case R.drawable.follow_transparent:
                                imageView.setTag(R.drawable.follow_selected);
                                imageView.setImageResource(R.drawable.follow_selected);
                                break;
                            case R.drawable.follow_selected:
                                imageView.setTag(R.drawable.follow_transparent);
                                imageView.setImageResource(R.drawable.follow_transparent);
                                break;
                        }

                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DiscoverFListener) {
            mListener = (DiscoverFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DiscoverFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DiscoverFListener {
        void onClickDiscoverImDone();
    }
}
