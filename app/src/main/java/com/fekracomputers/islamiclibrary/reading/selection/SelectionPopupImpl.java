package com.fekracomputers.islamiclibrary.reading.selection;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout.LayoutParams;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.reading.FadeAnimationController;

public class SelectionPopupImpl implements SelectionPopup {
    private boolean mContentsReady = false;
    private boolean mLocationReady = false;
    private final int mScreenHeight;
    private final int mScreenWidth;
    private SelectionLocation mSelectionLocation;
    protected final View mView;
    private TextSelectionDelegate delegate;

    @NonNull
    private final FadeAnimationController mFadeController;




    public static class SelectionBoundsInfo {
        private final Rect mBounds;
        private final Point mHandleAnchorPoint;
        private final int mHandleLongerAxis;

        public SelectionBoundsInfo(Rect bounds, int handleMajor, Point handleAnchorPoint) {
            this.mBounds = bounds;
            this.mHandleLongerAxis = handleMajor;
            this.mHandleAnchorPoint = handleAnchorPoint;
        }

        public int getHandleLongerAxis() {
            return this.mHandleLongerAxis;
        }

        public Rect getBounds() {
            return this.mBounds;
        }
    }

    private class SelectionLocation {
        private final Point mAnchorPoint;
        private final int mHandleLongerAxis;
        private final int mSelectionBottom;
        private final int mSelectionLeft;
        private final int mSelectionRight;
        private final int mSelectionTop;

        public SelectionLocation(int selectionLeft, int selectionRight, int selectionTop, int selectionBottom, int handleLongerAxis, Point anchorPoint) {
            this.mSelectionLeft = selectionLeft;
            this.mSelectionRight = selectionRight;
            this.mSelectionTop = selectionTop;
            this.mSelectionBottom = selectionBottom;
            this.mHandleLongerAxis = handleLongerAxis;
            this.mAnchorPoint = anchorPoint;
        }

        private void layout() {
            int drawLocationY;
            int drawLocationX;
            int size;
            int width;
            int height;

            size = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            SelectionPopupImpl.this.mView.measure(size, size);
            height = SelectionPopupImpl.this.mView.getMeasuredHeight();
            width = SelectionPopupImpl.this.mView.getMeasuredWidth();
            drawLocationX = Math.min(SelectionPopupImpl.this.mScreenWidth - width, Math.max(0, (this.mSelectionLeft + ((this.mSelectionRight - this.mSelectionLeft) / 2)) - (width / 2)));
            drawLocationY = this.mSelectionTop - height >= 0 ? this.mSelectionTop - height : (this.mSelectionBottom + height) + this.mHandleLongerAxis >= SelectionPopupImpl.this.mScreenHeight ? this.mHandleLongerAxis > 0 ? this.mSelectionBottom - height : ((this.mSelectionBottom + this.mSelectionTop) / 2) - (height / 2) : this.mSelectionBottom + this.mHandleLongerAxis;

            LayoutParams layoutParams = (LayoutParams) SelectionPopupImpl.this.mView.getLayoutParams();
            layoutParams.leftMargin = drawLocationX;
            layoutParams.topMargin = drawLocationY;
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            SelectionPopupImpl.this.mView.requestLayout();
        }
    }


    public SelectionPopupImpl(@NonNull TextSelectionDelegate delegate, SelectionBoundsInfo mSelectionRect) {
        this.delegate = delegate;
        SelectionBoundsInfo mSelectionRect1 = mSelectionRect;
        Context mContext = delegate.getContext();
        this.mView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_text_selection, null);
        this.mView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        delegate.attachSelectionPopup(this.mView);
        this.mFadeController = new FadeAnimationController((View) this.mView.getParent(), View.INVISIBLE);
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        this.mScreenWidth = displayMetrics.widthPixels;
        this.mScreenHeight = displayMetrics.heightPixels;
    }

    public void setContentReady() {
        this.mContentsReady = true;
    }

    public void setLocationReady() {
        this.mLocationReady = true;
    }

    public void maybeShow() {
        if (this.mLocationReady && this.mContentsReady) {
            refreshContents();
            relocate();
            this.mFadeController.setVisible(true, 100, null);
        }
    }

    private void refreshContents() {

    }

    public void hide() {
        if (this.mFadeController.getVisible()) {
            this.mContentsReady = false;
            this.mLocationReady = false;
            this.mFadeController.setVisible(false, 275, null);
        }
    }

    private void relocate() {
        SelectionBoundsInfo selectionInfo = getSelectionRect();
        if (selectionInfo != null) {
            Rect selectionBounds = selectionInfo.getBounds();
            this.mSelectionLocation = new SelectionLocation(selectionBounds.left, selectionBounds.right, selectionBounds.top, selectionBounds.bottom, selectionInfo.getHandleLongerAxis(), selectionInfo.mHandleAnchorPoint);
            maybeLayout();
        }
    }

    public void maybeLayout() {
        if (this.mSelectionLocation != null) {
            this.mSelectionLocation.layout();
        }
    }


    private SelectionBoundsInfo getSelectionRect() {
       return delegate.getHighlightScreenRect();

    }

}
