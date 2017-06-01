package com.fekracomputers.islamiclibrary.widget;


import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

/**
 * Author: Michael Ritchie, ThanksMister LLC
 * Date: 10/16/12
 * Web: thanksmister.com
 * <p>
 * Extension of <code>TextView</code> that adds listener for ellipses changes.  This can be used to determine
 * if a TextView has an ellipses or not.
 * <p>
 * Derived from discussion on StackOverflow:
 * <p>
 * http://stackoverflow.com/questions/4005933/how-do-i-tell-if-my-textview-has-been-ellipsized
 */

public class EllipsisTextView extends android.support.v7.widget.AppCompatTextView {
    private EllipsisListener ellipsesListeners;
    private boolean ellipses;

    public EllipsisTextView(Context context) {
        super(context);
    }

    public EllipsisTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EllipsisTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addEllipsesListener(EllipsisListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        ellipsesListeners = listener;
    }

    public void removeEllipsesListener(EllipsisListener listener) {
        ellipsesListeners = null;
    }

    public boolean hadEllipses() {
        return ellipses;
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);

        ellipses = false;
        Layout layout = getLayout();
        if (layout != null) {
            int lines = layout.getLineCount();
            if (lines > 0) {
                if (layout.getEllipsisCount(lines - 1) > 0) {
                    ellipses = true;
                }
            }
        }

        ellipsesListeners.ellipsisStateChanged(ellipses);
    }

    public interface EllipsisListener {
        void ellipsisStateChanged(boolean ellipses);
    }
}
