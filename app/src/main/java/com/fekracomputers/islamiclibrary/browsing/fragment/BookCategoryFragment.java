package com.fekracomputers.islamiclibrary.browsing.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.browsing.adapters.BookCategoryRecyclerViewAdapter;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.model.BookCatalogElement;
import com.fekracomputers.islamiclibrary.model.BookCategory;

import java.util.List;

/**
 * A fragment representing a list of {@link BookCategory}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCategoryItemClickListener}
 * interface.
 */
public class BookCategoryFragment
        extends Fragment
        implements BrowsingActivityListingFragment,
        SortListDialogFragment.OnSortDialogListener {
    public static final int GRID_LAYOUT_MANAGER = 0;
    public static final int LINEAR_LAYOUT_MANAGER = 1;
    private static final String TAG = "BookCategoryFragmentRecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "BookCategoryFragmentLayoutManager";
    private static final int SPAN_COUNT = 3;
    private static final String KEY_SHARED_PREF_CATEGORY_LAYOUT_TYPE = "BookCategoryFragmentSharedPrefLayoutKey";
    protected int mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected BookCategoryRecyclerViewAdapter mBookCategoryRecyclerViewAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private OnCategoryItemClickListener mListener;
    private BooksInformationDbHelper booksInformationDbHelper;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookCategoryFragment() {
    }

    public static BookCategoryFragment newInstance() {
        return new BookCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mCurrentLayoutManagerType = sharedPref.getInt(KEY_SHARED_PREF_CATEGORY_LAYOUT_TYPE, LINEAR_LAYOUT_MANAGER);

        //Restore the last layout Type
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = savedInstanceState.getInt(KEY_LAYOUT_MANAGER);
        }
        switch (mCurrentLayoutManagerType) {
            case LINEAR_LAYOUT_MANAGER:
            default:
                mLayoutManager = new LinearLayoutManager(getContext());
                break;
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getContext(),SPAN_COUNT);
        }

        booksInformationDbHelper = BooksInformationDbHelper.getInstance(this.getContext());

        List<BookCategory> categoryList = booksInformationDbHelper.getCategoriesFiltered(null,
                null,
                null,
                mListener.shouldDisplayDownloadedOnly());
        mBookCategoryRecyclerViewAdapter = new BookCategoryRecyclerViewAdapter(categoryList, mListener, getActivity().getPreferences(Context.MODE_PRIVATE), getContext());

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bookcategory_list, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mBookCategoryRecyclerViewAdapter);
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_category, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mBookCategoryRecyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_rearrange) {

            if (mCurrentLayoutManagerType == GRID_LAYOUT_MANAGER) {
                setRecyclerViewLayoutManager(LINEAR_LAYOUT_MANAGER);
                item.setIcon(R.drawable.ic_view_stream_black_24dp);
            } else {//if mCurrentLayoutManagerType == LINEAR_LAYOUT_MANAGER
                setRecyclerViewLayoutManager(GRID_LAYOUT_MANAGER);
                item.setIcon(R.drawable.ic_view_module_black_24dp);
            }

            return true;
        } else if (item.getItemId() == R.id.action_sort) {
            SortListDialogFragment sortListDialogFragment = SortListDialogFragment.newInstance(R.array.category_list_sorting, mBookCategoryRecyclerViewAdapter.getCurrentSortIndex());
            //see this answer http://stackoverflow.com/a/37794319/3061221
            FragmentManager fm = getChildFragmentManager();
            sortListDialogFragment.show(fm, "fragment_sort");
            return true;

        } else return super.onOptionsItemSelected(item);
    }

    @Override
    public void sortMethodSelected(int which) {
        mBookCategoryRecyclerViewAdapter.sortBy(which);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryItemClickListener) {
            mListener = (OnCategoryItemClickListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryItemClickListener");
        }
        if (context instanceof BrowsingActivity) {
            ((BrowsingActivity) context).registerListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ((BrowsingActivity) getActivity()).unRegisterListener(this);

    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(int layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LINEAR_LAYOUT_MANAGER;
        }

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_SHARED_PREF_CATEGORY_LAYOUT_TYPE, layoutManagerType);
        editor.apply();

        mBookCategoryRecyclerViewAdapter.setLayoutManagerType(layoutManagerType);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putInt(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void actionModeDestroyed() {
        mBookCategoryRecyclerViewAdapter.notifyDataSetChanged();
//        mBookCategoryRecyclerViewAdapter.notifyItemRangeChanged(0,
//                mBookCategoryRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_VISABILITY,false));
//        mBookCategoryRecyclerViewAdapter.notifyItemRangeChanged(0,
//                mBookCategoryRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_CHECKED,false));
    }

    @Override
    public void actionModeStarted() {
        mBookCategoryRecyclerViewAdapter.notifyDataSetChanged();

//        mBookCategoryRecyclerViewAdapter.notifyItemRangeChanged(0,
//                mBookCategoryRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_VISABILITY,true));
    }

    @Override
    public void switchTodownloadedOnly(boolean checked) {
        mBookCategoryRecyclerViewAdapter.changeDataset(booksInformationDbHelper.getCategoriesFiltered(null, null, BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER, checked));
        mBookCategoryRecyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void reAcquireCursors() {
        List<BookCategory> categoryList = booksInformationDbHelper.getCategoriesFiltered(null,
                null,
                null,
                mListener.shouldDisplayDownloadedOnly());
        mBookCategoryRecyclerViewAdapter.changeDataset(categoryList);
        mBookCategoryRecyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void closeCursors() {
        //do nothing since we don't hold any cursor
    }

    @Override
    public void selecteItem(BookCatalogElement bookCatalogElement) {
        mRecyclerView.scrollToPosition(mBookCategoryRecyclerViewAdapter.getPositonById(bookCatalogElement.getId()));

    }


    @Override
    public void BookDownloadStatusUpdate(int bookId, int downloadStatus) {
        boolean downloadedOnly = mListener.shouldDisplayDownloadedOnly();
        int catId = booksInformationDbHelper.getBookCategoryId(bookId);
        if (downloadedOnly) {
            mBookCategoryRecyclerViewAdapter
                    .add(booksInformationDbHelper
                            .getCategoriesFiltered(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + "=?",
                                    new String[]{String.valueOf(catId)},
                                    null,
                                    false)
                            .get(0));
        }
        else
        {
            mBookCategoryRecyclerViewAdapter.setCategoryDownloadStatus(catId,downloadStatus);
        }
//
//        mBookCategoryRecyclerViewAdapter.notifyItemRangeChanged(0,
//                mBookCategoryRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_DOWNLOAD_STATUS,bookId,downloadStatus));
    }

    @Override
    public void bookSelectionStatusUpdate() {
        mBookCategoryRecyclerViewAdapter.notifyDataSetChanged();
//
//        mBookCategoryRecyclerViewAdapter.notifyItemRangeChanged(0,
//                mBookCategoryRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_CHECKED));
    }

    @Override
    public int getType() {
        return BrowsingActivity.BOOK_CATEGORY_FRAGMENT_TYPE;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCategoryItemClickListener {
        void OnCategoryItemClick(BookCategory bookCategory);

        boolean OnCategoryItemLongClicked(int categoryId);

        void onCategorySelected(int categoryId, boolean checked);

        boolean isCategorySelected(int categoryId);

        boolean isInSelectionMode();

        boolean shouldDisplayDownloadedOnly();
    }

}
