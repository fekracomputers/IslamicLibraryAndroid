package com.fekracomputers.islamiclibrary.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.utility.Util;


public class AboutActivity extends MaterialAboutActivity {


    private boolean mIsArabic;


    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull final Context context) {
        mIsArabic = Util.isArabicUi(this);
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        MaterialAboutCard.Builder generalInfoCardBuilder = new MaterialAboutCard.Builder();

        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo = getApplicationInfo();
        Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);

        PackageInfo pInfo;
        String versionName = "1.0";
        int versionCode = 1;

        try {
            pInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        generalInfoCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(getString(R.string.app_name))
                .icon(applicationIcon)
                .build());


        generalInfoCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_version_number)
                .subText(versionName + " (" + versionCode + ")")
                .build());

        MaterialAboutCard.Builder supportCardBuilder = new MaterialAboutCard.Builder();
        supportCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.send_feedback_two)
                .icon(R.drawable.ic_feedback_black_24dp)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        AboutUtil.sendFeedBack(context);

                    }
                })
                .build());


        MaterialAboutCard.Builder shareCardBuilder = new MaterialAboutCard.Builder();
        shareCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.action_share)
                .icon(R.drawable.ic_share_black_24dp)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        AboutUtil.ShareAppLink(context);
                    }
                })
                .build());
        shareCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.action_rate)
                .icon(R.drawable.ic_star_black_24dp)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        AboutUtil.rateApp(context);
                    }
                })
                .build());


        MaterialAboutCard.Builder aboutCardBuilder = new MaterialAboutCard.Builder();
        aboutCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.other_apps)
                .icon(R.drawable.ic_collections_bookmark_black_24dp)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        AboutUtil.openDevelopersPage(context);

                    }
                })
                .build());
        aboutCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_company)
                .icon(R.drawable.ic_info_black_24dp)
                .setOnClickAction(AboutUtil.webViewDialog(context,
                        "file:///android_asset/aboutFekra.html", R.string.about_company))
                .build());


        MaterialAboutCard.Builder socialNetworksCardBuilder = new MaterialAboutCard.Builder();
        socialNetworksCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.find_us_on_social_media)
                .build());


        socialNetworksCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_facebook_label)
                .subText(R.string.facebookUserName)
                .icon(R.drawable.ic_facebook_logo)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        AboutUtil.getOpenFacebookIntent(context,
                                getString(R.string.facebookUserName));

                    }
                })
                .build());
        socialNetworksCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_twitter_label)
                .subText(R.string.twitter_user_name)
                .icon(R.drawable.ic_twitter_logo_blue)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        AboutUtil.startTwitter(context,
                                getString(R.string.twitter_user_name));

                    }
                })
                .build());

        socialNetworksCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_web_page_label)
                .subText(R.string.web_page)
                .icon(R.drawable.ic_public_black_24dp)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.web_page))));
                    }
                })
                .build());


        MaterialAboutCard.Builder legalCardBuilder = new MaterialAboutCard.Builder();
        legalCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.open_source_licences)
                .icon(R.drawable.ic_insert_drive_file_black_24dp)
                .setOnClickAction(AboutUtil.webViewDialog(context,
                        "file:///android_asset/licenses.html",
                        R.string.open_source_licences))
                .build());
        legalCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_acknowledgements)
                .icon(R.drawable.ic_info_black_24dp)
                .setOnClickAction(AboutUtil.webViewDialog(context,
                        "file:///android_asset/acknowledgements.html",
                        R.string.about_activity_acknowledgements))
                .build());


        return new MaterialAboutList.Builder()
                .addCard(generalInfoCardBuilder.build())
                .addCard(supportCardBuilder.build())
                .addCard(shareCardBuilder.build())
                .addCard(aboutCardBuilder.build())
                .addCard(socialNetworksCardBuilder.build())
                .addCard(legalCardBuilder.build())
                .build();
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.About);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restartIfLocaleChanged(this, mIsArabic);

    }


}