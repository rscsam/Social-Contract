package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jd7337.socialcontract.R;
import jd7337.socialcontract.controller.activity.TutorialActivity;
import jd7337.socialcontract.controller.delegate.ServerDelegate;
import jd7337.socialcontract.model.InterestProfile;

public class EditInterestProfileFragment extends Fragment {
    private EditInterestProfileFListener mListener;

    private InterestProfile interestProfile;
    private CheckBox musicCB, foodCB, videogamesCB, moviesCB, sportsCB, memesCB;

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

        musicCB = view.findViewById(R.id.music_interest_cb);
        foodCB = view.findViewById(R.id.food_interest_cb);
        videogamesCB = view.findViewById(R.id.video_games_interest_cb);
        moviesCB = view.findViewById(R.id.movies_interest_cb);
        sportsCB = view.findViewById(R.id.sports_interest_cb);
        memesCB = view.findViewById(R.id.memes_interest_cb);

        musicCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setMusic(((CheckBox) view).isChecked());
            }
        });
        foodCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setFood(((CheckBox) view).isChecked());
            }
        });
        videogamesCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setVideoGames(((CheckBox) view).isChecked());
            }
        });
        moviesCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setMovies(((CheckBox) view).isChecked());
            }
        });
        sportsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setSports(((CheckBox) view).isChecked());
            }
        });
        memesCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestProfile.setMemes(((CheckBox) view).isChecked());
            }
        });

        Button saveChangesBttn = view.findViewById(R.id.eip_save_changes_bttn);
        if (mListener instanceof TutorialActivity) {
            saveChangesBttn.setVisibility(View.GONE);
        } else {
            saveChangesBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ServerDelegate.sendInterestProfile(getContext(), mListener.getSocialContractId(),
                            getInterestProfile());
                }
            });
        }

        refreshFromServer();
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

    @Override
    public void onResume() {
        super.onResume();
        refreshFromServer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener instanceof TutorialActivity)
            ServerDelegate.sendInterestProfile(getContext(), mListener.getSocialContractId(),
                getInterestProfile());
    }

    private void refreshFromServer() {
        String url = ServerDelegate.SERVER_URL + "/interestProfile";
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("socialContractId", mListener.getSocialContractId());
        ServerDelegate.postRequest(getContext(), url, requestParams,
                new ServerDelegate.OnResultListener() {
            @Override
            public void onResult(boolean success, JSONObject response) throws JSONException {
                if (success) {
                    interestProfile.setMusic(response.getBoolean("music"));
                    interestProfile.setFood(response.getBoolean("food"));
                    interestProfile.setSports(response.getBoolean("sports"));
                    interestProfile.setMovies(response.getBoolean("movies"));
                    interestProfile.setVideoGames(response.getBoolean("videogames"));
                    interestProfile.setMemes(response.getBoolean("memes"));

                    refreshCheckboxes();
                }
            }
        });
    }

    private void refreshCheckboxes() {
        musicCB.setChecked(interestProfile.getMusic());
        foodCB.setChecked(interestProfile.getFood());
        sportsCB.setChecked(interestProfile.getSports());
        moviesCB.setChecked(interestProfile.getMovies());
        videogamesCB.setChecked(interestProfile.getVideoGames());
        memesCB.setChecked(interestProfile.getMemes());
    }

    public InterestProfile getInterestProfile() {
        return interestProfile;
    }

    public interface EditInterestProfileFListener {
        String getSocialContractId();
    }
}
