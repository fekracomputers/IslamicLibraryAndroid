package com.fekracomputers.islamiclibrary.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.utility.Util;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.SharedPreferences;

public class AboutActivity extends AppCompatActivity {


    private boolean mIsArabic;

    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupDarkModeToggle();
        setSupportActionBar(findViewById(R.id.toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.About);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.material_about_fragment_container, new MaterialAboutFragmentImplementation()).commit();

        mIsArabic = Util.isArabicUi(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restartIfLocaleChanged(this, mIsArabic);
    }


// إضافة زر الوضع المظلم
private void setupDarkModeToggle() {
    SwitchCompat darkModeSwitch = findViewById(R.id.dark_mode_switch);
    
    // تحميل الإعدادات الحالية
    SharedPreferences prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);
    boolean isDarkMode = prefs.getBoolean("DARK_MODE", false);
    darkModeSwitch.setChecked(isDarkMode);

    // عند تغيير وضع الزر
    darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("DARK_MODE", isChecked);
        editor.apply();
        
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate(); // إعادة تحميل النشاط
    });
}
}
