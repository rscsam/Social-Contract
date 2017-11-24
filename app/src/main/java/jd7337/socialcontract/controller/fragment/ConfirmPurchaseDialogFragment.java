package jd7337.socialcontract.controller.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import jd7337.socialcontract.R;

public class ConfirmPurchaseDialogFragment extends DialogFragment{

    private ConfirmPurchaseDialogFListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Purchase 1 Follower for 10 coins?")
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onClickConfirmPurchase();
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

    public interface ConfirmPurchaseDialogFListener {
        void onClickConfirmPurchase();
    }

}
