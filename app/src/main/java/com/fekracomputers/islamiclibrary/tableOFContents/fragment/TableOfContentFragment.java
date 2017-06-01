package com.fekracomputers.islamiclibrary.tableOFContents.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseContract;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.Title;
import com.fekracomputers.islamiclibrary.tableOFContents.adapter.HistoryTitlesAdapter;
import com.fekracomputers.islamiclibrary.tableOFContents.adapter.TableOfContentRecyclerViewAdapter;

import java.util.LinkedList;


/**
 * Fragment to display the table of content for a book
 */
public class TableOfContentFragment extends Fragment implements
        HistoryTitlesAdapter.OnTitleHistoryClickListener,
        TableOfContentRecyclerViewAdapter.OnTableOfContentExpandListener {

    private static final java.lang.String KEY_BUILD_HISTORY = "build_history_list";
    /**
     * number of titles to display befor current tilte
     */
    public static final int TITLE_WITHIN_PARENT_SCROLL_DIPLAY_OFFSET = 24*2;
    public TableOfContentRecyclerViewAdapter mTableOfContentRecyclerViewAdapter;
    private LinkedList<Title> mHistoryLinkedList = new LinkedList<>();
    private LinkedList<Title> mBacstackTitlesLinkedList = new LinkedList<>();
    private HistoryTitlesAdapter mHistoryTitlesAdapter;
    private OnTableOfContentTitleClickListener mListener;
    private RecyclerView mHistoryRecyclerView;
    private boolean mInSearchMode;
    private LinkedList<Title> mBeforeEnteringSearchHistory = new LinkedList<>();
    private LinkedList<Title> mBeforeBacstackTitlesLinkedList = new LinkedList<>();

    public TableOfContentFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int bookId, String bookName) {
        return newInstance(bookId, bookName, false, 0, 0);
    }


    public static Fragment newInstance(int bookId, String bookName, boolean buildHistory, int pageId, int titleId) {
        TableOfContentFragment fragment = new TableOfContentFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_BUILD_HISTORY, buildHistory);
        args.putInt(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, bookId);
        args.putInt(BookDatabaseContract.TitlesEntry.COLUMN_NAME_PAGE_ID, pageId);
        args.putInt(BookDatabaseContract.TitlesEntry.COLUMN_NAME_ID, titleId);
        args.putString(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE, bookName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_table_of_content, container, false);


        Bundle bundle = getArguments();
        int bookId = bundle.getInt(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID);
        String bookName = bundle.getString(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE);


        RecyclerView titleRecyclerView = (RecyclerView) rootView.findViewById(R.id.toc_recycler_view);
        LinearLayoutManager tableOfContentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        titleRecyclerView.setLayoutManager(tableOfContentLayoutManager);
        mTableOfContentRecyclerViewAdapter = new TableOfContentRecyclerViewAdapter(getContext(), bookId, this, mListener);
        titleRecyclerView.setAdapter(mTableOfContentRecyclerViewAdapter);
        titleRecyclerView.setHasFixedSize(true);
        DividerItemDecoration tableOfContentsDividerItemDecoration =
                new DividerItemDecoration(getContext(), tableOfContentLayoutManager.getOrientation());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tableOfContentsDividerItemDecoration.setDrawable(getContext().getDrawable(R.drawable.horizontal_separator));
        } else {
            tableOfContentsDividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.horizontal_separator));
        }
        titleRecyclerView.addItemDecoration(tableOfContentsDividerItemDecoration);


        mHistoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.toc_history_recycler_view);
        LinearLayoutManager historyLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mHistoryRecyclerView.setLayoutManager(historyLinearLayoutManager);


        if (bundle.getBoolean(KEY_BUILD_HISTORY)) {
            int pageId = bundle.getInt(BookDatabaseContract.TitlesEntry.COLUMN_NAME_PAGE_ID);
            int titleId = bundle.getInt(BookDatabaseContract.TitlesEntry.COLUMN_NAME_ID);
            BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(getContext(), bookId);
            mHistoryLinkedList = bookDatabaseHelper.buildTableOfContentHistoryToTitle(titleId);
            mTableOfContentRecyclerViewAdapter.displayChildrenOfWithHighlightCurrent(mHistoryLinkedList.peekLast(), titleId);
            tableOfContentLayoutManager.scrollToPositionWithOffset(
                    bookDatabaseHelper.getTitlePositionUnderParent(titleId, mHistoryLinkedList.peekLast().id)-1, TITLE_WITHIN_PARENT_SCROLL_DIPLAY_OFFSET);
        } else {
            mHistoryLinkedList.addLast(Title.createRootTitle(bookName));
        }
        mHistoryTitlesAdapter = new HistoryTitlesAdapter(mHistoryLinkedList, this);


        mHistoryRecyclerView.setAdapter(mHistoryTitlesAdapter);
        mHistoryRecyclerView.scrollToPosition(mHistoryLinkedList.size() - 1);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getContext().getDrawable(R.drawable.arrow_into);
        } else {
            drawable = getContext().getResources().getDrawable(R.drawable.arrow_into);
        }
        dividerItemDecoration.setDrawable(drawable);

        mHistoryRecyclerView.addItemDecoration(dividerItemDecoration);


        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mBacstackTitlesLinkedList.size() > 0 && event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    //back key is equivelant to pressing the previous to last title in history
                    OnTitleHistoryClicked(mHistoryLinkedList.size() - 2);
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }


    @Override
    public void OnTitleHistoryClicked(int pastTitlePosition) {
        //pop the history until the clicked element is the last one
        int removed = 0;
        while (mHistoryLinkedList.size() - 1 > pastTitlePosition) {
            mHistoryLinkedList.removeLast();
            if (!mBacstackTitlesLinkedList.isEmpty()) mBacstackTitlesLinkedList.removeLast();
            removed++;
        }

        mHistoryTitlesAdapter.notifyItemRangeRemoved(pastTitlePosition + 1, removed);
        mTableOfContentRecyclerViewAdapter.displayChildrenOf(mHistoryLinkedList.peekLast());
    }


    @Override
    public void OnOnTableOfContentExpandClicked(Title title) {
        mHistoryLinkedList.addLast(title);
        mBacstackTitlesLinkedList.addLast(title);
        mHistoryTitlesAdapter.notifyItemInserted(mHistoryLinkedList.size() - 1);
        mTableOfContentRecyclerViewAdapter.displayChildrenOf(mHistoryLinkedList.peekLast());
        mHistoryRecyclerView.scrollToPosition(mHistoryLinkedList.size() - 1);
        getView().requestFocus();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTableOfContentTitleClickListener) {
            mListener = (OnTableOfContentTitleClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTableOfContentTitleClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void restoreNonSearchMode() {
        mHistoryLinkedList.clear();
        mHistoryLinkedList.addAll(mBeforeEnteringSearchHistory);//restore the history linked list
        mHistoryTitlesAdapter.notifyDataSetChanged();
        mBeforeEnteringSearchHistory.clear();//reset the before search list

        mBacstackTitlesLinkedList.clear();
        mBacstackTitlesLinkedList.addAll(mBeforeBacstackTitlesLinkedList);//restore the back stack linked list
        mBeforeBacstackTitlesLinkedList.clear();//reset the before search list


        mTableOfContentRecyclerViewAdapter.displayChildrenOf(mHistoryLinkedList.peekLast());
        mInSearchMode = false;
        getView().requestFocus();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_table_of_content, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getString(R.string.search_toc_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query == null || query.isEmpty()) {
                    if (mInSearchMode) {
                        //we already started the search mode so now we will restore the normal view
                        restoreNonSearchMode();
                    }

                } else {
                    if (!mInSearchMode) {
                        //we are entering search mode for the first time
                        mBeforeEnteringSearchHistory.addAll(mHistoryLinkedList);//save the history linked list
                        mHistoryLinkedList.clear();//clear the history linked list after saving its state
                        mBeforeBacstackTitlesLinkedList.addAll(mBacstackTitlesLinkedList);//save the back stack linked list
                        mBacstackTitlesLinkedList.clear();//clear the back stack linked list after saving its state
                        mInSearchMode = true;
                        Title searchResultRootTitle = Title.createRootTitle(getString(R.string.search_result_root_title));
                        mHistoryLinkedList.addLast(searchResultRootTitle);
                        mHistoryTitlesAdapter.notifyDataSetChanged();
                    }
                    mTableOfContentRecyclerViewAdapter.getFilter().filter(query);

                }
                return false;
            }
        });


        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (mInSearchMode) {
                    //we already started the search mode so now we will restore the normal view
                    restoreNonSearchMode();
                }
                return true;
            }
        });


    }


    public interface OnTableOfContentTitleClickListener {
        void OnOnTableOfContentTitleClicked(Title title);

        BookPartsInfo getBookPartsInfo();
    }


}
