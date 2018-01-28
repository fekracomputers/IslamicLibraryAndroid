package com.fekracomputers.islamiclibrary.download.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class RefreshBooksWithDirectoryService extends IntentService {
    private static final String ACTION_REFRESH_EVERY_THING = "com.fekracomputers.islamiclibrary.download.service.action.ACTION_REFRESH_EVERY_THING";


    public RefreshBooksWithDirectoryService() {
        super("RefreshBooksWithDirectoryService");
        setIntentRedelivery(true);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRefreshEveryThing(@NonNull Context context) {
        Intent intent = new Intent(context, RefreshBooksWithDirectoryService.class);
        intent.setAction(ACTION_REFRESH_EVERY_THING);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH_EVERY_THING.equals(action)) {
                handleActionRefreshEveryThing();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRefreshEveryThing() {
        BooksInformationDbHelper booksInformationDbHelper = BooksInformationDbHelper.getInstance(this);
        if (booksInformationDbHelper != null) {
            booksInformationDbHelper.refreshBooksDbWithDirectory(this);
        }

    }


}
