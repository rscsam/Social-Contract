package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.InterestProfile;

public class EditInterestProfileFragment extends Fragment {
    private EditInterestProfileFListener mListener;

    private InterestProfile interestProfile;

    public EditInterestProfileFragment() {
        // Required empty public constructor
        interestProfile = new InterestProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_interest_profile, container,
                false);
        view.findViewById(R.id.music_interest_cb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setMusic(((CheckBox) view).isChecked());
            }
        });
        view.findViewById(R.id.food_interest_cb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setFood(((CheckBox) view).isChecked());
            }
        });
        view.findViewById(R.id.video_games_interest_cb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setVideoGames(((CheckBox) view).isChecked());
            }
        });
        view.findViewById(R.id.movies_interest_cb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setMovies(((CheckBox) view).isChecked());
            }
        });
        view.findViewById(R.id.sports_interest_cb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setSports(((CheckBox) view).isChecked());
            }
        });
        view.findViewById(R.id.memes_interest_cb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setMemes(((CheckBox) view).isChecked());
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditInterestProfileFListener) {
            mListener = (EditInterestProfileFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditInterestProfileFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public InterestProfile getInterestProfile() {
        return interestProfile;
    }

    public interface EditInterestProfileFListener {
    }
}
