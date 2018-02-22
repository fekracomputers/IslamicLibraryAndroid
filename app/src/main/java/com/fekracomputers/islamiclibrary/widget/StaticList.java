package com.fekracomputers.islamiclibrary.widget;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.fekracomputers.islamiclibrary.R;

/**
 * Created by Mohammad on 22/2/2018.
 */

public class StaticList extends LinearLayoutCompat {
    public StaticList(Context context) {
        super(context);
    }

    public StaticList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public StaticList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    public void setAdapter(@NonNull Context context, @LayoutRes int resource,
                           @IdRes int textViewResourceId, @NonNull CharSequence[] objects) {
        ArrayAdapter<CharSequence> list = new ArrayAdapter<>(context,
                resource,
                textViewResourceId,
                objects);
        this.removeAllViews();
        for (int i = 0; i < list.getCount(); i++) {
            View item = list.getView(i, null, null);
            addView(item);
            addView(getSeparatorView());
        }
        removeViewAt(getChildCount() - 1);
    }

    @NonNull
    public View getSeparatorView() {
        View separator = new View(getContext());
        separator.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                4
        ));
        separator.setBackgroundColor(getResources().getColor(R.color.material_grey_200));
        return separator;
    }
}

