package com.fekracomputers.islamiclibrary.reminder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;

public class DonationReminderDialogFragment extends DonationReminderDialogFragmentBaseClass {

    public DonationReminderDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Context context = getContext();
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_donate_reminder, null);
        setUpBottomBar(root);
        return new android.support.v7.app.AlertDialog.Builder(context)
                //.setMessage(R.string.support_notice)
                .setPositiveButton(R.string.action_never_show,
                        (dialog, id) -> {
                            mListener.onDonationReminderDialogFragmentDontShowAgainClick();
                            dismiss();
                        })
                .setNeutralButton(R.string.action_show_later, (dialog, id) -> dismiss())
                .setView(root)
                .create();

    }

}