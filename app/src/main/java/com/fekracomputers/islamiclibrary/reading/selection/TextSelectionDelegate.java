package com.fekracomputers.islamiclibrary.reading.selection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 2/4/2017.
 */

public interface TextSelectionDelegate {
    @NonNull
    Context getContext();
    void attachSelectionPopup(View mView);

    void onContextualMenuItemClicked(int id);

    @NonNull
    SelectionPopupImpl.SelectionBoundsInfo getHighlightScreenRect();
}
