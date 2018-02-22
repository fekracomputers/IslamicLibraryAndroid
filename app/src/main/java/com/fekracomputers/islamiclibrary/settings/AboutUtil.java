package com.fekracomputers.islamiclibrary.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.billing.BillingActivity;
import com.webianks.easy_feedback.EasyFeedback;

import java.util.List;

import timber.log.Timber;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammad Yahia on 11/2/2017.
 */
public class AboutUtil {

    public static void sendFeedBack(@NonNull Context c) {
        new EasyFeedback.Builder(c)
                .withEmail(c.getString(R.string.feedback_email))
                .withSystemInfo()
                .build()
                .start();
    }

    public static void ShareAppLink(@NonNull Context c) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            String appId = c.getPackageName();
            String appName = c.getResources().getString(R.string.app_name);
            String recommendationString = c.getString(R.string.share_app_msg, appName, "https://play.google.com/store/apps/details?id=" + appId);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            shareIntent.putExtra(Intent.EXTRA_TEXT, recommendationString);
            c.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private static void openMarket(@NonNull Context context, String appURI, String webURI) {
        // you can also use BuildConfig.APPLICATION_ID
        //String appId = "com.google.android.apps.books";
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(appURI));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task re-parenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(webURI));
            context.startActivity(webIntent);
        }
    }

    public static void rateApp(@NonNull Context context) {
        String appId = context.getPackageName();
        openMarket(context, "market://details?id=" + appId, "https://play.google.com/store/apps/details?id=" + appId);
    }


    public static void openDevelopersPage(@NonNull Context context) {
        openMarket(context, "market://developer?id=Fekra+Computers", "https://play.google.com/store/apps/developer?id=Fekra+Computers");
    }


    public static void getOpenFacebookIntent(@NonNull Context context, String name) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + name));
            context.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + name));
                context.startActivity(intent);
            } catch (Exception e1) {
                Toast.makeText(context, R.string.error_message_title, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void startTwitter(@NonNull Context context, String name) {
        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
                context.startActivity(intent);
            } catch (Exception e1) {
                Toast.makeText(context, R.string.error_message_title, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static MaterialAboutItemOnClickAction webViewDialog(@NonNull final Context context,
                                                               final String targetUrl,
                                                               final int title) {

        return webViewDialog(context, targetUrl, context.getString(title));
    }

    public static MaterialAboutItemOnClickAction webViewDialog(@NonNull final Context context,
                                                               final String targetUrl,
                                                               final String title) {
        return () -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(title);
            WebView wv = new WebView(context);
            wv.setWebViewClient(
                    new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull String url) {
                            if (!url.equals("targetUrl")) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(url)));
                                view.reload();
                                return true;
                            } else {
                                view.loadUrl(url);
                                return true;
                            }
                        }
                    }
            );
            wv.loadUrl(targetUrl);
            alert.setView(wv);
            alert.setNegativeButton(R.string.Ok, (dialog, id) -> dialog.dismiss()

            );
            alert.show();
        }

                ;

    }

    public static void pay(Context context) {
        Intent intent = new Intent(context, BillingActivity.class);
        context.startActivity(intent);

    }
}
