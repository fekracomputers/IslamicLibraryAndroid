package com.fekracomputers.islamiclibrary.browsing.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;

/**
 * Created by Mohammad on 10/19/2017.
 */

public class BrowsingActivityNavigationControllerSinglePaneAlways extends BrowsingActivityNavigationControllerSinglePane {
    public BrowsingActivityNavigationControllerSinglePaneAlways(int oldPanNumbers, @NonNull FragmentManager fragmentManager, boolean fromRotation, BrowsingActivity browsingActivity, @NonNull BottomNavigationView bottomNavigationView, @NonNull BrowsingActivityControllerListener listener) {
        super(oldPanNumbers, fragmentManager, fromRotation, browsingActivity, bottomNavigationView, listener);
    }

    @Override
    protected void intializePansAfterRotation(int oldPanNumbers, FragmentManager fragmentManager) {
        //doNothing
    }


}
