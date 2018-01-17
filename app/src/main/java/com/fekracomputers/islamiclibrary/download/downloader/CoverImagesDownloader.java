package com.fekracomputers.islamiclibrary.download.downloader;

import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants;


public class CoverImagesDownloader {
    /**
     * @return the image cover Url if the URL exits otherwise null
     */


    @NonNull
    public static String getImageUrl(int bookId) {
        return DownloadFileConstants.BASE_URL + "/" + DownloadFileConstants.COVERS + "/" + bookId + ".jpg";

    }


}
