package com.fekracomputers.islamiclibrary.browsing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.widget.HorizontalBookRecyclerView;

import java.util.ArrayList;

public class HomeScreenFragment extends Fragment implements BrowsingActivityListingFragment {

    private final ArrayList<Pair<HorizontalBookRecyclerView, BooksCollection>> horizontalBookRecyclerViews = new ArrayList<>();
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
        View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);
        LinearLayout containerLinearLayout = rootView.findViewById(R.id.home_screen_horizontal_list_container);

        booksInformationDbHelper = BooksInformationDbHelper.getInstance(getContext());
        globalUserDBHelper = UserDataDBHelper.getInstance(getContext());
        ArrayList<BooksCollection> booksCollections = globalUserDBHelper.getBooksCollections(true, false);
        for (BooksCollection booksCollection : booksCollections) {
            HorizontalBookRecyclerView recyclerView = new HorizontalBookRecyclerView(getContext())
                    .setupRecyclerView(booksCollection, mListener);
            horizontalBookRecyclerViews.add(new Pair<>(recyclerView, booksCollection));
            containerLinearLayout.addView(recyclerView);
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

    private void notifyAllRecyclersDatasetChanged() {
        for (Pair<HorizontalBookRecyclerView, BooksCollection> horizontalBookRecyclerView : horizontalBookRecyclerViews) {
            horizontalBookRecyclerView.first.notifyDatasetChanged();
        }
    }

    @Override
    public void actionModeDestroyed() {
        notifyAllRecyclersDatasetChanged();
    }

    @Override
    public void actionModeStarted() {
        notifyAllRecyclersDatasetChanged();

    }

    @Override
    public void bookSelectionStatusUpdate() {
        notifyAllRecyclersDatasetChanged();
    }

    @Override
    public int getType() {
        return BrowsingActivity.HOME_SCREEN_TYPE;
    }

    @Override
    public void BookDownloadStatusUpdate(int bookId, int downloadStatus) {
        reAcquireCursors();
    }

    @Override
    public void switchTodownloadedOnly(boolean checked) {

    }

    @Override
    public void reAcquireCursors() {
        for (Pair<HorizontalBookRecyclerView, BooksCollection> horizontalBookRecyclerView : horizontalBookRecyclerViews) {
            horizontalBookRecyclerView.first.changeCursor(horizontalBookRecyclerView.second.reAcquireCursor(getContext()));
        }
    }

    @Override
    public void closeCursors() {
        for (Pair<HorizontalBookRecyclerView, BooksCollection> horizontalBookRecyclerView : horizontalBookRecyclerViews) {
            horizontalBookRecyclerView.first.closeCursor();
        }
    }

    @Override
    public void selecteItem(int id) {

    }
}
