package com.fekracomputers.islamiclibrary.reminder;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;

/**
 * Example with Dialog
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class ChangeLogDialogFragment extends DonationReminderDialogFragmentBaseClass {
    DonationReminderDialogFragmentBaseClass.DonationReminderDialogFragmentListener mListener;

    public ChangeLogDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View chgList = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_log, null);
        setUpBottomBar(chgList);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setView(chgList)
                .setTitle(R.string.change_log)
                .create();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;

    }


}