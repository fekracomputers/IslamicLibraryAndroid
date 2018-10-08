package com.fekracomputers.islamiclibrary.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.utility.Util;


public class HelpActivity extends AppCompatActivity {
    private boolean mIsArabic;

    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setSupportActionBar(findViewById(R.id.toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.action_help);
        }

        mIsArabic = Util.isArabicUi(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restartIfLocaleChanged(this, mIsArabic);
    }


    public void showHelpVideo(View view) {
        String id = "";
        switch (view.getId()) {
            case R.id.btn_explain_video:
                id = "playlist?list=PL9gaYRJUzld1UhUzTLccEQI-8qdz_pGKQ";
                break;
            case R.id.btn_vid_1:
                id = "watch?v=cRe5qKWWMW8&list=PL9gaYRJUzld1UhUzTLccEQI-8qdz_pGKQ&index=1";
                break;
            case R.id.btn_vid_2:
                id = "watch?v=SO1YZcZZYYg&list=PL9gaYRJUzld1UhUzTLccEQI-8qdz_pGKQ&index=2";
                break;
            case R.id.btn_vid_3:
                id = "watch?v=SHeNOG73DEM&list=PL9gaYRJUzld1UhUzTLccEQI-8qdz_pGKQ&index=3";
                break;
        }
        AboutUtil.ShowHelpVideos(this, id);
    }
}