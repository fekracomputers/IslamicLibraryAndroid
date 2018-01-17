package com.fekracomputers.islamiclibrary.userNotes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.fekracomputers.islamiclibrary.browsing.util.BrowsingUtils;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.UserNote;
import com.fekracomputers.islamiclibrary.userNotes.adapters.HeaderItem;
import com.fekracomputers.islamiclibrary.userNotes.adapters.UserNoteGroupAdapter;
import com.fekracomputers.islamiclibrary.userNotes.adapters.UserNoteItem;
import com.xwray.groupie.Section;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link UserNoteGroupAdapter.UserNoteInterActionListener}
 * interface.
 */
public class GlobalUserNotesFragment extends Fragment implements SortListDialogFragment.OnSortDialogListener {

    private UserNoteGroupAdapter.UserNoteInterActionListener mListener;
    private UserDataDBHelper.GlobalUserDBHelper userDatabase;
    private ArrayList<UserNoteItem> bookmarkItems;
    private ArrayList<UserNoteItem> highlightItems;
    private UserNoteGroupAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GlobalUserNotesFragment() {
    }

    public static GlobalUserNotesFragment newInstance() {
        return new GlobalUserNotesFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_global_highlight, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void sortMethodSelected(int which) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userDatabase = UserDataDBHelper.getInstance(getContext());


        bookmarkItems = userDatabase.getBookmarkItems();
        highlightItems = userDatabase.getHighlightItems();
        adapter = new UserNoteGroupAdapter();
        adapter.setUserNoteInterActionListener(mListener);

        Section bookmarksExpandableGroup = new Section(new HeaderItem(R.string.bookmarks));
        bookmarksExpandableGroup.addAll(bookmarkItems);
        adapter.add(bookmarksExpandableGroup);

        Section hihligtsexpandableGroup = new Section(new HeaderItem(R.string.notes));
        hihligtsexpandableGroup.addAll(highlightItems);
        adapter.add(hihligtsexpandableGroup);

        mListener = new UserNoteGroupAdapter.UserNoteInterActionListener() {
            @Override
            public void onUserNoteClicked(UserNote userNote) {
                int pageId = userNote.getPageInfo() != null ? userNote.getPageInfo().pageId : 1;
                BrowsingUtils.openBookForReading(userNote.bookId,
                        pageId,
                        getContext());
            }

            @Override
            public void onUserNoteRemoved(UserNote userNote) {
                int pageId = userNote.getPageInfo() != null ? userNote.getPageInfo().pageId : 1;
                userDatabase.deleteBookmark(pageId, userNote.bookId);
            }
        };
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_global_user_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ViewStub zeroView = view.findViewById(R.id.zero_global_user_notes);

        if ((bookmarkItems.isEmpty() && highlightItems.isEmpty())) {
            recyclerView.setVisibility(View.GONE);
            zeroView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


}
