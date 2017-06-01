package com.fekracomputers.islamiclibrary.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.download.downloader.BooksDownloader;

/**
 * Created by Mohammad Yahia on 02/11/2016.
 */

public class DownloadBookInformationDialog extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_information_database_not_available);

        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BooksDownloader booksDownloader = new BooksDownloader(getContext());

                //TODO shared preference
                booksDownloader.DownloadBookInformationDatabase(true);
                Toast.makeText(getContext(), R.string.wait_for_download, Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
}
