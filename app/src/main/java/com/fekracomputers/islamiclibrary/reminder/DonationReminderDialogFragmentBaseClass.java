package com.fekracomputers.islamiclibrary.reminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;

public class DonationReminderDialogFragmentBaseClass extends DialogFragment {
    DonationReminderDialogFragmentListener mListener;

    public void setUpBottomBar(View root) {
        root.findViewById(R.id.btn_rate).setOnClickListener(v -> {
            mListener.onDonationReminderDialogFragmentRateClick();
            dismiss();
        });
        root.findViewById(R.id.btn_donate).setOnClickListener(v -> {
            mListener.onDonationReminderDialogFragmentDonateClick();
            dismiss();
        });
        root.findViewById(R.id.btn_paid_version).setOnClickListener(v -> {
            mListener.onDonationReminderDialogFragmentBuyPaidVersion();
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = ((DonationReminderDialogFragmentDelegate) context).getListener();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DonationReminderDialogFragmentDelegate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DonationReminderDialogFragmentDelegate {
        DonationReminderDialogFragmentListener getListener();
    }

    public interface DonationReminderDialogFragmentListener {
        void onDonationReminderDialogFragmentRateClick();

        void onDonationReminderDialogFragmentDonateClick();

        void onDonationReminderDialogFragmentDontShowAgainClick();

        void onDonationReminderDialogFragmentBuyPaidVersion();
    }
}
