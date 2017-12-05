package com.fekracomputers.islamiclibrary.userNotes;

import android.os.Bundle;
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
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.UserNote;
import com.fekracomputers.islamiclibrary.userNotes.adapters.ExpandableHeaderItem;
import com.fekracomputers.islamiclibrary.userNotes.adapters.UpdatableExpandingGroup;
import com.fekracomputers.islamiclibrary.userNotes.adapters.UserNoteGroupAdapter;
import com.fekracomputers.islamiclibrary.userNotes.adapters.UserNoteItem;

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
        mListener = new UserNoteGroupAdapter.UserNoteInterActionListener() {
            @Override
            public void onUserNoteClicked(UserNote userNote) {

            }

            @Override
            public void onUserNoteRemoved(UserNote userNote) {

            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_global_user_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ViewStub zeroView = view.findViewById(R.id.zero_global_user_notes);

        if ((bookmarkItems.isEmpty() && highlightItems.isEmpty())) {
            recyclerView.setVisibility(View.GONE);
            zeroView.setVisibility(View.VISIBLE);
        } else {
            UserNoteGroupAdapter adapter = new UserNoteGroupAdapter();
            adapter.setUserNoteInterActionListener(mListener);

            UpdatableExpandingGroup bookmarksExpandableGroup = new UpdatableExpandingGroup(new ExpandableHeaderItem(R.string.bookmarks));
            bookmarksExpandableGroup.update(bookmarkItems);
            adapter.add(bookmarksExpandableGroup);

            UpdatableExpandingGroup hihligtsexpandableGroup = new UpdatableExpandingGroup(new ExpandableHeaderItem(R.string.notes));
            hihligtsexpandableGroup.update(highlightItems);
            adapter.add(hihligtsexpandableGroup);

            recyclerView.setAdapter(adapter);
        }


        return view;
    }


}
