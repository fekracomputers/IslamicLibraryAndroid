package com.fekracomputers.islamiclibrary.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.XpPreferenceFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.BuildConfig;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants;
import com.fekracomputers.islamiclibrary.utility.PermissionUtil;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;
import com.fekracomputers.islamiclibrary.widget.DataListPreference;
import com.fekracomputers.islamiclibrary.widget.DataListPreferenceDialogFragmentCompat;

import net.xpece.android.support.preference.ColorPreference;
import net.xpece.android.support.preference.ListPreference;
import net.xpece.android.support.preference.MultiSelectListPreference;
import net.xpece.android.support.preference.PreferenceCategory;
import net.xpece.android.support.preference.PreferenceDividerDecoration;
import net.xpece.android.support.preference.PreferenceScreenNavigationStrategy;
import net.xpece.android.support.preference.RingtonePreference;
import net.xpece.android.support.preference.SeekBarPreference;
import net.xpece.android.support.preference.SharedPreferencesCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * @author Eugen on 7. 12. 2015.
 */
public class SettingsFragment extends XpPreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_DISPLAY_TEXT_SIZE = "global_display_text_size";
    public static final String KEY_GLOBAL_DISPLAY_OVERRIDES_LOCAL = "global_overrides_local";
    public static final String KEY_GLOBAL_THEME_COLOR = "global_theme_color";
    public static final String KEY_IS_THEME_NIGHT_MODE = "global_night_mode";
    public static final String KEY_UI_LANG_ARABIC = "ui_lang_arabic";
    public static final String PREF_USE_VOLUME_KEY_NAV = "volumeKeyNavigation";
    public static final String KEY_IS_TASHKEEL_ON = "tashkeel_on";
    private static final String TAG = SettingsFragment.class.getSimpleName();
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof SeekBarPreference) {
            SeekBarPreference pref = (SeekBarPreference) preference;
            int progress = (int) value;
            pref.setInfo(progress + "%");
        } else if (preference instanceof ColorPreference) {
            ColorPreference colorPreference = (ColorPreference) preference;
            int color = (int) value;
//                String colorString = String.format("#%06X", 0xFFFFFF & color);
//                preference.setSummary(colorString);
            int index = colorPreference.findIndexOfValue(color);
            if (index < 0) {
                preference.setSummary(null);
            } else {
                final CharSequence name = colorPreference.getNameForColor(color);
                preference.setSummary(name);
            }
        } else if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
        } else if (preference instanceof MultiSelectListPreference) {
            String summary = stringValue.trim().substring(1, stringValue.length() - 1); // strip []
            preference.setSummary(summary);
        } else if (preference instanceof RingtonePreference) {
            // For ringtone preferences, look up the correct display value
            // using RingtoneManager.
            if (TextUtils.isEmpty(stringValue)) {
                // Empty values correspond to 'silent' (no ringtone).
                preference.setSummary(R.string.pref_ringtone_silent);

            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(null);
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            }

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };
    private DataListPreference listStoragePref;
    private MoveFilesAsyncTask moveFilesTask;
    private List<StorageUtils.Storage> storageList;
    private LoadStorageOptionsTask loadStorageOptionsTask;
    private int appSize;
    private String internalSdcardLocation;
    private AlertDialog dialog;
    private boolean isPaused;

    public static SettingsFragment newInstance(String rootKey) {
        Bundle args = new Bundle();
        args.putString(SettingsFragment.ARG_PREFERENCE_ROOT, rootKey);
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        final String key = preference.getKey();
        if (preference instanceof MultiSelectListPreference) {
            Set<String> summary = SharedPreferencesCompat.getStringSet(
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()),
                    key,
                    new HashSet<>());
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, summary);
        } else if (preference instanceof ColorPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, ((ColorPreference) preference).getColor());
        } else if (preference instanceof SeekBarPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, ((SeekBarPreference) preference).getValue());
        } else {
            String value = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(key, "");
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
        }
    }

    @Override
    public String[] getCustomDefaultPackages() {
        return new String[]{BuildConfig.APPLICATION_ID};
    }


    @Override
    public void onCreatePreferences2(final Bundle savedInstanceState, final String rootKey) {
        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);
        prepareStorageLst();
        // Add 'global_display' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(getPreferenceManager().getContext());
        //fakeHeader.setLayoutResource(R.layout.fake_pref_header);
        fakeHeader.setTitle(R.string.pref_header_global_display);
        getPreferenceScreen().addPreference(fakeHeader);


        addPreferencesFromResource(R.xml.pref_page_diplay);

        // Setup SeekBarPreference "info" text field.
        final SeekBarPreference seekBarPreference = (SeekBarPreference) findPreference(KEY_DISPLAY_TEXT_SIZE);
        seekBarPreference.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarPreference.setInfo(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        bindPreferenceSummaryToValue(findPreference(KEY_GLOBAL_THEME_COLOR));


//
//        // Add 'notifications' preferences, and a corresponding header.
//        PreferenceCategory fakeHeader = new PreferenceCategory(getPreferenceManager().getContext());
//        fakeHeader.setTitle(R.string.pref_header_notifications);
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_notification);
//
//        // Add 'data and sync' preferences, and a corresponding header.
//        fakeHeader = new PreferenceCategory(getPreferenceManager().getContext());
//        fakeHeader.setTitle(R.string.pref_header_data_sync);
//        fakeHeader.setTitleTextAppearance(R.style.TextAppearance_AppCompat_Button);
//        fakeHeader.setTitleTextColor(ContextCompat.getColor(fakeHeader.getContext(), R.color.primary)); // No disabled color state please.
//        getPreferenceScreen().addPreference(fakeHeader);
//        addPreferencesFromResource(R.xml.pref_data_sync);
//
//        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
//        // their values. When their values change, their summaries are updated
//        // to reflect the new value, per the Android Design guidelines.
//        bindPreferenceSummaryToValue(findPreference("example_text"));
//        bindPreferenceSummaryToValue(findPreference("example_list"));
//        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        bindPreferenceSummaryToValue(findPreference("notif_content"));
//        bindPreferenceSummaryToValue(findPreference("notif_color"));
//
//        // Setup SeekBarPreference "info" text field.
//        final SeekBarPreference volume2 = (SeekBarPreference) findPreference("notifications_new_message_volume2");
//        volume2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                volume2.setInfo(progress + "%");
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        // Setup EditTextPreference input field.
//        ((EditTextPreference) findPreference("example_text")).setOnEditTextCreatedListener(new EditTextPreference.OnEditTextCreatedListener() {
//            @Override
//            public void onEditTextCreated(EditText edit) {
//                Context context = edit.getContext();
//                Drawable d = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_create_black_24dp);
//                d = DrawableCompat.wrap(d);
//                DrawableCompat.setTintList(d, Util.resolveColorStateList(context, R.attr.colorControlNormal));
//                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(edit, null, null, d, null);
//
//                // These are inflated from XML. Undocumented API.
////                edit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
////                edit.setSingleLine(true);
////                edit.setSelectAllOnFocus(true);
//            }
//        });
//
//        // Setup an OnPreferenceLongClickListener via XpPreferenceHelpers.
//        XpPreferenceHelpers.setOnPreferenceLongClickListener(findPreference("example_text"), new OnPreferenceLongClickListener() {
//            @Override
//            public boolean onLongClick(Preference preference, View view) {
//                final Toast toast = Toast.makeText(getContext(), "This showcases long click listeners on preferences.", Toast.LENGTH_SHORT);
//                toast.show();
//                return true;
//            }
//        });

        // Setup root preference title.
        getPreferenceScreen().setTitle(getActivity().getTitle());

        // Setup root preference.
        // Use with ReplaceFragment strategy.
        PreferenceScreenNavigationStrategy.ReplaceFragment.onCreatePreferences(this, rootKey);
    }

    private void prepareStorageLst() {
        internalSdcardLocation =
                Environment.getExternalStorageDirectory().getAbsolutePath();

        listStoragePref = (DataListPreference) findPreference(DownloadFileConstants.PREF_APP_LOCATION);
        listStoragePref.setEnabled(false);

        try {
            storageList = StorageUtils.getAllStorageLocations(getContext().getApplicationContext());
        } catch (Exception e) {
            Timber.d(e, "Exception while trying to get storage locations");
            storageList = new ArrayList<>();
        }

        // Hide app location pref if there is no storage option
        // except for the normal Environment.getExternalStorageDirectory
        if (storageList == null || storageList.size() <= 1) {
            Timber.d("removing advanced settings from preferences");
            hideStorageListPref();
        } else {
            loadStorageOptionsTask = new LoadStorageOptionsTask(getContext());
            loadStorageOptionsTask.execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        mPreferenceScreenNavigation.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Change activity title to preference title. Used with ReplaceFragment strategy.
        getActivity().setTitle(getPreferenceScreen().getTitle());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView listView = getListView();

        // We're using alternative divider.
        listView.addItemDecoration(new PreferenceDividerDecoration(getContext()).drawBottom(true).drawBetweenCategories(true));
        setDivider(null);

        // We don't want this. The children are still focusable.
        listView.setFocusable(false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_UI_LANG_ARABIC)) {
            final Context context = getActivity();
            if (context instanceof SettingsActivity) {
                ((SettingsActivity) context).restartActivity();
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment fragment;
        if (preference instanceof DataListPreference) {
            fragment = DataListPreferenceDialogFragmentCompat.newInstance(preference);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(),
                    "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else super.onDisplayPreferenceDialog(preference);
    }

    public void moveFiles(String newLocation, boolean automatic) {
        moveFilesTask = new MoveFilesAsyncTask(getActivity(), newLocation,automatic);
        moveFilesTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        isPaused = true;
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void loadStorageOptions(final Context context) {
        try {
            if (appSize == -1) {
                // sdcard is not mounted...
                hideStorageListPref();
                return;
            }

            listStoragePref.setLabelsAndSummaries(context, appSize, storageList);
            final HashMap<String, StorageUtils.Storage> storageMap =
                    new HashMap<>(storageList.size());
            for (StorageUtils.Storage storage : storageList) {
                storageMap.put(storage.getMountPoint(), storage);
            }

            listStoragePref
                    .setOnPreferenceChangeListener((preference, newValue) -> {
                        final Context context1 = SettingsFragment.this.getActivity();

                        if (TextUtils.isEmpty(StorageUtils.getAppCustomLocation(context)) &&
                                Environment.getExternalStorageDirectory().equals(newValue)) {
                            // do nothing since we're moving from empty settings to
                            // the default sdcard setting, which are the same, but write it.
                            return false;
                        }

                        // this is called right before the preference is saved
                        String newLocation = (String) newValue;
                        StorageUtils.Storage destStorage = storageMap.get(newLocation);
                        String current = StorageUtils.getAppCustomLocation(context);
                        if (appSize < destStorage.getFreeSpace()) {
                            if (current == null || !current.equals(newLocation)) {
                                if (destStorage.doesRequirePermission()) {
                                    if (!PermissionUtil.haveWriteExternalStoragePermission(context1)) {
                                        SettingsFragment.this.requestExternalStoragePermission(newLocation, current);
                                        return false;
                                    }

                                    // we have the permission, so fall through and handle the move
                                }
                                SettingsFragment.this.handleMove(newLocation, current);
                            }
                        } else {
                            Toast.makeText(context1,
                                    SettingsFragment.this.getString(
                                            R.string.prefs_no_enough_space_to_move_files),
                                    Toast.LENGTH_LONG).show();
                        }
                        // this says, "don't write the preference"
                        return false;
                    });
            listStoragePref.setEnabled(true);
        } catch (Exception e) {
            Timber.e(e, "error loading storage options");
            hideStorageListPref();
        }
    }

    private void requestExternalStoragePermission(String newLocation, String oldLocation) {
        Activity activity = getActivity();
        if (activity instanceof SettingsActivity) {
            ((SettingsActivity) activity)
                    .requestWriteExternalSdcardPermission(newLocation, oldLocation);
        }
    }

    public void handleMove(final String newLocation, String current) {

        final Context context = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(getString(R.string.external_move_choice, current, newLocation))
                .setPositiveButton(R.string.external_move_automatic, (currentDialog, which) -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ||
                            newLocation.equals(internalSdcardLocation)) {
                        moveFiles(newLocation, true);
                    } else {
                        showKitKatConfirmation(newLocation);
                    }
                })
                .setNegativeButton(R.string.external_move_manually, (currentDialog, which) -> {
                    moveFiles(newLocation, false);
                    currentDialog.dismiss();
                    SettingsFragment.this.dialog = null;
                });
        dialog = builder.create();
        dialog.show();
    }

    private void showKitKatConfirmation(final String newLocation) {
        final Context context = getActivity();
        final AlertDialog.Builder b = new AlertDialog.Builder(context)
                .setTitle(R.string.warning)
                .setMessage(R.string.kitkat_external_message)
                .setPositiveButton(R.string.ok, (currentDialog, which) -> {
                    moveFiles(newLocation, true);
                    currentDialog.dismiss();
                    SettingsFragment.this.dialog = null;
                })
                .setNegativeButton(R.string.cancel, (currentDialog, which) -> {
                    currentDialog.dismiss();
                    SettingsFragment.this.dialog = null;
                });
        dialog = b.create();
        dialog.show();
    }

    private void removeAdvancePreference(Preference preference) {
        // these null checks are to fix a crash due to an NPE on 4.4.4
        if (preference != null) {
            PreferenceGroup group =
                    (PreferenceGroup) findPreference("pref_general");
            if (group != null) {
                group.removePreference(preference);
            }
        }
    }

    private void hideStorageListPref() {
        removeAdvancePreference(listStoragePref);
    }

    private class MoveFilesAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private String newLocation;
        private ProgressDialog dialog;
        private Context appContext;
        private boolean automatic;

        private MoveFilesAsyncTask(Context context, String newLocation, boolean automatic) {
            this.newLocation = newLocation;
            this.appContext = context.getApplicationContext();
            this.automatic = automatic;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(appContext.getString(R.string.prefs_copying_app_files));

            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return StorageUtils.moveAppFiles(appContext, newLocation, automatic);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!isPaused) {
                dialog.dismiss();
                if (result) {
                    //StorageUtils.setAppCustomLocation(newLocation, appContext);
                    if (listStoragePref != null) {
                        listStoragePref.setValue(newLocation);
                    }
                    BooksInformationDbHelper.clearInstance(getContext());
                    ((SettingsActivity) getContext()).setRestartOnBack(automatic ?
                            SettingsActivity.RESTART_ON_BACK :
                            SettingsActivity.EXIT_ON_BACK);
                    if (!automatic)
                        Toast.makeText(appContext,
                                getString(R.string.press_back_then_move_files_using_system_file_manager),
                                Toast.LENGTH_LONG)
                                .show();

                } else {
                    Toast.makeText(appContext,
                            getString(R.string.prefs_err_moving_app_files),
                            Toast.LENGTH_LONG)
                            .show();
                }
                dialog = null;
                moveFilesTask = null;
            }
        }
    }

    private class LoadStorageOptionsTask extends AsyncTask<Void, Void, Void> {

        private Context appContext;

        LoadStorageOptionsTask(Context context) {
            this.appContext = context.getApplicationContext();
        }

        @Override
        protected void onPreExecute() {
            listStoragePref.setSummary(R.string.prefs_calculating_app_size);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appSize = StorageUtils.getAppUsedSpace(appContext);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!isPaused) {
                loadStorageOptions(appContext);
                loadStorageOptionsTask = null;
                listStoragePref.setSummary(R.string.prefs_app_location_summary);
            }
        }
    }


}