package com.fekracomputers.islamiclibrary.browsing.activity;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookInformationFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.userNotes.GlobalUserNotesFragment;
import com.fekracomputers.islamiclibrary.homeScreen.fragment.HomeFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.LibraryFragment;

import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_INFORMATION_FRAGMENT_ADDED;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_INFORMATION_FRAGMENT_TAG;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_LIST_FRAGMENT_TAG;

/**
 * Created by Mohammad on 10/19/2017.
 */

class BrowsingActivityNavigationControllerSinglePane extends BrowsingActivityNavigationController {


    public BrowsingActivityNavigationControllerSinglePane(int oldPanNumbers, FragmentManager fragmentManager, boolean fromRotation, BrowsingActivity browsingActivity, BottomNavigationView bottomNavigationView, BrowsingActivityControllerListener listener) {
        super(oldPanNumbers, fragmentManager, fromRotation, browsingActivity, bottomNavigationView, listener);
        paneNumber = 1;
        backStackChangedListener = () -> {
            if (fragmentManager.getBackStackEntryCount() == 0) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                listener.setUpNavigation(false);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
                listener.setUpNavigation(true);
            }
        };
        fragmentManager.addOnBackStackChangedListener(backStackChangedListener);
    }

    @Override
    protected void intializePansAfterRotation(int oldPanNumbers, FragmentManager fragmentManager) {
        if (oldPanNumbers != paneNumber) {
            if (oldPanNumbers == 3) {
                Fragment oldBookList = fragmentManager.findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);
                //First remove the fragment from its container
                if (oldBookList != null) {
                    fragmentManager.beginTransaction().remove(oldBookList).commitNow();
                    pushBookListFragment(oldBookList);
                }

                Fragment oldBookInfo = fragmentManager.findFragmentByTag(BOOK_INFORMATION_FRAGMENT_TAG);
                if (oldBookInfo != null) {
                    fragmentManager.beginTransaction().remove(oldBookInfo).commitNow();
                    pushBookInformationFragment(oldBookInfo);
                }

            } else if (oldPanNumbers == 2) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment oldBookList = fragmentManager.findFragmentByTag(BOOK_INFORMATION_FRAGMENT_TAG);
                if (oldBookList != null) fragmentTransaction.remove(oldBookList);
                fragmentTransaction.commit();

            }
        }
    }

    @Override
    protected void intitalizePansFresh(FragmentManager fragmentManager) {
        switchBottomNavigationTo(lastButtomSheetCheckedItemId);
    }

    @Override
    protected boolean switchBottomNavigationTo(int itemId) {
        Fragment fragment;
        if (fragments.get(itemId) == null) {
            switch (itemId) {
                case R.id.bottom_nav_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.bottom_library:
                default:
                    fragment = new LibraryFragment();
                    break;
//                case R.id.bottom_nav_search:
//                    fragment = new AdvancedSearchFragment();
//                    break;
                case R.id.bottom_nav_user_notes:
                    fragment=GlobalUserNotesFragment.newInstance();
                    break;
            }
            fragments.put(itemId, fragment);
        } else {
            fragment = fragments.get(itemId);
        }

        fragments.put(itemId, fragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.filter_pager_container, fragment);
        fragmentTransaction.commit();
        return true;
    }

    void showBookInformationFragment(BookInformationFragment bookInformationFragment) {
        pushBookInformationFragment(bookInformationFragment);
        listener.setAppbarExpanded(true);
    }

    void pushBookInformationFragment(Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.filter_pager_container, fragment, BOOK_INFORMATION_FRAGMENT_TAG)
                .addToBackStack(BOOK_INFORMATION_FRAGMENT_ADDED)
                .commit();

    }

    public void showCategoryDetails(Fragment fragment) {
        pushBookListFragment(fragment);
        listener.setAppbarExpanded(true);

    }

    public void showAuthorFragment(BookListFragment fragment) {
        pushBookListFragment(fragment);
        listener.setAppbarExpanded(true);

    }

    @Override
    public void showCollectionDetails(BookListFragment fragment) {
        pushBookListFragment(fragment);
        listener.setAppbarExpanded(true);
    }


}

