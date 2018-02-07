package com.fekracomputers.islamiclibrary.browsing.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.AllBooksTab;
import com.fekracomputers.islamiclibrary.model.AuthoursTab;
import com.fekracomputers.islamiclibrary.model.BookCatalogElement;
import com.fekracomputers.islamiclibrary.model.CategoryTab;

import java.util.ArrayList;

import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.AUTHOR_LIST_FRAGMENT_TYPE;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_CATEGORY_FRAGMENT_TYPE;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_INFORMATION_TYPE;
import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.BOOK_LIST_FRAGMENT_TYPE;

/**
 * A {@link Fragment} displaying view pager to display categories or authors lists.
 * Activities that contain this fragment must implement the
 * <p>
 * to handle interaction events.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 * The {@link android.support.v4.view.PagerAdapter} that will provide
 * fragments for each of the sections. We use a
 * {@link FragmentPagerAdapter} derivative, which will keep every
 * loaded fragment in memory. If this becomes too memory intensive, it
 * may be best to switch to a
 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
 */
public class LibraryFragment extends Fragment {

    private static final String SHARED_PREF_FILTER_PAGER_CURRENT_ITEM_KEY = "SHARED_PREF_FILTER_PAGER_CURRENT_ITEM_KEY";
    @NonNull
    ArrayList<BookCatalogElement> bookCatalogElements = new ArrayList<>();
    @Nullable
    private OnBookFilterPagerPageChangedListener mListener;
    private ViewPager mViewPager;

    public LibraryFragment() {
        bookCatalogElements.add(new CategoryTab());
        bookCatalogElements.add(new AuthoursTab());
        bookCatalogElements.add(new AllBooksTab());
    }


    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter_pager, container, false);
        mViewPager = rootView.findViewById(R.id.category_filter_pager);
        TabLayout mTabLayout = rootView.findViewById(R.id.tabs);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mViewPager.setCurrentItem(sharedPref.getInt(SHARED_PREF_FILTER_PAGER_CURRENT_ITEM_KEY, 0));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mListener.OnFilterAllSelected(position == 2);
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SHARED_PREF_FILTER_PAGER_CURRENT_ITEM_KEY, position);
                editor.apply();
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        ViewCompat.setLayoutDirection(mTabLayout, ViewCompat.LAYOUT_DIRECTION_LTR);
//        makTabsFixed(mTabLayout);
//        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
//            mTabLayout.getTabAt(i).setIcon(bookCatalogElements.get(i).getIconDrawableId());
//        }
        return rootView;
    }

    private void makTabsFixed(@NonNull TabLayout tabLayout) {
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
        int tabCount = tabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            View tab = slidingTabStrip.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();
            layoutParams.weight = 1;
            tab.setLayoutParams(layoutParams);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBookFilterPagerPageChangedListener) {
            mListener = (OnBookFilterPagerPageChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBookFilterPagerPageChangedListener");
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.registerPagerFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.unRegisterPagerFragment();
        mListener = null;

    }

    public void switchTo(int PagerFragmentType) {
        switch (PagerFragmentType) {
            case AUTHOR_LIST_FRAGMENT_TYPE:
                mViewPager.setCurrentItem(1);
                break;
            case BOOK_CATEGORY_FRAGMENT_TYPE:
                mViewPager.setCurrentItem(0);
                break;
            case BOOK_LIST_FRAGMENT_TYPE:
                mViewPager.setCurrentItem(2);
            case BOOK_INFORMATION_TYPE:
                throw new IllegalArgumentException("Pager doesn't hold this item");

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.mayBeSetTitle("");
    }

    public interface OnBookFilterPagerPageChangedListener {
        void OnFilterAllSelected(boolean b);

        void mayBeSetTitle(String s);

        void registerPagerFragment(LibraryFragment libraryFragment);

        void unRegisterPagerFragment();

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public int getCount() {
            return bookCatalogElements.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(bookCatalogElements.get(position).getName());
        }

        @Override
        public Fragment getItem(int position) {
            return bookCatalogElements.get(position).getNewFragment();
        }


    }


}






