package com.fekracomputers.islamiclibrary.download.dialog;

import android.app.Dialog;
import android.content.Context;
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
public class CancelDownloadDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    CancelDownloadDialogFragmentListener mListener;


    public CancelDownloadDialogFragment() {

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
                .setPositiveButton(R.string.stop_downloading,
                        (dialog, id) -> {
                            mListener.onCancelAllDialogPositiveClick();
                            dismiss();
                        })
                .setNegativeButton(R.string.continue_downloading, (dialog, id) -> dismiss());

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CancelDownloadDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CancelDownloadDialogFragmentListener");
        }
    }


    public interface CancelDownloadDialogFragmentListener {
        void onCancelAllDialogPositiveClick();
    }


}
