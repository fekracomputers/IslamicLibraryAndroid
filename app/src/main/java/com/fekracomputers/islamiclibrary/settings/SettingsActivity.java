package com.fekracomputers.islamiclibrary.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.XpPreferenceManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.BuildConfig;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.SplashActivity;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;
import com.fekracomputers.islamiclibrary.utility.PermissionUtil;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;
import com.fekracomputers.islamiclibrary.utility.Util;

import net.xpece.android.support.preference.ColorPreference;
import net.xpece.android.support.preference.PreferenceScreenNavigationStrategy;
import net.xpece.android.support.preference.XpColorPreferenceDialogFragment;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p></p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback,
        PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback,
        FragmentManager.OnBackStackChangedListener,
        PreferenceScreenNavigationStrategy.ReplaceFragment.Callbacks {

    public static final String KEY_KILL_APP="intent_key_kill_app" ;
    Toolbar mToolbar;
    TextSwitcher mTitleSwitcher;

    private CharSequence mTitle;

    private SettingsFragment mSettingsFragment;

    private PreferenceScreenNavigationStrategy.ReplaceFragment mReplaceFragmentStrategy;
    private static final String SI_LOCATION_TO_WRITE = "SI_LOCATION_TO_WRITE";
    private static final int REQUEST_WRITE_TO_SDCARD_PERMISSION = 1;

    @Nullable
    private String mNewLocation;
    private String mOldLocation;
    private int restartOnBack = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);

        // Enable if you use AppCompat 24.1.x.
//        Fixes.updateLayoutInflaterFactory(getLayoutInflater());

        setContentView(R.layout.activity_settings);

        mReplaceFragmentStrategy = new PreferenceScreenNavigationStrategy
                .ReplaceFragment(this,
                R.anim.abc_fade_in,
                R.anim.abc_fade_out,
                R.anim.abc_fade_in,
                R.anim.abc_fade_out);

        if (savedInstanceState == null) {
            mSettingsFragment = SettingsFragment.newInstance(null);
            getSupportFragmentManager().beginTransaction().add(R.id.content, mSettingsFragment, "Settings").commit();
        } else {
            mSettingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("Settings");
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();


        // Cross-fading title setup.
        mTitle = getTitle();

        mTitleSwitcher = new TextSwitcher(mToolbar.getContext());
        mTitleSwitcher.setFactory(() -> {
            TextView tv = new AppCompatTextView(mToolbar.getContext());
            TextViewCompat.setTextAppearance(tv, R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
            return tv;
        });
        mTitleSwitcher.setCurrentText(mTitle);
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setCustomView(mTitleSwitcher);
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }


        // Add to hierarchy before accessing layout params.
        int margin = Util.dpToPxOffset(this, 16);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mTitleSwitcher.getLayoutParams();
        lp.leftMargin = margin;
        lp.rightMargin = margin;

        mTitleSwitcher.setInAnimation(this, R.anim.abc_fade_in);
        mTitleSwitcher.setOutAnimation(this, R.anim.abc_fade_out);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);

        if (!mTitle.equals(title)) {
            mTitle = title;

            // Only switch if the title differs. Used for the first hook.
            mTitleSwitcher.setText(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            case R.id.reset: {
                final Context context = this;
                final String[] customPackages = {BuildConfig.APPLICATION_ID};
                XpPreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
                XpPreferenceManager.setDefaultValues(context, R.xml.pref_general, true, customPackages);
                XpPreferenceManager.setDefaultValues(context, R.xml.pref_notification, true, customPackages);
                XpPreferenceManager.setDefaultValues(context, R.xml.pref_data_sync, true, customPackages);
                mSettingsFragment = SettingsFragment.newInstance(null);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                        .replace(R.id.content, mSettingsFragment, "Settings")
                        .commit();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceStartScreen(@NonNull final PreferenceFragmentCompat preferenceFragmentCompat, @NonNull final PreferenceScreen preferenceScreen) {
        mReplaceFragmentStrategy.onPreferenceStartScreen(getSupportFragmentManager(), preferenceFragmentCompat, preferenceScreen);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        mSettingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("Settings");
    }

    @Override
    public PreferenceFragmentCompat onBuildPreferenceFragment(final String rootKey) {
        return SettingsFragment.newInstance(rootKey);
    }

    @Override
    public boolean onPreferenceDisplayDialog(PreferenceFragmentCompat preferenceFragmentCompat, @NonNull Preference preference) {
        final String key = preference.getKey();
        DialogFragment f;
        if (preference instanceof ColorPreference) {
            f = XpColorPreferenceDialogFragment.newInstance(key);
        } else {
            return false;
        }

        f.setTargetFragment(preferenceFragmentCompat, 0);
        f.show(this.getSupportFragmentManager(), key);
        return true;
    }

    public void restartActivity() {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, true);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void requestWriteExternalSdcardPermission(String newLocation, String oldLocation) {
        if (PermissionUtil.canRequestWriteExternalStoragePermission(this)) {
            StorageUtils.setSdcardPermissionsDialogPresented(this);

            mNewLocation = newLocation;
            mOldLocation = oldLocation;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_TO_SDCARD_PERMISSION);
        } else {
            // in the future, we should make this a direct link - perhaps using a Snackbar.
            Toast.makeText(this, R.string.please_grant_permissions, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_TO_SDCARD_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mNewLocation != null) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                    if (fragment instanceof SettingsFragment) {
                        ((SettingsFragment) fragment).handleMove(mNewLocation, mOldLocation);
                    }

                }
            }
            mNewLocation = null;
        }
    }

    public static final int RESTART_ON_BACK = 1;
    public static final int EXIT_ON_BACK = 2;

    public void setRestartOnBack(int i) {
        this.restartOnBack = i;
    }

    @Override
    public void onBackPressed() {
        if (restartOnBack > 0) {
            if (restartOnBack == RESTART_ON_BACK) {
                Intent intent = new Intent(this, BrowsingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (restartOnBack == EXIT_ON_BACK) {
                Intent intent = new Intent(this, SplashActivity.class);
                intent.putExtra(KEY_KILL_APP,true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }


        } else

        {
            super.onBackPressed();
        }
    }
}