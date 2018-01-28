package com.fekracomputers.islamiclibrary.widget;

/**
 * Created by Mohammad on 30/10/2017.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

public class SpinnerWithCloseEvent extends android.support.v7.widget.AppCompatSpinner {
    private static final String TAG = "CustomSpinner";
    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    public SpinnerWithCloseEvent(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public SpinnerWithCloseEvent(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpinnerWithCloseEvent(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerWithCloseEvent(@NonNull Context context, int mode) {
        super(context, mode);
    }

    public SpinnerWithCloseEvent(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened();
        }
        return super.performClick();
    }

    public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerClosed();
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        //android.util.Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasBeenOpened() && hasWindowFocus) {
            //android.util.Log.i(TAG, "closing popup");
            performClosedEvent();
        }
    }

    public interface OnSpinnerEventsListener {

        void onSpinnerOpened();

        void onSpinnerClosed();

    }
}