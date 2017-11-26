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

import jd7337.socialcontract.R;

public class GrowFragment extends Fragment {

    private GrowFListener mListener;

    private RadioGroup interactionTypesRG;
    private EditText quantityET;

    public GrowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grow, container, false);
        View connectAccountButton = view.findViewById(R.id.purchase_button);
        interactionTypesRG = view.findViewById(R.id.interaction_type_rg);
        quantityET = view.findViewById(R.id.quantityET);
        connectAccountButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = Integer.parseInt(quantityET.getText().toString());

                        int selectId = interactionTypesRG.getCheckedRadioButtonId();
                        System.out.println(selectId);
                        RadioButton selected = interactionTypesRG.findViewById(selectId);
                        System.out.println(selected);
                        System.out.println(selected.getText().toString());
                        String type = selected.getText().toString();
                        int price;
                        switch (type) {
                            case "Likes":
                                price = 1;
                                break;
                            case "Retweets":
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
