package com.fekracomputers.islamiclibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

import com.duolingo.open.rtlviewpager.RtlViewPager;


/**
 * Created by Mohammad on 6/9/2017.
 */

public class RTLOnlyViewPager extends RtlViewPager  {
    public RTLOnlyViewPager(Context context) {
        super(context);
    }

    public RTLOnlyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean isRtl() {
        return true;
    }
}
