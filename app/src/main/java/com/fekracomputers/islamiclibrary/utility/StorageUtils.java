package com.fekracomputers.islamiclibrary.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import com.fekracomputers.islamiclibrary.BuildConfig;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import timber.log.Timber;

import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.DATABASE_FULL_NAME;
import static com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants.PREF_SDCARDPERMESSION_DIALOG_DISPLAYED;

/**
 * <pre>
 * Based on:
 * - https://github.com/quran/quran_android/blob/master/app/src/main/java/com/quran/labs/androidquran/util/StorageUtils.java
 * - https://github.com/quran/quran_android/blob/master/app/src/main/java/com/quran/labs/androidquran/util/QuranFileUtils.java
 * - http://sapienmobile.com/?p=204
 * - http://stackoverflow.com/a/15612964
 * - http://renzhi.ca/2012/02/03/how-to-list-all-sd-cards-on-android/
 * </pre>
 */
public class StorageUtils {

    public static String getIslamicLibraryShamelaBooksDir(Context context) {
        return getIslamicLibraryBaseDirectory(context) + File.separator + DownloadFileConstants.SHAMELA_BOOKS_DIR;

//        return Environment.getExternalStorageDirectory().getAbsolutePath() +
//                File.separator + DownloadFileConstants.ISLAMIC_LIBRARY_BASE_DIRECTORY;
    }

    @Nullable
    public static String getIslamicLibraryBaseDirectory(Context context) {
        String basePath = getAppCustomLocation(context);

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
    public static String getAppCustomLocation(Context context) {
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

    public static boolean moveAppFiles(Context context, String newLocation, boolean automatic) {
        if (getAppCustomLocation(context).equals(newLocation)) {
            return true;
        }
        final String baseDir = getIslamicLibraryBaseDirectory(context);
        if (baseDir == null) {
            return false;
        }
        File currentDirectory = new File(baseDir);
        File newDirectory = new File(newLocation, DownloadFileConstants.ISLAMIC_LIBRARY_BASE_DIRECTORY);
        if (!currentDirectory.exists()) {
            // No files to copy, so change the app directory directly
            return true;
        } else if (newDirectory.exists() || newDirectory.mkdirs()) {
            if (automatic) {
                try {
                    copyFileOrDirectory(currentDirectory, newDirectory);
                    deleteFileOrDirectory(currentDirectory);
                    return true;
                } catch (IOException e) {
                    Timber.e(e, "error moving app files");
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private static void deleteFileOrDirectory(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            // subFiles is null on some devices, despite this being a directory
            int length = subFiles == null ? 0 : subFiles.length;
            for (int i = 0; i < length; i++) {
                File sf = subFiles[i];
                if (sf.isFile()) {
                    if (!sf.delete()) {
                        Timber.e("Error deleting %s", sf.getPath());
                    }
                } else {
                    deleteFileOrDirectory(sf);
                }
            }
        }
        if (!file.delete()) {
            Timber.e("Error deleting %s", file.getPath());
        }
    }

    private static void copyFileOrDirectory(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists() && !destination.mkdirs()) {
                return;
            }
            File[] files = source.listFiles();
            for (File f : files) {
                copyFileOrDirectory(f, new File(destination, f.getName()));
            }
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyFile(File source, File destination) throws IOException {
        FileInputStream inStream = new FileInputStream(source);
        FileOutputStream outStream = new FileOutputStream(destination);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            inStream.close();
            outStream.close();
        }

    }

    /**
     * @return A List of all storage locations available
     */
    public static List<Storage> getAllStorageLocations(Context context) {

    /*
      This first condition is the code moving forward, since the else case is a bunch
      of unsupported hacks.

      For Kitkat and above, we rely on Environment.getExternalFilesDirs to give us a list
      of application writable directories (none of which require WRITE_EXTERNAL_STORAGE on
      Kitkat and above).

      Previously, we only would show anything if there were at least 2 entries. For M,
      some changes were made, such that on M, we even show this if there is only one
      entry.

      Irrespective of whether we require 1 entry (M) or 2 (Kitkat and L), we add an
      additional entry explicitly for the sdcard itself, (the one requiring
      WRITE_EXTERNAL_STORAGE to write).

      Thus, on Kitkat, the user may either:
      a. not see any item (if there's only one entry returned by getExternalFilesDirs, we won't
      show any options since it's the same sdcard and we have the permission and the user can't
      revoke it pre-Kitkat), or
      b. see 3+ items - /sdcard, and then at least 2 external fiels directories.

      on M, the user will always see at least 2 items (the external files dir and the actual
      external storage directory), and potentially more (depending on how many items are returned
      by getExternalFilesDirs).
     */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<Storage> result = new ArrayList<>();
            int limit = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 1 : 2;
            final File[] mountPoints = ContextCompat.getExternalFilesDirs(context, null);
            if (mountPoints != null && mountPoints.length >= limit) {
                int typeId;
                if (!Environment.isExternalStorageRemovable() || Environment.isExternalStorageEmulated()) {
                    typeId = R.string.prefs_sdcard_internal;
                } else {
                    typeId = R.string.prefs_sdcard_external;
                }

                int number = 1;
                result.add(new Storage(context.getString(typeId, number),
                        Environment.getExternalStorageDirectory().getAbsolutePath(),
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M));
                for (File mountPoint : mountPoints) {
                    result.add(new Storage(context.getString(typeId, number++),
                            mountPoint.getAbsolutePath()));
                    typeId = R.string.prefs_sdcard_external;
                }
            }
            return result;
        } else {
            return getLegacyStorageLocations(context);
        }
    }

    /**
     * Attempt to return a list of storage locations pre-Kitkat.
     *
     * @param context the context
     * @return the list of storage locations
     */
    private static List<Storage> getLegacyStorageLocations(Context context) {
        List<String> mounts = readMountsFile();

        // As per http://source.android.com/devices/tech/storage/config.html
        // device-specific vold.fstab file is removed after Android 4.2.2
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Set<String> volds = readVoldsFile();

            List<String> toRemove = new ArrayList<>();
            for (String mount : mounts) {
                if (!volds.contains(mount)) {
                    toRemove.add(mount);
                }
            }

            for (String s : toRemove) {
                mounts.remove(s);
            }
        } else {
            Timber.d("Android version: %d, skip reading vold.fstab file", Build.VERSION.SDK_INT);
        }

        Timber.d("mounts list is: %s", mounts);
        return buildMountsList(context, mounts);
    }

    /**
     * Converts a list of mount strings to a list of Storage items
     *
     * @param context the context
     * @param mounts  a list of mount points as strings
     * @return a list of Storage items that can be rendered by the ui
     */
    private static List<Storage> buildMountsList(Context context, List<String> mounts) {
        List<Storage> list = new ArrayList<>(mounts.size());

        int externalSdcardsCount = 0;
        if (mounts.size() > 0) {
            // Follow Android SD Cards naming conventions
            if (!Environment.isExternalStorageRemovable() || Environment.isExternalStorageEmulated()) {
                list.add(new Storage(context.getString(R.string.prefs_sdcard_internal),
                        Environment.getExternalStorageDirectory().getAbsolutePath()));
            } else {
                externalSdcardsCount = 1;
                list.add(new Storage(context.getString(R.string.prefs_sdcard_external,
                        externalSdcardsCount), mounts.get(0)));
            }

            // All other mounts rather than the first mount point are considered as External SD Card
            if (mounts.size() > 1) {
                externalSdcardsCount++;
                for (int i = 1/*skip the first item*/; i < mounts.size(); i++) {
                    list.add(new Storage(context.getString(R.string.prefs_sdcard_external,
                            externalSdcardsCount++), mounts.get(i)));
                }
            }
        }

        Timber.d("final storage list is: %s", list);
        return list;
    }

    /**
     * Read /proc/mounts. This is a set of hacks for versions below Kitkat.
     *
     * @return list of mounts based on the mounts file.
     */
    private static List<String> readMountsFile() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        List<String> mounts = new ArrayList<>();
        mounts.add(sdcardPath);

        Timber.d("reading mounts file begin");
        try {
            File mountFile = new File("/proc/mounts");
            if (mountFile.exists()) {
                Timber.d("mounts file exists");
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    Timber.d("line: %s", line);
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];
                        Timber.d("mount element is: %s", element);
                        if (!sdcardPath.equals(element)) {
                            mounts.add(element);
                        }
                    } else {
                        Timber.d("skipping mount line: %s", line);
                    }
                }
            } else {
                Timber.d("mounts file doesn't exist");
            }

            Timber.d("reading mounts file end.. list is: %s", mounts);
        } catch (Exception e) {
            Timber.e(e, "Error reading mounts file");
        }
        return mounts;
    }

    /**
     * Reads volume manager daemon file for auto-mounted storage.
     * Read more about it <a href="http://vold.sourceforge.net/">here</a>.
     * <p>
     * Set usage, to safely avoid duplicates, is intentional.
     *
     * @return Set of mount points from `vold.fstab` configuration file
     */
    private static Set<String> readVoldsFile() {
        Set<String> volds = new HashSet<>();
        volds.add(Environment.getExternalStorageDirectory().getAbsolutePath());

        Timber.d("reading volds file");
        try {
            File voldFile = new File("/system/etc/vold.fstab");
            if (voldFile.exists()) {
                Timber.d("reading volds file begin");
                Scanner scanner = new Scanner(voldFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    Timber.d("line: %s", line);
                    if (line.startsWith("dev_mount")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[2];
                        Timber.d("volds element is: %s", element);

                        if (element.contains(":")) {
                            element = element.substring(0, element.indexOf(":"));
                            Timber.d("volds element is: %s", element);
                        }

                        Timber.d("adding volds element to list: %s", element);
                        volds.add(element);
                    } else {
                        Timber.d("skipping volds line: %s", line);
                    }
                }
            } else {
                Timber.d("volds file doesn't exit");
            }
            Timber.d("reading volds file end.. list is: %s", volds);
        } catch (Exception e) {
            Timber.e(e, "Error reading volds file");
        }

        return volds;
    }

    public static int getAppUsedSpace(Context context) {
        final String baseDirectory = getIslamicLibraryBaseDirectory(context);
        if (baseDirectory == null) {
            return -1;
        }

        File base = new File(baseDirectory);
        ArrayList<File> files = new ArrayList<>();
        files.add(base);
        long size = 0;
        while (!files.isEmpty()) {
            File f = files.remove(0);
            if (f.isDirectory()) {
                File[] subFiles = f.listFiles();
                if (subFiles != null) {
                    Collections.addAll(files, subFiles);
                }
            } else {
                size += f.length();
            }
        }
        return (int) (size / (long) (1024 * 1024));
    }

    public static boolean didPresentSdcardPermissionsDialog(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getBoolean(PREF_SDCARDPERMESSION_DIALOG_DISPLAYED, false);
    }

    public static class Storage {
        private final String label;
        private final String mountPoint;
        private final boolean requiresPermission;

        private int freeSpace;

        Storage(String label, String mountPoint) {
            this(label, mountPoint, false);
        }

        Storage(String label, String mountPoint, boolean requiresPermission) {
            this.label = label;
            this.mountPoint = mountPoint;
            this.requiresPermission = requiresPermission;
            computeSpace();
        }

        private void computeSpace() {
            StatFs stat = new StatFs(mountPoint);
            long bytesAvailable;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                bytesAvailable = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            } else {
                //noinspection deprecation
                bytesAvailable = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
            // Convert total bytes to megabytes
            freeSpace = Math.round(bytesAvailable / (1024 * 1024));
        }

        public String getLabel() {
            return label;
        }

        public String getMountPoint() {
            return mountPoint;
        }

        /**
         * @return available free size in Megabytes
         */
        public int getFreeSpace() {
            return freeSpace;
        }

        public boolean doesRequirePermission() {
            return requiresPermission;
        }
    }


}
