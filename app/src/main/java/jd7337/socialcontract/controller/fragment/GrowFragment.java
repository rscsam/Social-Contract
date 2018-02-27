package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.constraint.ConstraintSet;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.SocialMediaAccount;

import java.util.ArrayList;

public class GrowFragment extends Fragment {

    private GrowFListener mListener;

    private SocialMediaAccount.AccountType accountType;

    private RadioGroup interactionTypesRG;
    private EditText quantityET;

    public GrowFragment() {
        // Required empty public constructor
    }

    public static GrowFragment newInstance(Bundle bundle) {
        GrowFragment growFragment = new GrowFragment();
        growFragment.setArguments(bundle);
        return growFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int typeOrdinal = getArguments().getInt("typeInt");
        accountType = SocialMediaAccount.AccountType.values()[typeOrdinal];
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grow, container, false);
        View connectAccountButton = view.findViewById(R.id.purchase_button);
        View facebookCl = view.findViewById(R.id.facebook_cl);
        // Get all the facebook views
        ArrayList<View> facebookViews = getAllViewChildren(facebookCl);

        View twitterCl = view.findViewById(R.id.twitter_cl);
        // Get all the twitter views
        ArrayList<View> twitterViews = getAllViewChildren(facebookCl);

        View instagramCl = view.findViewById(R.id.instagram_cl);
        // Get all the instagram views
        ArrayList<View> instagramViews = getAllViewChildren(facebookCl);

        quantityET = view.findViewById(R.id.quantityET);
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout growLayout = view.findViewById(R.id.grow_cl);
        constraintSet.clone(growLayout);
        switch (accountType) {
            case FACEBOOK:
                // Find all the facebook elements and make them visible
                for (View v : facebookViews) {
                    v.setVisibility(View.VISIBLE);
                }
                // Find all the twitter elements and hide them
                for (View v : twitterViews) {
                    v.setVisibility(View.GONE);
                }
                // Find all the instagram elements and hide them
                for (View v : instagramViews) {
                    v.setVisibility(View.GONE);
                }
                interactionTypesRG = view.findViewById(R.id.facebook_interaction_type_rg);
                constraintSet.connect(quantityET.getId(), ConstraintSet.TOP, facebookCl.getId(), ConstraintSet.BOTTOM);
                break;
            case TWITTER:
                // Find all the facebook elements and make them visible
                for (View v : facebookViews) {
                    v.setVisibility(View.GONE);
                }
                // Find all the twitter elements and hide them
                for (View v : twitterViews) {
                    v.setVisibility(View.VISIBLE);
                }
                // Find all the instagram elements and hide them
                for (View v : instagramViews) {
                    v.setVisibility(View.GONE);
                }
                interactionTypesRG = view.findViewById(R.id.twitter_interaction_type_rg);
                constraintSet.connect(quantityET.getId(), ConstraintSet.TOP, twitterCl.getId(), ConstraintSet.BOTTOM);
                break;
            case INSTAGRAM:
                // Find all the facebook elements and make them visible
                for (View v : facebookViews) {
                    v.setVisibility(View.GONE);
                }
                // Find all the twitter elements and hide them
                for (View v : twitterViews) {
                    v.setVisibility(View.GONE);
                }
                // Find all the instagram elements and hide them
                for (View v : instagramViews) {
                    v.setVisibility(View.VISIBLE);
                }
                interactionTypesRG = view.findViewById(R.id.instagram_interaction_type_rg);
                constraintSet.connect(quantityET.getId(), ConstraintSet.TOP, instagramCl.getId(), ConstraintSet.BOTTOM);
                break;
            default:
                break;
        }
        constraintSet.applyTo(growLayout);
        connectAccountButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity;
                        try {
                            quantity = Integer.parseInt(quantityET.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Please choose a quantity.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int selectId = interactionTypesRG.getCheckedRadioButtonId();
                        RadioButton selected = interactionTypesRG.findViewById(selectId);
                        String type = selected.getText().toString();
                        type = type.substring(0, type.length() - 1);
                        int price;
                        switch (type) {
                            case "Like":
                                price = 1;
                                break;
                            case "Retweet":
                                price = 5;
                                break;
                            default:
                                price = 10;
                                break;
                        }

                        mListener.onClickGrowPurchase(quantity, type, price);
                    }
                }
        );
        return view;
    }

    private ArrayList<View> getAllViewChildren(View v) {
        ArrayList<View> result = new ArrayList<>();

        // add the view
        result.add(v);

        // if the view has children, add them and their children
        if (v instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                result.addAll(getAllViewChildren(((ViewGroup) v).getChildAt(i)));
            }
        }

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GrowFListener) {
            mListener = (GrowFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GrowFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface GrowFListener {
        void onClickGrowPurchase(int quantity, String type, int individualPrice);
    }
}
