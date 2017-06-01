package com.fekracomputers.islamiclibrary.reading.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.reading.SearchNavigationStatus;
import com.fekracomputers.islamiclibrary.settings.easy_feedback.components.Utils;


public class SearchScrubBar extends FrameLayout {
    private final OnClickListener mDelegateOnClickListener;
    private View mExitSearch;
    private TextView mMatches;
    private View mNext;
    private View mPrevious;
    private OnClickListener mRealOnClickListener;
    private final int mSearchScrubBarTextActiveColor;
    private final int mSearchScrubBarTextInactiveColor;

    public SearchScrubBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchScrubBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.mDelegateOnClickListener = new OnClickListener() {
            public void onClick(View view) {
                if (SearchScrubBar.this.mRealOnClickListener != null) {
                    SearchScrubBar.this.mRealOnClickListener.onClick(view);
                }
            }
        };
         this.mSearchScrubBarTextActiveColor = Utils.getColorFromAttr(context, R.attr.searchScrubBarTextActiveColor, 0xFF888888);
         this.mSearchScrubBarTextInactiveColor = Utils.getColorFromAttr(context, R.attr.searchScrubBarTextInactiveColor, 0xFF888888);
    }

    public View getPreviousButton() {
        return this.mPrevious;
    }

    public View getNextButton() {
        return this.mNext;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mMatches = (TextView) findViewById(R.id.matchIndex);
    }

    public void setupPagingDirection(boolean isArabic) {
        if (isArabic) {
            this.mPrevious = findViewById(R.id.proceed_right);
            this.mNext = findViewById(R.id.proceed_left);
        } else {
            this.mPrevious = findViewById(R.id.proceed_left);
            this.mNext = findViewById(R.id.proceed_right);
        }
        Resources res = getResources();
        this.mPrevious.setContentDescription(res.getString(R.string.previous_result_button));
        this.mNext.setContentDescription(res.getString(R.string.next_result_button));
        this.mPrevious.setOnClickListener(this.mDelegateOnClickListener);
        this.mNext.setOnClickListener(this.mDelegateOnClickListener);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mRealOnClickListener = onClickListener;
    }

    public void setPreviousButtonEnabled(boolean enabled) {
        this.mPrevious.setEnabled(enabled);
        this.mPrevious.setAlpha(enabled ? 1.0f : 0.3f);
    }

    public void setNextButtonEnabled(boolean enabled) {
        this.mNext.setEnabled(enabled);
        this.mNext.setAlpha(enabled ? 1.0f : 0.3f);
    }

    public void setSearchBarMatchText(int numMatchesBeforeCurrenPage, int numMatches, boolean currentPageContainsMatch) {
        if (numMatches > 0) {
            int numMatchesToReport = numMatchesBeforeCurrenPage + 1;
            this.mMatches.setText(
                    getResources()
                            .getQuantityString(R.plurals.search_num_results_before,
                                    numMatches,
                                    new Object[]{Integer.valueOf(numMatchesToReport),Integer.valueOf(numMatches)}));

            if (currentPageContainsMatch) {
                this.mMatches.setTextColor(this.mSearchScrubBarTextActiveColor);
            } else {
                this.mMatches.setTextColor(this.mSearchScrubBarTextInactiveColor);
            }
            this.mMatches.setVisibility(VISIBLE);
            return;
        }
        hideSearchBarMatchText();
    }

    public void setNavigationStatus(SearchNavigationStatus status) {
        setPreviousButtonEnabled(status.hasPrevious);
        setNextButtonEnabled(status.hasNext);
        setSearchBarMatchText(status.numMatchesBeforeSpread, status.numMatches, status.currentSpreadContainsMatch);
    }

    public void hideSearchBarMatchText() {
        this.mMatches.setVisibility(INVISIBLE);
    }

    public void setMatchDescriptionOnClickListener(OnClickListener matchDescriptionOnClickListener) {
        this.mMatches.setOnClickListener(matchDescriptionOnClickListener);
    }

    public void setExitSearchListener(OnClickListener exitSearchListener) {
        this.mExitSearch = findViewById(R.id.exit_search);
        this.mExitSearch.setOnClickListener(exitSearchListener);
    }
}
