package com.fekracomputers.islamiclibrary.browsing.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 16/5/2017.
 */
public class ConfirmBookDeleteDialogFragment extends DialogFragment {

    public static final String KEY_NUMBER_OF_BOOKS_TO_DELETE = "KEY_NUMBER_OF_BOOKS_TO_DELETE";
    // Use this instance of the interface to deliver action events
    BookDeleteDialogListener mListener;


    public ConfirmBookDeleteDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        int booksCount = getArguments().getInt(KEY_NUMBER_OF_BOOKS_TO_DELETE);
        final int bookId=getArguments().getInt(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID);
        String bookName = getArguments().getString(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE);
        if (booksCount > 1) {
            builder
                    .setMessage(
                            getContext()
                                    .getResources()
                                    .getQuantityString(R.plurals.confirm_book_delete,
                                            booksCount,
                                            booksCount)
                    );

        } else {
            builder
                    .setMessage(
                            getContext()
                                    .getResources()
                                    .getString(R.string.confirm_book_delete,
                                            bookName
                                    )
                    );
            builder.setPositiveButton(R.string.confirm_delete,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                            mListener.onBookDeleteDialogDialogPositiveClick(bookId);

                        }
                    });
        }

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
            mListener = (BookDeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BookDeleteDialogListener");
        }
    }


    public interface BookDeleteDialogListener {
        void onBookDeleteDialogDialogPositiveClick(int bookId);
    }


}
