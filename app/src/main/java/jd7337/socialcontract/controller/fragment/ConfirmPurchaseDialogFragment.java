package jd7337.socialcontract.controller.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.SocialMediaAccount;

public class ConfirmPurchaseDialogFragment extends DialogFragment{

    private ConfirmPurchaseDialogFListener mListener;

    private int quantity;
    private SocialMediaAccount account;
    private String type;
    private int totalPrice;
    private String accessToken;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String priceStr = totalPrice == 1 ? "1 coin?" : totalPrice + " coins?";
        String message = quantity == 1
                ? "Purchase 1 " + type + " for " + priceStr
                : "Purchase " + quantity + " " + type + "s for " + priceStr;
        builder.setMessage(message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onClickConfirmPurchase(account, type, quantity, totalPrice);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConfirmPurchaseDialogFListener) {
            mListener = (ConfirmPurchaseDialogFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ConfirmPurchaseDialogFListener");
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public SocialMediaAccount getAccount() {
        return account;
    }

    public void setAccount(SocialMediaAccount account) {
        this.account = account;
    }

    public interface ConfirmPurchaseDialogFListener {
        void onClickConfirmPurchase(SocialMediaAccount account, String type, int quantity,
                                    int individualPrice);
    }

}
