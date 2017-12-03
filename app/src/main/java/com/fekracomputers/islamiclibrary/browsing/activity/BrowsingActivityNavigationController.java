package com.fekracomputers.islamiclibrary.browsing.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.MenuItem;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookInformationFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.LibraryFragment;

import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_INFORMATION_FRAGMENT_ADDED;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_INFORMATION_FRAGMENT_TAG;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_LIST_FRAGMENT_ADDED;

/**
 * Created by Mohammad on 10/19/2017.
 */

abstract class BrowsingActivityNavigationController {
    private static final String PREF_BOTTOM_NAVIGATION_CURRENT_ITEM_ID = "PREF_BOTTOM_NAVIGATION_CURRENT_ITEM_ID";
    FragmentManager fragmentManager;
    private LibraryFragment pagerFragment;
    int paneNumber;
    int lastButtomSheetCheckedItemId;
    protected SparseArray<Fragment> fragments = new SparseArray<>(3);
    protected BrowsingActivityControllerListener listener;
    FragmentManager.OnBackStackChangedListener backStackChangedListener;
    @Nullable
    private BottomNavigationView bottomNavigationView;
    private int oldPanNumbers;
    private boolean fromRotation;
    private FragmentActivity activity;

    BrowsingActivityNavigationController(int oldPanNumbers, FragmentManager fragmentManager, boolean fromRotation, BrowsingActivity activity, BottomNavigationView bottomNavigationView, BrowsingActivityControllerListener listener) {
        this.oldPanNumbers = oldPanNumbers;
        this.fragmentManager = fragmentManager;
        this.fromRotation = fromRotation;
        this.activity = activity;
        this.bottomNavigationView = bottomNavigationView;
        this.listener = listener;
    }

    public static BrowsingActivityNavigationController create(int paneNumber,
                                                              int oldPanNumbers,
                                                              FragmentManager fragmentManager,
                                                              boolean fromRotation,
                                                              BrowsingActivity browsingActivity,
                                                              BottomNavigationView bottomNavigationView,
                                                              BrowsingActivityControllerListener listener) {
        switch (paneNumber) {
            case 1: {
                if (oldPanNumbers <= 1)
                    return new BrowsingActivityNavigationControllerSinglePaneAlways(oldPanNumbers, fragmentManager, fromRotation, browsingActivity, bottomNavigationView, listener);
                else
                    return new BrowsingActivityNavigationControllerSinglePane(oldPanNumbers, fragmentManager, fromRotation, browsingActivity, bottomNavigationView, listener);
            }
            case 2:
                return new BrowsingActivityNavigationControllerDualPan(oldPanNumbers, fragmentManager, fromRotation, browsingActivity, bottomNavigationView, listener);
            case 3:
                return new BrowsingActivityNavigationControllerTriplePan(oldPanNumbers, fragmentManager, fromRotation, browsingActivity, bottomNavigationView, listener);
            default:
                return null;
        }
    }

    void intiializePans() {
        restoreLastCheckedItem();
        if (!fromRotation) {
            intitalizePansFresh(fragmentManager);
        } else {//after screen rotation
            intializePansAfterRotation(oldPanNumbers, fragmentManager);
        }
    }

    private void restoreLastCheckedItem() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        lastButtomSheetCheckedItemId = sharedPref.getInt(PREF_BOTTOM_NAVIGATION_CURRENT_ITEM_ID, R.id.bottom_library);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(lastButtomSheetCheckedItemId);
        }
    }

    protected abstract void intializePansAfterRotation(int oldPanNumbers, FragmentManager fragmentManager);

    protected abstract void intitalizePansFresh(FragmentManager fragmentManager);

    void pushBookListFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.filter_pager_container, fragment, BrowsingActivity.BOOK_LIST_FRAGMENT_TAG)
                .addToBackStack(BOOK_LIST_FRAGMENT_ADDED)
                .commit();
    }

    void pushBookInformationFragment(Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.filter_pager_container, fragment, BOOK_INFORMATION_FRAGMENT_TAG)
                .addToBackStack(BOOK_INFORMATION_FRAGMENT_ADDED)
                .commit();

    }

    boolean handleButtomNavigationItem(MenuItem item) {
        if (!item.isChecked()) {
            saveBottomBarPosition(item);
            return switchBottomNavigationTo(item.getItemId());
        } else {
            return false;
        }
    }

    protected abstract boolean switchBottomNavigationTo(int itemId);

    private void saveBottomBarPosition(MenuItem item) {
        lastButtomSheetCheckedItemId = item.getItemId();
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PREF_BOTTOM_NAVIGATION_CURRENT_ITEM_ID, item.getItemId());
        editor.apply();
    }


    void registerPagerFragment(LibraryFragment libraryFragment) {
        pagerFragment = libraryFragment;
    }

    void unRegisterPagerFragment() {
        pagerFragment = null;
    }

    boolean shouldCloseSelectionMode() {
        return pagerFragment != null && pagerFragment.isVisible();
    }


    abstract void showBookInformationFragment(BookInformationFragment bookInformationFragment);

    public abstract void showCategoryDetails(Fragment fragment);

    public abstract void showAuthorFragment(BookListFragment fragment);

    public void switchPagerTo(int pagerFragmentType) {
        if (lastButtomSheetCheckedItemId == R.id.bottom_library)
            pagerFragment.switchTo(pagerFragmentType);
    }

    void onDestroy() {
        if (backStackChangedListener != null)
            fragmentManager.removeOnBackStackChangedListener(backStackChangedListener);
    }

    public void showCollectionDetails(BookListFragment fragment) {

    }


    public interface BrowsingActivityControllerListener {
        public void setAppbarExpanded(boolean expanded);

        void setUpNavigation(boolean b);
    }


}
