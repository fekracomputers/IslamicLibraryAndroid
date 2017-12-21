package com.fekracomputers.islamiclibrary.download.view;

import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.download.model.DownloadInfo;
import com.fekracomputers.islamiclibrary.download.reciver.LocalDownloadBroadCastReciver;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.fekracomputers.islamiclibrary.download.model.DownloadInfo.DownloadInfoDiffCallback.KEY_PROGRESS_PERCENT_FROM;
import static com.fekracomputers.islamiclibrary.download.model.DownloadInfo.DownloadInfoDiffCallback.KEY_REASON_RES_ID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadInfo.DownloadInfoDiffCallback.KEY_STATUS_RES_ID;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 10/5/2017.
 */

class DownlandProgressRecyclerViewAdapter extends RecyclerView.Adapter<DownlandProgressRecyclerViewAdapter.DownloadProgressViewHolder> {
    private List<DownloadInfo> downloadInfos;
    private Context context;
    private Deque<DownloadInfoUpdate> pendingUpdates = new ArrayDeque<>();

    public DownlandProgressRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    DownlandProgressRecyclerViewAdapter(Context context, List<DownloadInfo> downloadInfos) {
        this.context = context;
        this.downloadInfos = downloadInfos;
    }

    @Override
    public DownloadProgressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_download, parent, false);
        return new DownlandProgressRecyclerViewAdapter.DownloadProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DownloadProgressViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            for (Object payload : payloads) {
                Bundle o = (Bundle) payload;
                for (String key : o.keySet()) {
                    switch (key) {
                        case KEY_PROGRESS_PERCENT_FROM:
                            int from = o.getInt(KEY_PROGRESS_PERCENT_FROM);
                            int to = o.getInt(DownloadInfo.DownloadInfoDiffCallback.KEY_PROGRESS_PERCENT_TO);
                            holder.updateProgress(from, to);
                            break;
                        case DownloadInfo.DownloadInfoDiffCallback.KEY_DOWNLOADED_BYTES:
                            holder.bindDownloadBytes(o.getLong(DownloadInfo.DownloadInfoDiffCallback.KEY_DOWNLOADED_BYTES));
                            break;
                        case KEY_STATUS_RES_ID:
                            holder.bindStatusAndReason(o.getInt(KEY_STATUS_RES_ID), o.getInt(KEY_REASON_RES_ID));
                            break;
                    }
                }
            }
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onBindViewHolder(DownloadProgressViewHolder holder, int position) {
        holder.bind(downloadInfos.get(position));
    }

    @Override
    public long getItemId(int position) {
        return downloadInfos.get(position).getId();
    }

    @Override
    public int getItemCount() {
        if (downloadInfos != null) {
            return downloadInfos.size();
        } else
            return 0;
    }

    void updateItems(final DownloadInfoUpdate newItems) {
        pendingUpdates.push(newItems);
        if (pendingUpdates.size() == 1) {
            applyDiffResult(newItems);
        }
    }

    private void applyDiffResult(DownloadInfoUpdate newItems) {
        pendingUpdates.remove(newItems);
        dispatchUpdates(newItems);
        if (pendingUpdates.size() > 0) {
            DownloadInfoUpdate latest = pendingUpdates.pop();
            pendingUpdates.clear();
            applyDiffResult(latest);
        }
    }

    // This method does the work of actually updating
    // the backing data and notifying the adapter
    private void dispatchUpdates(DownloadInfoUpdate newItems) {
        //this order is opposite to what the documentation suggests but this only works
        newItems.diffResult.dispatchUpdatesTo(this);
        downloadInfos.clear();
        downloadInfos.addAll(newItems.downloadInfos);

    }

    class DownloadProgressViewHolder extends RecyclerView.ViewHolder {
        static final int PERCENTSMOOTHINGFACTOR = 100;
        final ProgressBar progressBar;
        final TextView downloadTitle;
        final ImageButton cancelButton;
        private final TextView downladStatusTextView;
        private final TextView bytesDownloadedSoFarTextView;
        private final TextView reasonTextView;
        DownloadInfo downloadInfo;

        DownloadProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            downloadTitle = itemView.findViewById(R.id.book_name);
            cancelButton = itemView.findViewById(R.id.cancel_btn);
            downladStatusTextView = itemView.findViewById(R.id.status_tv);
            reasonTextView = itemView.findViewById(R.id.reason_tv);
            bytesDownloadedSoFarTextView = itemView.findViewById(R.id.download_size_tv);

            cancelButton.setOnClickListener(v -> {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    downloadManager.remove(downloadInfo.getId());
                }
                LocalDownloadBroadCastReciver.broadCastDownloadCanceled(context, downloadInfo.getId());
            });
        }


        void bind(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            bindProgress(downloadInfo.getProgressPercent());
            downloadTitle.setText(downloadInfo.getTitle());
            bindStatusAndReason(downloadInfo.getStatusTextResId(), downloadInfo.getReasonTextResId());
            bindDownloadBytes(downloadInfo.getDownloadedSize(), downloadInfo.getTotalSize());

        }

        private void bindStatusAndReason(int statusResId, int reasonResId) {
            if (statusResId != R.string.STATUS_RUNNING) {
                downladStatusTextView.setVisibility(View.VISIBLE);
                reasonTextView.setVisibility(View.VISIBLE);
                bindStatus(statusResId);
                bindReason(reasonResId);
            } else {
                downladStatusTextView.setVisibility(View.GONE);
                reasonTextView.setVisibility(View.GONE);
            }
        }

        private void bindStatus(int statusResId) {
            downladStatusTextView.setText(context.getString(statusResId));

        }

        private void bindReason(int reasonResId) {
            reasonTextView.setText(context.getString(reasonResId));
        }

        private void bindProgress(int percent) {
            progressBar.setProgress(percent * PERCENTSMOOTHINGFACTOR);
        }

        private void updateProgress(int from, int to) {
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", from * PERCENTSMOOTHINGFACTOR, to * PERCENTSMOOTHINGFACTOR);
            animation.setDuration(500);// 0.5 second
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }

        private void bindDownloadBytes(long bytesDownloadedSoFar, long totalSize) {
            String bytesDownloadedSoFarFormatted = android.text.format.Formatter.formatShortFileSize(context, bytesDownloadedSoFar);
            String toTalSizeFormatted = android.text.format.Formatter.formatShortFileSize(context, totalSize);
            bytesDownloadedSoFarTextView.setText(context.getString(R.string.downloaded_size, bytesDownloadedSoFarFormatted, toTalSizeFormatted));
        }


        void bindDownloadBytes(long bytesDownloadedSoFar) {
            String bytesDownloadedSoFarFormatted = android.text.format.Formatter.formatShortFileSize(context, bytesDownloadedSoFar);
            String toTalSizeFormatted = android.text.format.Formatter.formatShortFileSize(context, this.downloadInfo.getTotalSize());
            bytesDownloadedSoFarTextView.setText(context.getString(R.string.downloaded_size, bytesDownloadedSoFarFormatted, toTalSizeFormatted));
        }
    }
}
