package com.fekracomputers.islamiclibrary.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Mohammad on 5/11/2017.
 */

public class FixScrollingFooterBehavior extends AppBarLayout.ScrollingViewBehavior {

    private AppBarLayout appBarLayout;

    public FixScrollingFooterBehavior() {
        super();
    }

    public FixScrollingFooterBehavior(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, @NonNull View child, View dependency) {

        if (appBarLayout == null) {
            appBarLayout = (AppBarLayout) dependency;
        }

        final boolean result = super.onDependentViewChanged(parent, child, dependency);
        final int bottomPadding = calculateBottomPadding(appBarLayout);
        final boolean paddingChanged = bottomPadding != child.getPaddingBottom();
        if (paddingChanged) {
            child.setPadding(
                    child.getPaddingLeft(),
                    child.getPaddingTop(),
                    child.getPaddingRight(),
                    bottomPadding);
            child.requestLayout();
        }
        return paddingChanged || result;
    }


    // Calculate the padding needed to keep the bottom of the view pager's content at the same location on the screen.
    private int calculateBottomPadding(@NonNull AppBarLayout dependency) {
        final int totalScrollRange = dependency.getTotalScrollRange();
        return totalScrollRange + dependency.getTop();
    }
}