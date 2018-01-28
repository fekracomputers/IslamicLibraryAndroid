package com.fekracomputers.islamiclibrary.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Mohammad on 4/9/2017.
 */

public class PermissionUtil {
    public static boolean haveWriteExternalStoragePermission(@NonNull Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    public static boolean canRequestWriteExternalStoragePermission(@NonNull Activity activity) {
        return !StorageUtils.didPresentSdcardPermissionsDialog(activity) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
