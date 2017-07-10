package com.fekracomputers.islamiclibrary.browsing.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.fekracomputers.islamiclibrary.browsing.adapters.BookListRecyclerViewAdapter;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.databases.SQL;
import com.fekracomputers.islamiclibrary.model.BookCatalogElement;
import com.fekracomputers.islamiclibrary.search.model.FTS.Util;

/**
 * A fragment to display a list of books filtered by author or category
 * Activities that contain this fragment must implement the
 * {@link BookCardEventsCallback} interface
 * to handle interaction events.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment and pass the required type of filter from the constant fields.
 */
public class BookListFragment
        extends Fragment implements
        BrowsingActivityListingFragment,
        SortListDialogFragment.OnSortDialogListener {
    public static final String FILTERTYPE = "filter_type";
    public static final int FILTERALL = 0;
    public static final int FILTERBYCATEGORY = 1;
    public static final int FILTERBYAuthour = 2;
    public static final String KEY_CAT_ID = "category_id";
    public static final String KEY_AUTOUR_ID = "autour_id";
    public static final String KEY_LAYOUT_MANAGER = "BookListFragmentLayoutManager";
    public static final int GRID_LAYOUT_MANAGER = 0;
    public static final int LINEAR_LAYOUT_MANAGER = 1;
    private static final String TAG = "BookList";
    private static final int SPAN_COUNT = 3;
    private static final String KEY_BOOKK_LIST_SORT_INDEX_ONLY = "BookListFragmentSortIndex";
    private static final String KEY_SHARED_PREF_BOOK_LAYOUT_TYPE = "BookListFragmentLayoutType";
    private static final String DOTSEPARATOR = ".";
    private static final String[] mOrderBy = new String[]{
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR,
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE
    };
    protected int mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected BookListRecyclerViewAdapter bookListRecyclerViewAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private BookCardEventsCallback mListener;
    private BooksInformationDbHelper booksInformationDbHelper;
    private int filterType;
    private int id;
    private int mCurrentSortIndex;
    private String mSearchQuery;
    private String title;
    private int mSavedScrollPositionBeforSearch;


    public static BookListFragment newInstance(int filterType, int id) {
        return newInstance(filterType, id, null);

    }

    /**
     * @param filterType either {@link BookListFragment#FILTERBYAuthour} or {@link BookListFragment#FILTERBYCATEGORY}
     *                   or {@link BookListFragment#FILTERALL} returns all books
     * @param id         autour id in case {@param filterType} = {@link BookListFragment#FILTERBYAuthour}
     *                   or Category id in case {@param filterType} = {@link BookListFragment#FILTERBYCATEGORY}
     *                   neglected otherwise
     * @return a new fragment displaying books as requested
     */
    public static BookListFragment newInstance(int filterType, int id, String name) {

        Bundle args = new Bundle();
        args.putInt(FILTERTYPE, filterType);
        switch (filterType) {
            case FILTERALL:
                break;
            case FILTERBYCATEGORY:
                args.putInt(KEY_CAT_ID, id);
                args.putString(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE, name);
                break;
            case FILTERBYAuthour:
                args.putInt(KEY_AUTOUR_ID, id);
                args.putString(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME, name);
                break;
            default:
        }
        BookListFragment bookListFragment = new BookListFragment();
        bookListFragment.setArguments(args);
        return bookListFragment;
    }

    public int getCurrentLayoutManagerType() {
        return mCurrentLayoutManagerType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        filterType = args.getInt(FILTERTYPE);
        switch (filterType) {
            case FILTERALL:
                break;
            case FILTERBYCATEGORY:
                id = args.getInt(KEY_CAT_ID, 0);
                title = args.getString(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE);

                break;
            case FILTERBYAuthour:
                id = args.getInt(KEY_AUTOUR_ID, 0);
                title = args.getString(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME);
                break;
            default:
        }
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mCurrentLayoutManagerType = sharedPref.getInt(KEY_SHARED_PREF_BOOK_LAYOUT_TYPE, GRID_LAYOUT_MANAGER);

        //Restore the last layout Type
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = savedInstanceState.getInt(KEY_LAYOUT_MANAGER);
        }

        switch (mCurrentLayoutManagerType) {
            case LINEAR_LAYOUT_MANAGER:
            default:
                mLayoutManager = new LinearLayoutManager(getContext());
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getContext());
        }

        mCurrentSortIndex = sharedPref.getInt(KEY_BOOKK_LIST_SORT_INDEX_ONLY, 0);

        booksInformationDbHelper = BooksInformationDbHelper.getInstance(getContext());


        boolean downloadedOnly = mListener.shouldDisplayDownloadedOnly();
        Cursor bookListCursor = getCursor(downloadedOnly);

        String idCoulmnName = BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID;

        bookListRecyclerViewAdapter = new BookListRecyclerViewAdapter(getContext(), bookListCursor, idCoulmnName, mListener);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mListener != null && title != null) {
            mListener.mayBeSetTitle(title);
        }
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(bookListRecyclerViewAdapter);
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book_list, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getString(R.string.hint_search_books_names));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if ((mSearchQuery == null || mSearchQuery.isEmpty()) && !(query == null || query.isEmpty())) {
                    //first click on search icon
                    mSearchQuery = query;
                    mSavedScrollPositionBeforSearch = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
                } else if (!(mSearchQuery == null || mSearchQuery.isEmpty()) && (query == null || query.isEmpty())) {
                    //clearing the search query
                    mSearchQuery = query;
                    Cursor bookListCursor = getCursor(mListener.shouldDisplayDownloadedOnly());
                    bookListRecyclerViewAdapter.changeCursor(bookListCursor);
                    (mRecyclerView.getLayoutManager()).scrollToPosition(mSavedScrollPositionBeforSearch);

                } else {
                    mSearchQuery = query;
                    Cursor bookListCursor = getCursor(mListener.shouldDisplayDownloadedOnly());
                    bookListRecyclerViewAdapter.changeCursor(bookListCursor);
                }
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
            } else {//if mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER
                setRecyclerViewLayoutManager(GRID_LAYOUT_MANAGER);
                item.setIcon(R.drawable.ic_view_module_black_24dp);
            }

            return true;
        } else if (item.getItemId() == R.id.action_sort) {
            SortListDialogFragment sortListDialogFragment = SortListDialogFragment.newInstance(R.array.book_list_sorting, mCurrentSortIndex);
            //see this answer http://stackoverflow.com/a/37794319/3061221
            FragmentManager fm = getChildFragmentManager();
            sortListDialogFragment.show(fm, SortListDialogFragment.TAG_FRAGMENT_SORT);
            return true;

        } else return super.onOptionsItemSelected(item);
    }

    @Override
    public void sortMethodSelected(int which) {
        mCurrentSortIndex = which;
        boolean downloadedOnly = mListener.shouldDisplayDownloadedOnly();
        Cursor bookListCursor = getCursor(downloadedOnly);
        bookListRecyclerViewAdapter.changeCursor(bookListCursor);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_BOOKK_LIST_SORT_INDEX_ONLY, which);
        editor.apply();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookCardEventListener) {
            mListener = ((BookCardEventListener) context).getBookCardEventCallback();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BookCardEventsCallback");
        }
        if (context instanceof BrowsingActivity) {
            ((BookCardEventListener) context).registerListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ((BookCardEventListener) getActivity()).unRegisterListener(this);
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

                //mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, 8, true, getContext()));
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                //DividerItemDecoration decor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                //decor.setDrawable();
                //mRecyclerView.addItemDecoration(decor);
                mCurrentLayoutManagerType = LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LINEAR_LAYOUT_MANAGER;
        }
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_SHARED_PREF_BOOK_LAYOUT_TYPE, layoutManagerType);
        editor.apply();

        //this line must be first
        bookListRecyclerViewAdapter.setLayoutManagerType(layoutManagerType);
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
        bookListRecyclerViewAdapter.notifyDataSetChanged();
//
//        bookListRecyclerViewAdapter.notifyItemRangeChanged(0,
//                bookListRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_VISABILITY, false));
//        bookListRecyclerViewAdapter.notifyItemRangeChanged(0,
//                bookListRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_CHECKED, false));
    }

    @Override
    public void actionModeStarted() {
        bookListRecyclerViewAdapter.notifyDataSetChanged();
//
//        bookListRecyclerViewAdapter.notifyItemRangeChanged(0,
//                bookListRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_VISABILITY, true));
    }

    @Override
    public void switchTodownloadedOnly(boolean checked) {
        Cursor bookListCursor = getCursor(checked);
        bookListRecyclerViewAdapter.changeCursor(bookListCursor);
    }

    @Override
    public void reAcquireCursors() {
        boolean downloadedOnly = mListener.shouldDisplayDownloadedOnly();
        Cursor bookListCursor;
        if (mSearchQuery != null && !mSearchQuery.isEmpty()) {
            bookListCursor = getCursor(downloadedOnly);

        } else {
            bookListCursor = getCursor(downloadedOnly);
        }
        bookListRecyclerViewAdapter.changeCursor(bookListCursor);
        bookListRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeCursors() {
        bookListRecyclerViewAdapter.getCursor().close();
    }

    @Override
    public void selecteItem(BookCatalogElement bookCatalogElement) {

    }

    @Override
    public void BookDownloadStatusUpdate(int bookId, int downloadStatus) {
        reAcquireCursors();
//
//        bookListRecyclerViewAdapter.notifyItemRangeChanged(0,
//                bookListRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_DOWNLOAD_STATUS,bookId, downloadStatus));
    }

    @Override
    public void bookSelectionStatusUpdate() {
        bookListRecyclerViewAdapter.notifyDataSetChanged();
//
//        bookListRecyclerViewAdapter.notifyItemRangeChanged(0,
//                bookListRecyclerViewAdapter.getItemCount(),
//                new RecyclerViewPartialUpdatePayload(RecyclerViewPartialUpdatePayload.UPDATE_CHECKED));
    }

    @Override
    public int getType() {
        return BrowsingActivity.BOOK_LIST_FRAGMENT_TYPE;
    }


    private Cursor getCursor(boolean downloadedOnly) {
        if (mSearchQuery == null || mSearchQuery.isEmpty()) {
            Cursor bookListCursor;
            switch (filterType) {
                case FILTERBYCATEGORY:
                    bookListCursor = booksInformationDbHelper.getBooksFiltered(
                            BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID + "=?",
                            new String[]{String.valueOf(id)},
                            mOrderBy[mCurrentSortIndex],
                            downloadedOnly, null);
                    break;
                case FILTERBYAuthour:
                    bookListCursor = booksInformationDbHelper.getBooksFiltered(
                            BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID + "=?",
                            new String[]{String.valueOf(id)},
                            mOrderBy[mCurrentSortIndex],
                            downloadedOnly, null);
                    break;
                default:
                case FILTERALL:
                    bookListCursor = booksInformationDbHelper.getBooksFiltered(null, null, mOrderBy[mCurrentSortIndex],
                            downloadedOnly, null);
                    break;
            }
            return bookListCursor;
        } else
            return getCursor(mSearchQuery, downloadedOnly);
    }

    private Cursor getCursor(String query, boolean downloadedOnly) {
        Cursor bookListCursor;
        switch (filterType) {
            case FILTERBYCATEGORY:
                bookListCursor = booksInformationDbHelper.getBooksFiltered(
                        BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID + "=? " + SQL.AND
                                + BooksInformationDbHelper.getFTSSelectionStringForBooks()
                        ,
                        new String[]{String.valueOf(id), Util.getSearchPrefixQueryString(query)},
                        mOrderBy[mCurrentSortIndex],
                        downloadedOnly, null);
                break;
            case FILTERBYAuthour:
                bookListCursor = booksInformationDbHelper.getBooksFiltered(
                        BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID + "=? " + SQL.AND
                                + BooksInformationDbHelper.getFTSSelectionStringForBooks(),
                        new String[]{String.valueOf(id), Util.getSearchPrefixQueryString(query)},
                        mOrderBy[mCurrentSortIndex],
                        downloadedOnly, null);
                break;
            default:
            case FILTERALL:
                bookListCursor = booksInformationDbHelper.getBooksFiltered(
                        BooksInformationDbHelper.getFTSSelectionStringForBooks(),
                        new String[]{Util.getSearchPrefixQueryString(query)},
                        mOrderBy[mCurrentSortIndex],
                        downloadedOnly, null);
                break;
        }
        return bookListCursor;
    }

}
