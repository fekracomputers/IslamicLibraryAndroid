package com.fekracomputers.islamiclibrary.reminder;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.fekracomputers.islamiclibrary.settings.AboutUtil;

import java.util.Date;

public final class AppRateController {

    private static final boolean IS_DEBUG = false;
    private final Context context;
    private int installDate = 5;
    private int launchTimes = 10;
    private int remindInterval = 30;

    public AppRateController(Context context) {
        this.context = context;
    }

    private static boolean isOverDate(long targetDate, int days) {
        return new Date().getTime() - targetDate >= days * 24 * 60 * 60 * 1000;
    }

    public void showRateDialogIfMeetsConditions(AppCompatActivity activity) {
        if (shouldChangeLogDialog()) {
            showChangeLogDialog(activity);
        } else if (IS_DEBUG || shouldShowRateDialog()) {
            showRateDialog(activity);

        }
    }

    public AppRateController setLaunchTimes(int launchTimes) {
        this.launchTimes = launchTimes;
        return this;
    }

    public AppRateController setInstallDays(int installDate) {
        this.installDate = installDate;
        return this;
    }

    public AppRateController setRemindInterval(int remindInterval) {
        this.remindInterval = remindInterval;
        return this;
    }

    public AppRateController clearAgreeShowDialog() {
        PreferenceHelper.setAgreeShowDialog(context, true);
        return this;
    }

    public AppRateController clearSettingsParam() {
        PreferenceHelper.setAgreeShowDialog(context, true);
        PreferenceHelper.clearSharedPreferences(context);
        return this;
    }

    public AppRateController setAgreeShowDialog(boolean clear, Context context) {
        PreferenceHelper.setAgreeShowDialog(context, clear);
        return this;
    }


    public AppRateController monitor() {
        if (PreferenceHelper.isFirstLaunch(context)) {
            PreferenceHelper.setInstallDate(context);
        }
        PreferenceHelper.incrementLaunchTimes(context);

        return this;
    }

    public void showRateDialog(AppCompatActivity activity) {
        if (!activity.isFinishing()) {
            DialogFragment DonationReminderDialogFragment = new DonationReminderDialogFragment();
            DonationReminderDialogFragment.show(activity.getSupportFragmentManager(), "DonationReminderDialogFragment");
            PreferenceHelper.resetLaunchTimes(activity);
            PreferenceHelper.reSetLastRemind(activity);
        }
    }

    public void showChangeLogDialog(AppCompatActivity activity) {
        if (!activity.isFinishing()) {
            DialogFragment donationReminderDialogFragment = new ChangeLogDialogFragment();
            donationReminderDialogFragment.show(activity.getSupportFragmentManager(), "donationReminderDialogFragment");
            PreferenceHelper.updateLastViewdChangeLog(activity);
        }
    }

    public boolean shouldChangeLogDialog() {
        //evaluate if we will show changelog
        return PreferenceHelper.isFirstLaunchAfterUpdate(context);
    }


    public boolean shouldShowRateDialog() {
        return PreferenceHelper
                .getIsAgreeShowDialog(context) &&
                isOverLaunchTimes() &&
                isOverInstallDate() &&
                isOverRemindDate()
                && !AboutUtil.isPro(context);
    }

    private boolean isOverLaunchTimes() {
        return PreferenceHelper.getLaunchTimes(context) >= launchTimes;
    }

    private boolean isOverInstallDate() {
        return isOverDate(PreferenceHelper.getInstallDate(context), installDate);
    }

    private boolean isOverRemindDate() {
        return isOverDate(PreferenceHelper.getRemindInterval(context), remindInterval);
    }


    public DonationReminderDialogFragmentBaseClass.DonationReminderDialogFragmentListener getListener() {
        return new DonationReminderDialogFragmentBaseClass.DonationReminderDialogFragmentListener() {
            @Override
            public void onDonationReminderDialogFragmentRateClick() {
                AboutUtil.rateApp(context);
            }

            @Override
            public void onDonationReminderDialogFragmentDonateClick() {
                AboutUtil.pay(context);
            }

            @Override
            public void onDonationReminderDialogFragmentDontShowAgainClick() {
                PreferenceHelper.setAgreeShowDialog(context,false);

            }

            @Override
            public void onDonationReminderDialogFragmentBuyPaidVersion() {
                AboutUtil.buyPaidVersion(context);
            }
        };
    }
}
