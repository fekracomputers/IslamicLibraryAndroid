package com.fekracomputers.islamiclibrary.download.model;

import android.app.DownloadManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.util.DiffUtil;

import com.fekracomputers.islamiclibrary.R;

import java.util.List;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 10/5/2017.
 */

public class DownloadInfo implements Comparable<DownloadInfo> {
    private long enquId;
    private String title;
    private int status;
    private int reason;
    private long bytesDownloadedSoFar;
    private long lastModifiedTimestamp;
    private long totalSizeBytes;


    public DownloadInfo(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        status = cursor.getInt(columnIndex);

        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        reason = cursor.getInt(columnReason);

        int columnBytesDownloadedSoFar = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        bytesDownloadedSoFar = cursor.getLong(columnBytesDownloadedSoFar);

        int columnId = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
        enquId = cursor.getLong(columnId);

        int columnTitlle = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
        title = cursor.getString(columnTitlle);

        int columnTimeStamp = cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP);
        lastModifiedTimestamp = cursor.getLong(columnTimeStamp);

        int columnTotlalSize = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        totalSizeBytes = cursor.getLong(columnTotlalSize);

    }

    public Long getDownloadedSize() {
        return bytesDownloadedSoFar;
    }

    public Long getTotalSize() {
        return totalSizeBytes;
    }

    public long getId() {
        return enquId;
    }

    public int getProgressPercent() {
        return (int) ((bytesDownloadedSoFar * 100L) / totalSizeBytes);
    }

    public String getTitle() {
        return title;
    }

    @StringRes
    public int getStatusTextResId() {
        int statusText = 0;
        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = (R.string.STATUS_FAILED);
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = (R.string.STATUS_PAUSED);

                break;
            case DownloadManager.STATUS_PENDING:
                statusText = (R.string.STATUS_PENDING);
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = (R.string.STATUS_RUNNING);
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = (R.string.STATUS_SUCCESSFUL);
                break;
        }
        return statusText;
    }

    @StringRes
    public int getReasonTextResId() {
        int reasonText = R.string.unKnown_status;
        switch (status) {
            case DownloadManager.STATUS_FAILED:
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = R.string.ERROR_CANNOT_RESUME;
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = (R.string.ERROR_DEVICE_NOT_FOUND);
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = (R.string.ERROR_FILE_ALREADY_EXISTS);
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = (R.string.ERROR_FILE_ERROR);
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = (R.string.ERROR_HTTP_DATA_ERROR);
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = (R.string.ERROR_INSUFFICIENT_SPACE);
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = (R.string.ERROR_TOO_MANY_REDIRECTS);
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = (R.string.ERROR_UNHANDLED_HTTP_CODE);
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = (R.string.ERROR_UNKNOWN);
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED: {
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = (R.string.PAUSED_QUEUED_FOR_WIFI);
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = (R.string.PAUSED_UNKNOWN);
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = (R.string.PAUSED_WAITING_FOR_NETWORK);
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = (R.string.PAUSED_WAITING_TO_RETRY);
                        break;
                }
                break;
            }
        }
        return reasonText;
    }

    public long getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    @Override
    public int compareTo(@NonNull DownloadInfo o) {
        return (int) (this.enquId - o.enquId);
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "enquId=" + enquId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", reason=" + reason +
                ", bytesDownloadedSoFar=" + bytesDownloadedSoFar +
                ", lastModifiedTimestamp=" + lastModifiedTimestamp +
                ", totalSizeBytes=" + totalSizeBytes +
                '}';
    }



    public static class DownloadInfoDiffCallback extends DiffUtil.Callback {
        public static final String KEY_PROGRESS_PERCENT_FROM = "KEY_PROGRESS_PERCENT_FROM";
        public static final String KEY_REASON_RES_ID = "KEY_REASON_RES_ID";
        public static final String KEY_STATUS_RES_ID = "KEY_STATUS_RES_ID";
        public static final String KEY_DOWNLOADED_BYTES = "KEY_DOWNLOADED_BYTES";
        public static final String KEY_PROGRESS_PERCENT_TO = "KEY_PROGRESS_PERCENT_TO";
        private List<DownloadInfo> mOldList;
        private List<DownloadInfo> mNewList;

        public DownloadInfoDiffCallback(List<DownloadInfo> oldList, List<DownloadInfo> newList) {
            this.mOldList = oldList;
            this.mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList != null ? mOldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return mNewList != null ? mNewList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mNewList.get(newItemPosition).getId() == mOldList.get(oldItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mNewList.get(newItemPosition).lastModifiedTimestamp == mOldList.get(oldItemPosition).lastModifiedTimestamp
                    && mNewList.get(newItemPosition).bytesDownloadedSoFar == mOldList.get(oldItemPosition).bytesDownloadedSoFar;
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            DownloadInfo newDownload = mNewList.get(newItemPosition);
            DownloadInfo oldDownload = mOldList.get(oldItemPosition);
            Bundle diffBundle = new Bundle();
            if (newDownload.getProgressPercent() != oldDownload.getProgressPercent()) {
                diffBundle.putInt(KEY_PROGRESS_PERCENT_FROM, oldDownload.getProgressPercent());
                diffBundle.putInt(KEY_PROGRESS_PERCENT_TO, newDownload.getProgressPercent());
            }
            if (newDownload.bytesDownloadedSoFar != oldDownload.bytesDownloadedSoFar) {
                diffBundle.putLong(KEY_DOWNLOADED_BYTES, newDownload.bytesDownloadedSoFar);
            }
            if (newDownload.status != oldDownload.status) {
                diffBundle.putInt(KEY_STATUS_RES_ID, newDownload.getStatusTextResId());
                if (newDownload.reason != oldDownload.reason) {
                    diffBundle.putInt(KEY_REASON_RES_ID, newDownload.getStatusTextResId());
                }
            }

            if (diffBundle.size() == 0) return null;
            return diffBundle;
        }
    }
}