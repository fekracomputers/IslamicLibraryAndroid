package com.fekracomputers.islamiclibrary.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.danielstone.materialaboutlibrary.MaterialAboutFragment;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.fekracomputers.islamiclibrary.R;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import timber.log.Timber;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 24/7/2017.
 */
public class MaterialAboutFragmentImplementation extends MaterialAboutFragment {
    public MaterialAboutFragmentImplementation() {

    }

    @Override
    protected int getTheme() {
        return R.style.AppTheme_MaterialAboutActivity_Fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull final Context context) {
        MaterialAboutCard.Builder generalInfoCardBuilder = new MaterialAboutCard.Builder();

        //region generalInfoCardBuilder
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);

        PackageInfo pInfo;
        String versionName = "1.0";
        int versionCode = 1;

        try {
            pInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }


        generalInfoCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(context.getResources().getString(R.string.app_name))
                .icon(applicationIcon)
                .build());


        generalInfoCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_version_number)
                .subText(versionName)// + " (" + versionCode + ")")
                .build());
        //endregion


        //region supportCardBuilder
        MaterialAboutCard.Builder supportCardBuilder = new MaterialAboutCard.Builder();
        supportCardBuilder.addItem(
                new MaterialAboutTitleItem.Builder()
                        .text(context.getResources().getString(R.string.about_support))
                        .icon(applicationIcon)
                        .build())

                .addItem(new MaterialAboutActionItem.Builder()
                        .subText(R.string.support_notice)
                        .build())

                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.financial_aid)
                        .subText(R.string.support_sub_text)
                        .icon(R.drawable.ic_payment_black_24dp)
                        .setOnClickAction(() -> AboutUtil.pay(context))
                        .build())

                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.send_feedback_two)
                        .subText(R.string.feed_back_sub_tex)
                        .icon(R.drawable.ic_feedback_black_24dp)
                        .setOnClickAction(() -> AboutUtil.sendFeedBack(context))
                        .build())

                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.action_share)
                        .subText(R.string.share_app_msg_sub_text)
                        .icon(R.drawable.ic_share_light_24dp)
                        .setOnClickAction(() -> AboutUtil.ShareAppLink(context))
                        .build())

                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.action_rate)
                        .icon(R.drawable.ic_star_black_24dp)
                        .setOnClickAction(() -> AboutUtil.rateApp(context))
                        .build());
        //endregion

        //region openSourceCardBuilder
        MaterialAboutCard.Builder openSourceCardBuilder = new MaterialAboutCard.Builder();
        openSourceCardBuilder.addItem(
                new MaterialAboutTitleItem.Builder()
                        .text(context.getResources().getString(R.string.about_cntribute_programatically))
                        .icon(applicationIcon)
                        .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .subText(R.string.open_source_notice)
                .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_git_hub)
                .subText(R.string.source_code)
                .icon(R.drawable.github_mark_64px)
                .setOnClickAction(() -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fekracomputers/IslamicLibraryAndroid"));
                    startActivity(browserIntent);
                })
                .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.chat_on_gitter)
                .icon(R.drawable.gitter_logo)
                .setOnClickAction(() -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gitter.im/fekracomputers/IslamicLibraryAndroid"));
                    startActivity(browserIntent);
                })
                .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.chat_on_slack)
                .icon(R.drawable.slack_mark)
                .setOnClickAction(() -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://join.slack.com/t/fekra-computers/shared_invite/MjExNjYwNDYzODEwLTE0OTk4NjA3NDgtMDU5MDQxOWRlNw"));
                    startActivity(browserIntent);
                })
                .build());
        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.licence)
                .subText(R.string.copyright_notice)
                .icon(R.drawable.gplv3_logo)
                .setOnClickAction(AboutUtil.webViewDialog(context,
                        "file:///android_asset/LICENCE.html",
                        R.string.licence))
                .build());

        //endregion

        //region socialNetworksCardBuilder
        MaterialAboutCard.Builder socialNetworksCardBuilder = new MaterialAboutCard.Builder();
        socialNetworksCardBuilder
                .addItem(new MaterialAboutTitleItem.Builder()
                        .text(R.string.find_us_on_social_media)
                        .icon(R.drawable.company_logo)
                        .build());


        socialNetworksCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_facebook_label)
                .subText(R.string.facebookUserName)
                .icon(R.drawable.ic_facebook_logo)
                .setOnClickAction(() -> AboutUtil.getOpenFacebookIntent(context,
                        context.getResources().getString(R.string.facebookUserName)))
                .build());
        socialNetworksCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_twitter_label)
                .subText(R.string.twitter_user_name)
                .icon(R.drawable.ic_twitter_logo_blue)
                .setOnClickAction(() -> AboutUtil.startTwitter(context,
                        context.getResources().getString(R.string.twitter_user_name)))
                .build());
        socialNetworksCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_youtube_label)
                .icon(R.drawable.youtube_social_square_red)
                .setOnClickAction(() -> AboutUtil.startYoutube(context))
                .build());


        //endregion

        //region aboutCardBuilder
        MaterialAboutCard.Builder aboutCardBuilder = new MaterialAboutCard.Builder();
        aboutCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.other_apps)
                .icon(R.drawable.ic_google_play)
                .setOnClickAction(() -> AboutUtil.openDevelopersPage(context))
                .build());
        aboutCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_web_page_label)
                .subText(R.string.web_page)
                .icon(R.drawable.ic_public_black_24dp)
                .setOnClickAction(() -> startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getResources().getString(R.string.web_page)))))
                .build());
        //endregion


        //region legalCardBuilder
        MaterialAboutCard.Builder legalCardBuilder = new MaterialAboutCard.Builder();
        legalCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.open_source_licences)
                .icon(R.drawable.ic_insert_drive_file_black_24dp)
                .setOnClickAction(() -> {
                    OssLicensesMenuActivity.setActivityTitle(context.getResources().getString(R.string.open_source_licences));
                    startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
                })
                .build())
//        .addItem(new MaterialAboutActionItem.Builder()
//                .text(R.string.about_activity_acknowledgements)
//                .icon(R.drawable.ic_info_black_24dp)
//                .setOnClickAction(AboutUtil.webViewDialog(context,
//                        "file:///android_asset/acknowledgements.html",
//                        R.string.about_activity_acknowledgements))
//                .build())
        ;
        //endregion


        return new MaterialAboutList.Builder()
                .addCard(generalInfoCardBuilder.build())
                .addCard(supportCardBuilder.build())
                .addCard(aboutCardBuilder.build())
                .addCard(socialNetworksCardBuilder.build())
                .addCard(openSourceCardBuilder.build())
                .addCard(legalCardBuilder.build())
                .build();
    }
}
