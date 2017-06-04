package com.fekracomputers.islamiclibrary.utility;

import android.os.Environment;

import java.io.File;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 1/6/2017.
 */

public class StorageUtils {
    public static String getApplicationBooksDir()
    {

       return Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "IslamicLibrary";
    }
}
