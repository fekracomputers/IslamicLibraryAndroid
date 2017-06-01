package com.fekracomputers.islamiclibrary.download.view;

import android.support.v7.util.DiffUtil;

import com.fekracomputers.islamiclibrary.download.model.DownloadInfo;

import java.util.List;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 17/5/2017.
 */

class DownloadInfoUpdate {
    public static final int TYPE_CANCEL = 0;
    public static final int TYPE_PROGRESS = 1;
    List<DownloadInfo> downloadInfos;
    DiffUtil.DiffResult diffResult;
    int type;

    public DownloadInfoUpdate(List<DownloadInfo> downloadInfos, DiffUtil.DiffResult diffResult) {
        this.downloadInfos = downloadInfos;
        this.diffResult = diffResult;
        type=TYPE_PROGRESS;
    }

    public DownloadInfoUpdate(int typeCancel) {
        type=TYPE_CANCEL;

    }
}
