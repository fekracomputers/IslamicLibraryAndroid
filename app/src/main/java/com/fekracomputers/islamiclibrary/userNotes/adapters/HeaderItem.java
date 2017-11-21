package com.fekracomputers.islamiclibrary.userNotes.adapters;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

/**
 * Created by Mohammad on 19/11/2017.
 */

public class HeaderItem extends Item<HeaderItem.HeaderViewHolder> {

    @StringRes
    private int titleStringResId;

    @StringRes
    private int subtitleResId;

    @DrawableRes
    private int iconResId;

    private View.OnClickListener onIconClickListener;

    public HeaderItem(@StringRes int titleStringResId) {
        this(titleStringResId, 0);
    }

    public HeaderItem(@StringRes int titleStringResId, @StringRes int subtitleResId) {
        this(titleStringResId, subtitleResId, 0, null);
    }

    public HeaderItem(@StringRes int titleStringResId,
                      @StringRes int subtitleResId,
                      @DrawableRes int iconResId,
                      View.OnClickListener onIconClickListener) {
        this.titleStringResId = titleStringResId;
        this.subtitleResId = subtitleResId;
        this.iconResId = iconResId;
        this.onIconClickListener = onIconClickListener;
    }

    @NonNull
    public HeaderItem.HeaderViewHolder createViewHolder(@NonNull View itemView) {
        return new HeaderItem.HeaderViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull HeaderViewHolder viewHeaderViewHolder, int position) {
        viewHeaderViewHolder.title.setText(titleStringResId);
        if (subtitleResId > 0) {
            viewHeaderViewHolder.subtitle.setVisibility(View.VISIBLE);
            viewHeaderViewHolder.subtitle.setText(subtitleResId);
        } else {
            viewHeaderViewHolder.subtitle.setVisibility(View.GONE);
        }
        if (iconResId > 0) {
            viewHeaderViewHolder.icon.setVisibility(View.VISIBLE);
            viewHeaderViewHolder.icon.setImageResource(iconResId);
            viewHeaderViewHolder.icon.setOnClickListener(onIconClickListener);
        } else {
            viewHeaderViewHolder.icon.setVisibility(View.GONE);
            viewHeaderViewHolder.icon.setOnClickListener(null);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.item_notes_header;
    }


    class HeaderViewHolder extends ViewHolder {
        TextView title;
        TextView subtitle;
        ImageView icon;

        public HeaderViewHolder(@NonNull View rootView) {
            super(rootView);
            title = rootView.findViewById(R.id.title);
            subtitle = rootView.findViewById(R.id.subtitle);
            icon = rootView.findViewById(R.id.icon);
        }
    }
}
