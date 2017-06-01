package com.fekracomputers.islamiclibrary.download.downloader;

import android.content.Context;
import android.support.annotation.Nullable;

import com.fekracomputers.islamiclibrary.R;


public class CoverImagesDownloader {
    /**
     * @return the image cover Url if the URL exits otherwise null
     */


    @Nullable
    public static String getImageUrl(Context context, int bookId)

    {

        return context.getString(R.string.base_url) + "/" + context.getString(R.string.url_covers) + "/" + bookId + ".jpg";

    }




}
