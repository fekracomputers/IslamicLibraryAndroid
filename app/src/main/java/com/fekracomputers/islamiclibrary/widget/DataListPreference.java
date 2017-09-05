package com.fekracomputers.islamiclibrary.widget;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Here we show storage title and free space amount (currently, in MB) in summary.
 * However, `ListPreference` does not provide summary text out of the box, and thus
 * we use a custom layout with two `TextView`s, for a title and a summary,
 * and a `CheckedTextView` for a radio-button.
 * We remove the `CheckedTextView`'s title during runtime and use one of the
 * `TextView`s instead to represent the title.
 * <p>
 * Also, we extend from `QuranListPreference` in order not to duplicate code for
 * setting dialog title color.
 */
public class DataListPreference extends ListPreference implements DialogInterface.OnClickListener {
    private List<StorageUtils.Storage> storageList;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DataListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DataListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DataListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataListPreference(Context context) {
        super(context);
    }


    public List<StorageUtils.Storage> getStorageList() {
        return storageList;
    }


    public void setLabelsAndSummaries(Context context, int appSize,
                                      List<StorageUtils.Storage> storageList) {
        String summary = context.getString(R.string.prefs_app_location_summary) + "\n"
                + context.getString(R.string.prefs_app_size) + " " +
                context.getString(R.string.prefs_megabytes_int, appSize);
        setSummary(summary);

        CharSequence[] values = new CharSequence[storageList.size()];
        CharSequence[] displayNames = new CharSequence[storageList.size()];
        this.storageList=storageList;
        StorageUtils.Storage storage;
        for (int i = 0; i < storageList.size(); i++) {
            storage = storageList.get(i);
            values[i] = storage.getMountPoint();
            displayNames[i] = storage.getLabel();

        }
        setEntries(displayNames);
        setEntryValues(values);
        String current = StorageUtils.getAppCustomLocation(context);
        if (TextUtils.isEmpty(current)) {
            current = values[0].toString();
        }
        setValue(current);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {

    }


}
