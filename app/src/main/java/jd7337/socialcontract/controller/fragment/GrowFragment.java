package jd7337.socialcontract.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        View view;
        switch (accountType) {
            case FACEBOOK:
                view = inflater.inflate(R.layout.fragment_facebook_grow, container, false);
                interactionTypesRG = view.findViewById(R.id.facebook_interaction_type_rg);
                break;
            case TWITTER:
                view = inflater.inflate(R.layout.fragment_twitter_grow, container, false);
                interactionTypesRG = view.findViewById(R.id.twitter_interaction_type_rg);
                break;
            case INSTAGRAM:
                view = inflater.inflate(R.layout.fragment_instagram_grow, container, false);
                interactionTypesRG = view.findViewById(R.id.instagram_interaction_type_rg);
                break;
            // If for some reason the type is different, inflate the facebook view instead.
            default:
                view = inflater.inflate(R.layout.fragment_facebook_grow, container, false);
                break;
        }
        View connectAccountButton = view.findViewById(R.id.purchase_button);
        quantityET = view.findViewById(R.id.quantityET);
        switch (accountType) {
            case FACEBOOK:
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
                                if (selected != null) {
                                    String type = selected.getText().toString();
                                    type = type.substring(0, type.length() - 1);
                                    int price;
                                    switch (type) {
                                        case "Like":
                                            price = 1;
                                            break;
                                        case "Share":
                                            price = 5;
                                            break;
                                        default:
                                            price = 10;
                                            break;
                                    }
                                    mListener.onClickGrowPurchase(quantity, type, price);
                                } else {
                                    Toast.makeText(getContext(), "Please choose an interaction.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
                break;
            case TWITTER:
                System.out.println("Twittered");
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

                                System.out.println("iT: " + interactionTypesRG);
                                int selectId = interactionTypesRG.getCheckedRadioButtonId();
                                System.out.println("sI: " + selectId);
                                RadioButton selected = interactionTypesRG.findViewById(selectId);
                                if (selected != null) {
                                    String type = selected.getText().toString();
                                    type = type.substring(0, type.length() - 1);
                                    int price;
                                    switch (type) {
                                        case "Like":
                                            price = 1;
                                            break;
                                        case "Share":
                                            price = 5;
                                            break;
                                        case "Follow":
                                            price = 10;
                                            break;
                                        default:
                                            price = 10;
                                            break;
                                    }
                                    mListener.onClickGrowPurchase(quantity, type, price);
                                } else {
                                    Toast.makeText(getContext(), "Please choose an interaction.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
                break;
            case INSTAGRAM:
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
                                if (selected != null) {
                                    String type = selected.getText().toString();
                                    type = type.substring(0, type.length() - 1);
                                    int price;
                                    switch (type) {
                                        case "Like":
                                            price = 1;
                                            break;
                                        case "Share":
                                            price = 5;
                                            break;
                                        case "Follow":
                                            price = 10;
                                            break;
                                        default:
                                            price = 10;
                                            break;
                                    }
                                    mListener.onClickGrowPurchase(quantity, type, price);
                                } else {
                                    Toast.makeText(getContext(), "Please choose an interaction.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
                break;
            default:
                break;
        }
        return view;
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
