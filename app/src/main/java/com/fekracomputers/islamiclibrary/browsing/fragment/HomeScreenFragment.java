package com.fekracomputers.islamiclibrary.browsing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.widget.HorizontalBookRecyclerView;

import java.util.ArrayList;

public class HomeScreenFragment extends Fragment implements BrowsingActivityListingFragment {

    private BookCardEventsCallback mListener;
    private BooksInformationDbHelper booksInformationDbHelper;
    private UserDataDBHelper.GlobalUserDBHelper globalUserDBHelper;

    public HomeScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_home_screen, container, false);
        booksInformationDbHelper = BooksInformationDbHelper.getInstance(getContext());
        globalUserDBHelper = UserDataDBHelper.getInstance(getContext());
        ArrayList<BooksCollection> booksCollections = globalUserDBHelper.getBooksCollections(true);
        for (BooksCollection booksCollection : booksCollections) {
            rootView.addView(
                    new HorizontalBookRecyclerView(getContext()).setupRecyclerView(booksCollection,
                            mListener
                    ));
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookCardEventListener) {
            mListener = ((BookCardEventListener) context).getBookCardEventCallback();
            ((BookCardEventListener) context).registerListener(this);

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BookCardEventsCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (getActivity() instanceof BookCardEventListener)
            ((BookCardEventListener) getActivity()).unRegisterListener(this);
    }

    @Override
    public void actionModeDestroyed() {

    }

    @Override
    public void actionModeStarted() {

    }

    @Override
    public void bookSelectionStatusUpdate() {

    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void BookDownloadStatusUpdate(int bookId, int downloadStatus) {

    }

    @Override
    public void switchTodownloadedOnly(boolean checked) {

    }

    @Override
    public void reAcquireCursors() {

    }

    @Override
    public void closeCursors() {

    }

    @Override
    public void selecteItem(int id) {

    }
}
