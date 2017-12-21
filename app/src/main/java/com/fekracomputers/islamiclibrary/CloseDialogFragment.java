package com.fekracomputers.islamiclibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Mohammad on 20/12/2017.
 */

public class CloseDialogFragment extends DialogFragment {
    CloseDialogFragmentListener closeDialogFragmentListener;

    public CloseDialogFragment() {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        closeDialogFragmentListener.onOkPressed();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.book_information_error_close)
                .setPositiveButton(R.string.ok, (dialog, which) -> closeDialogFragmentListener.onOkPressed())
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            closeDialogFragmentListener = (CloseDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CloseDialogFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        closeDialogFragmentListener = null;
    }

    interface CloseDialogFragmentListener {
        void onOkPressed();
    }
}
