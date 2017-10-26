package com.fekracomputers.islamiclibrary.tableOFContents.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.dialog.SortListDialogFragment;
import com.fekracomputers.islamiclibrary.databases.UserDataDBContract;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.Bookmark;
import com.fekracomputers.islamiclibrary.tableOFContents.adapter.BookmarkRecyclerViewAdapter;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <br>
 * Activities containing this fragment MUST implement the {@link onBookmarkClickListener}
 * interface.
 */
public class BookmarkFragment extends Fragment implements SortListDialogFragment.OnSortDialogListener {

    private int bookId;
    private onBookmarkClickListener mListener;
    private BookmarkRecyclerViewAdapter bookmarkRecyclerViewAdapter;
    private boolean sortedByPage = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookmarkFragment() {
    }

    public static BookmarkFragment newInstance(int bookId) {
        BookmarkFragment fragment = new BookmarkFragment();
        Bundle args = new Bundle();
        args.putInt("bookId", bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_bookmark, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                SortListDialogFragment sortListDialogFragment = SortListDialogFragment.newInstance(R.array.bookmark_list_sorting, bookmarkRecyclerViewAdapter.getmCurrentSortIndex());
                //see this answer http://stackoverflow.com/a/37794319/3061221
                FragmentManager fm = getChildFragmentManager();
                sortListDialogFragment.show(fm, "fragment_sort");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void sortMethodSelected(int which) {
        bookmarkRecyclerViewAdapter.sortBy(which);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundl = getArguments();
        if (bundl != null) {
            bookId = bundl.getInt("bookId");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark_list, container, false);

        // Set the adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ViewStub zeroView = view.findViewById(R.id.zero_bookmarks);

        UserDataDBHelper userDataDBHelper = UserDataDBHelper.getInstance(getContext(), bookId);
        ArrayList<Bookmark> bookmarks = userDataDBHelper.getAllBookmarks(UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID);

        if (bookmarks.size() != 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            bookmarkRecyclerViewAdapter = new BookmarkRecyclerViewAdapter(bookmarks, mListener, getContext(), userDataDBHelper,  getActivity().getPreferences(Context.MODE_PRIVATE));
            bookmarkRecyclerViewAdapter.setHasStableIds(true);
            recyclerView.setAdapter(bookmarkRecyclerViewAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            zeroView.setVisibility(View.VISIBLE);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onBookmarkClickListener) {
            mListener = (onBookmarkClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onBookmarkClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface onBookmarkClickListener {
        // TODO: Update argument type and name
        void onBookmarkClicked(Bookmark bookmark);

        BookPartsInfo getBookPartsInfo();

    }
}
