package com.fekracomputers.islamiclibrary.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 26/4/2017.
 */

public class BookInformationDetailsCard extends LinearLayout {

    public BookInformationDetailsCard(Context context) {
        this(context, null);
    }

    public BookInformationDetailsCard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookInformationDetailsCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookInformationDetailsCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public BookInformationDetailsCard(Context context, CharSequence header, CharSequence body, boolean isGrey, View.OnClickListener moreOnClickListener) {
        super(context);
        initialize();
        setup(header, body, isGrey, moreOnClickListener);
    }


    private void initialize() {
        inflate(getContext(), R.layout.widget_book_info_card, this);
        setOrientation(VERTICAL);


    }

    public void setup(CharSequence header, CharSequence body, boolean isGrey, View.OnClickListener moreOnClickListener) {
        setBackgroundResource(isGrey ?
                R.color.infoPage_details_gray :
                R.color.infoPage_details_white);

        final TextView mMoreTextView = (TextView) findViewById(R.id.more_tv);
        mMoreTextView.setOnClickListener(moreOnClickListener);

        TextView headerTextView = (TextView) findViewById(R.id.header_tv);
        headerTextView.setText(header);
        EllipsisTextView detailsTextView = (EllipsisTextView) findViewById(R.id.details_tv);
        detailsTextView.setText(body);

        detailsTextView.addEllipsesListener(new EllipsisTextView.EllipsisListener() {
            @Override
            public void ellipsisStateChanged(boolean ellipses) {
                mMoreTextView.setVisibility(ellipses ? VISIBLE : GONE);
            }
        });

        headerTextView.setTextColor(getContext().
                getResources().
                getColor(isGrey ?
                        R.color.info_page_grey_background_header_text_color :
                        R.color.info_page_white_background_header_text_color));


    }

}
