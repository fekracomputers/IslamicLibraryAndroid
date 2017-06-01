package com.fekracomputers.islamiclibrary.reading.selection;

import android.content.Context;
import android.view.View;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 2/4/2017.
 */

public interface TextSelectionDelegate {
    Context getContext();
    void attachSelectionPopup(View mView);

    void onContextualMenuItemClicked(int id);

    SelectionPopupImpl.SelectionBoundsInfo getHighlightScreenRect();
}
