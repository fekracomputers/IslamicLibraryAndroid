package com.fekracomputers.islamiclibrary.browsing.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 16/5/2017.
 */
public class ConfirmBatchDownloadDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    BatchDownloadConfirmationListener mListener;


    public ConfirmBatchDownloadDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        int booksCount = getArguments().getInt(BrowsingActivity.KEY_NUMBER_OF_BOOKS_TO_DONLOAD);
        builder
                .setMessage(
                        getContext()
                                .getResources()
                                .getQuantityString(R.plurals.confirm_book_download,
                                        booksCount,
                                        booksCount)
                )
                .setPositiveButton(R.string.confirm_download,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dismiss();
                                mListener.onDialogPositiveClick();

                            }
                        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BatchDownloadConfirmationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BatchDownloadConfirmationListener");
        }
    }


    public interface BatchDownloadConfirmationListener {
        void onDialogPositiveClick();
    }


}
