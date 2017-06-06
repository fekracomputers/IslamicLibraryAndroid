package com.fekracomputers.islamiclibrary.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import com.fekracomputers.islamiclibrary.BuildConfig;
import com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants;

import java.io.File;

import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.DATABASE_FULL_NAME;
import static com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants.PREF_SDCARDPERMESSION_DIALOG_DISPLAYED;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 1/6/2017.
 */

public class StorageUtils {

    public static String getIslamicLibraryShamelaBooksDir(Context context) {
        return getIslamicLibraryBaseDirectory(context) + File.separator + DownloadFileConstants.SHAMELA_BOOKS_DIR;

//        return Environment.getExternalStorageDirectory().getAbsolutePath() +
//                File.separator + DownloadFileConstants.ISLAMIC_LIBRARY_BASE_DIRECTORY;
    }

    @Nullable
    public static String getIslamicLibraryBaseDirectory(Context context) {
        String basePath = getCustomLocation(context);

        if (!isSDCardMounted()) {
            // if our best guess suggests that we won't have access to the data due to the sdcard not
            // being mounted, then set the base path to null for now.
            if (basePath == null ||
                    basePath.equals(Environment.getExternalStorageDirectory().getAbsolutePath()) ||
                    (basePath.contains(BuildConfig.APPLICATION_ID) && context.getExternalFilesDir(null) == null)) {
                basePath = null;
            }
        }

        if (basePath != null) {
            if (!basePath.endsWith(File.separator)) {
                basePath += File.separator;
            }
            return basePath + DownloadFileConstants.ISLAMIC_LIBRARY_BASE_DIRECTORY;
        }
        return null;
    }

    /**
     * @return string representing the path to the root custom external storage
     */
    public static String getCustomLocation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(DownloadFileConstants.PREF_APP_LOCATION,
                Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private static boolean isSDCardMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    public static boolean haveWriteExternalStoragePermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    public static boolean canRequestWriteExternalStoragePermission(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean didPresentSdcardPermissionsDialog = sharedPreferences.getBoolean(PREF_SDCARDPERMESSION_DIALOG_DISPLAYED, false);
        return !didPresentSdcardPermissionsDialog ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void setAppCustomLocation(String location, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DownloadFileConstants.PREF_APP_LOCATION, location);
        editor.commit();

    }

    public static void setSdcardPermissionsDialogPresented(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_SDCARDPERMESSION_DIALOG_DISPLAYED, true);
        editor.commit();

    }

    public static boolean makeIslamicLibraryShamelaDirectory(Context context) {
        String path = getIslamicLibraryShamelaBooksDir(context);
        if (path == null) {
            return false;
        }
        File directory = new File(path);
        return (directory.exists() && directory.isDirectory()) || directory.mkdirs();
    }

    public static boolean isOldDirectoriesExists(Context context) {
        String oldPath = getIslamicLibraryBaseDirectory(context) + File.separator +
                DATABASE_FULL_NAME;
        return new File(oldPath).exists();

    }



}
