package com.fekracomputers.islamiclibrary.browsing.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fekracomputers.islamiclibrary.model.AuthorInfo;
import com.fekracomputers.islamiclibrary.model.BookCategory;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Mohammad on 24/1/2018.
 */

public class BrowsingAnalyticsController {
    private static final String ANALYTICS_AUTHOR_TYPE = "Author_";
    private static final String ANALYTICS_CATEGORY_TYPE = "Category_";

    @Nullable
    private FirebaseAnalytics mFirebaseAnalytics;

    public BrowsingAnalyticsController(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    void logAuthorBooksList(@NonNull AuthorInfo authorInfo) {
        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getCategoryValue(authorInfo));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
        }
    }

    @NonNull
    private String getCategoryValue(@NonNull AuthorInfo authorInfo) {
        return ANALYTICS_AUTHOR_TYPE + authorInfo.getId();
    }

    void logBookSelectionEvent(int bookId) {
        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(bookId));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        }
    }

    public void logCategoryEvent(BookCategory bookCategory) {
        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getCategoryValue(bookCategory));
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
        }
    }

    private String getCategoryValue(BookCategory bookCategory) {
        return ANALYTICS_CATEGORY_TYPE + bookCategory.getId();

    }


}
