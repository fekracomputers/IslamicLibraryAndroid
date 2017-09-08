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
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.franmontiel.attributionpresenter.AttributionPresenter;
import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.Library;
import com.franmontiel.attributionpresenter.entities.License;

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

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull final Context context) {
        ((IslamicLibraryApplication) getActivity().getApplication()).refreshLocale(getActivity(), false);
        MaterialAboutCard.Builder generalInfoCardBuilder = new MaterialAboutCard.Builder();

        //region generalInfoCardBuilder
        PackageManager packageManager = getActivity().getPackageManager();
        ApplicationInfo applicationInfo = getActivity().getApplicationInfo();
        Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);

        PackageInfo pInfo;
        String versionName = "1.0";
        int versionCode = 1;

        try {
            pInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
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
                .subText(versionName)// + " (" + versionCode + ")")
                .build());
        //endregion


        //region supportCardBuilder
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
        //endregion


        //region shareCardBuilder
        MaterialAboutCard.Builder shareCardBuilder = new MaterialAboutCard.Builder();
        shareCardBuilder
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.action_share)
                        .icon(R.drawable.ic_share_black_24dp)
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                AboutUtil.ShareAppLink(context);
                            }
                        })
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.action_rate)
                        .icon(R.drawable.ic_star_black_24dp)
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                AboutUtil.rateApp(context);
                            }
                        })
                        .build());
        //endregion


        //region aboutCardBuilder
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
        //endregion


        //region socialNetworksCardBuilder
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
        //endregion


        //region openSourceCardBuilder
        MaterialAboutCard.Builder openSourceCardBuilder = new MaterialAboutCard.Builder();
        openSourceCardBuilder.addItem(
                new MaterialAboutTitleItem.Builder()
                        .text(getString(R.string.about_cntribute_programatically))
                        .icon(applicationIcon)
                        .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .subText(R.string.open_source_notice)
                 .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_git_hub)
                .subText(R.string.source_code)
                .icon(R.drawable.github_mark_64px)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fekracomputers/IslamicLibraryAndroid"));
                        startActivity(browserIntent);
                    }
                })
                .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.chat_on_gitter)
                .icon(R.drawable.gitter_logo)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gitter.im/fekracomputers/IslamicLibraryAndroid"));
                        startActivity(browserIntent);
                    }
                })
                .build());

        openSourceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.chat_on_slack)
                .icon(R.drawable.slack_mark)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://join.slack.com/t/fekra-computers/shared_invite/MjExNjYwNDYzODEwLTE0OTk4NjA3NDgtMDU5MDQxOWRlNw"));
                        startActivity(browserIntent);
                    }
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

        //region legalCardBuilder
        final AttributionPresenter attributionPresenter = new AttributionPresenter.Builder(context)
                .addAttributions(
                        new Attribution.Builder("Rangy")
                                .addCopyrightNotice("Copyright (c) 2014 Tim Down")
                                .addLicense(License.MIT)
                                .setWebsite("https://github.com/timdown/rangy")
                                .build()
                ).addAttributions(
                        new Attribution.Builder("Expandable RecyclerView")
                                .addCopyrightNotice("Copyright (c) 2014 Big Nerd Ranch")
                                .addLicense(License.MIT)
                                .setWebsite("http://bignerdranch.github.io/expandable-recycler-view/")
                                .build()
                ).addAttributions(
                        new Attribution.Builder("rtl-viewpager")
                                .addCopyrightNotice("Copyright 2016 Duolingo")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/duolingo/rtl-viewpager")
                                .build()
                ).addAttributions(
                        new Attribution.Builder("android-support-preference")
                                .addCopyrightNotice("Copyright 2015 Diego Gómez Olvera")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/consp1racy/android-support-preference")
                                .build()
                ).addAttributions(
                        new Attribution.Builder("EasyFeedback")
                                .addCopyrightNotice("Copyright 2017 Ramankit Singh")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/webianks/EasyFeedback")
                                .build()
                ).addAttributions(
                        new Attribution.Builder("AttributionPresenter")
                                .addCopyrightNotice("Copyright 2017 Francisco José Montiel Navarro")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/franmontiel/AttributionPresenter")
                                .build()
                )
                .addAttributions(
                        new Attribution.Builder("Quran Android")
                                .addCopyrightNotice("For Storage Utilities")
                                .addLicense(License.GPL_3)
                                .setWebsite("https://github.com/quran/quran_android")
                                .build()
                )
                .addAttributions(Library.GLIDE)
                .build();
        MaterialAboutCard.Builder legalCardBuilder = new MaterialAboutCard.Builder();
        legalCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.open_source_licences)
                .icon(R.drawable.ic_insert_drive_file_black_24dp)
                .setOnClickAction(new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        attributionPresenter.showDialog(getString(R.string.open_source_licences));
                    }
                })
                .build());
        legalCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_activity_acknowledgements)
                .icon(R.drawable.ic_info_black_24dp)
                .setOnClickAction(AboutUtil.webViewDialog(context,
                        "file:///android_asset/acknowledgements.html",
                        R.string.about_activity_acknowledgements))
                .build());
        //endregion


        return new MaterialAboutList.Builder()
                .addCard(generalInfoCardBuilder.build())
                .addCard(supportCardBuilder.build())
                .addCard(shareCardBuilder.build())
                .addCard(aboutCardBuilder.build())
                .addCard(socialNetworksCardBuilder.build())
                .addCard(openSourceCardBuilder.build())
                .addCard(legalCardBuilder.build())
                .build();
    }
}
