package com.fekracomputers.islamiclibrary.download.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.download.view.DownloadProgressActivity;

public class DownloadManagerNotificationClickReciver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        //start activity
        Intent i = new Intent(context,DownloadProgressActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}
