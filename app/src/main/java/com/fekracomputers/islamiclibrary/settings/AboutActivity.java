package com.fekracomputers.islamiclibrary.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.utility.Util;


public class AboutActivity extends AppCompatActivity {


    private boolean mIsArabic;

    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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


}