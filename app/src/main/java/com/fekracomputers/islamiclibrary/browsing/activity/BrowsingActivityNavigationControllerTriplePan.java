package com.fekracomputers.islamiclibrary.browsing.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookInformationFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.LibraryFragment;

import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_INFORMATION_FRAGMENT_ADDED;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_INFORMATION_FRAGMENT_TAG;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_LIST_FRAGMENT_ADDED;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_LIST_FRAGMENT_TAG;

/**
 * Created by Mohammad on 10/19/2017.
 */

class BrowsingActivityNavigationControllerTriplePan extends BrowsingActivityNavigationController {


    public BrowsingActivityNavigationControllerTriplePan(int oldPanNumbers, FragmentManager fragmentManager, boolean fromRotation, BrowsingActivity browsingActivity, BottomNavigationView bottomNavigationView, BrowsingActivityControllerListener listener) {
        super(oldPanNumbers, fragmentManager, fromRotation, browsingActivity, bottomNavigationView, listener);
        paneNumber = 3;
    }

    @Override
    protected void intializePansAfterRotation(int oldPanNumbers, @NonNull FragmentManager fragmentManager) {
        if (oldPanNumbers != paneNumber) {
            if (oldPanNumbers == 1) {
                Fragment oldBookInfo = fragmentManager.findFragmentByTag(BOOK_INFORMATION_FRAGMENT_TAG);
                if (oldBookInfo != null) {
                    fragmentManager.popBackStackImmediate(BOOK_INFORMATION_FRAGMENT_ADDED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().remove(oldBookInfo).commit();
                    fragmentManager.beginTransaction()
                            .replace(R.id.book_info_container, oldBookInfo, BOOK_INFORMATION_FRAGMENT_TAG)
                            .commit();

                }
                Fragment oldBookList = fragmentManager.findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);
                if (oldBookList != null) {
                    fragmentManager.popBackStackImmediate(BOOK_LIST_FRAGMENT_ADDED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().remove(oldBookList).commit();
                    fragmentManager.beginTransaction()
                            .replace(R.id.book_list_container, oldBookList, BOOK_LIST_FRAGMENT_TAG)
                            .commit();

                }
            }
        }
    }

    @Override
    protected void intitalizePansFresh(@NonNull FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.filter_pager_container, new LibraryFragment());
        fragmentTransaction.commit();
    }

    @Override
    protected boolean switchBottomNavigationTo(int itemId) {
        return false;
    }

    void showBookInformationFragment(BookInformationFragment bookInformationFragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.book_info_container, bookInformationFragment, BOOK_INFORMATION_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    public void showCategoryDetails(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.book_list_container, fragment, BOOK_LIST_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    public void showAuthorFragment(BookListFragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.book_list_container, fragment, BOOK_LIST_FRAGMENT_TAG)
                .commit();
    }

}

