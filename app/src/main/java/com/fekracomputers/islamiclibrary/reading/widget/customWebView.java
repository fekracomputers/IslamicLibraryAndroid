package com.fekracomputers.islamiclibrary.reading.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 3/4/2017.
 */

public class customWebView extends WebView {
    public customWebView(Context context) {
        super(context);
    }

    public customWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public customWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        Log.d("customWebView", "onFocusChanged: "+previouslyFocusedRect);
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
