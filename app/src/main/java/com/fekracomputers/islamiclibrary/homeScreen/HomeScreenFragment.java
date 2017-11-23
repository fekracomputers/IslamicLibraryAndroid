package com.fekracomputers.islamiclibrary.homeScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;
import com.fekracomputers.islamiclibrary.browsing.controller.BookCollectionsController;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BooksCollection;

import java.util.ArrayList;

public class HomeScreenFragment extends Fragment
        implements BrowsingActivityListingFragment,
        HomeScreenCallBack {

    private BookCardEventsCallback mListener;
    private ArrayList<BooksCollection> booksCollections;
    private HomeScreenRecyclerViewAdapter homeScreenRecyclerViewAdapter;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserDataDBHelper.GlobalUserDBHelper globalUserDBHelper = UserDataDBHelper.getInstance(getContext());
        booksCollections = globalUserDBHelper.getBooksCollections(true, false);
        homeScreenRecyclerViewAdapter = new HomeScreenRecyclerViewAdapter(booksCollections, mListener, getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);
        RecyclerView containerLinearLayout = rootView.findViewById(R.id.home_screen_horizontal_list_container);
        containerLinearLayout.setHasFixedSize(true);
        containerLinearLayout.setAdapter(homeScreenRecyclerViewAdapter);
        containerLinearLayout.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookCardEventListener) {
            mListener = ((BookCardEventListener) context).getBookCardEventCallback();
            ((BookCardEventListener) context).registerListener(this);
            ((BookCollectionsController.BookCollectionsControllerCallback) context).registerHomeScreen(this);

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
        ((BookCollectionsController.BookCollectionsControllerCallback) getActivity()).unRegisterHomeScreen(this);

    }


    @Override
    public void actionModeDestroyed() {
        homeScreenRecyclerViewAdapter.notifyAllRecyclersDatasetChanged();
    }

    @Override
    public void actionModeStarted() {
        homeScreenRecyclerViewAdapter.notifyAllRecyclersDatasetChanged();

    }

    @Override
    public void bookSelectionStatusUpdate() {
        homeScreenRecyclerViewAdapter.notifyAllRecyclersDatasetChanged();
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
        homeScreenRecyclerViewAdapter.notifyAllToReAquireCursors();
    }

    @Override
    public void closeCursors() {
        homeScreenRecyclerViewAdapter.notifyAllRecyclersCloseCursors();
    }

    @Override
    public void selectAllItems(int id) {

    }

    @Override
    public void notifyBookCollectionCahnged(int collectionId) {
        homeScreenRecyclerViewAdapter.notifyBookCollectionChanged(collectionId);

    }

    @Override
    public void notifyBookCollectionAdded(int collectionId) {
        homeScreenRecyclerViewAdapter.notifyBookCollectionAdded(collectionId);
    }

    @Override
    public void notifyBookCollectionRemoved(int collectionId) {
        homeScreenRecyclerViewAdapter.notifyBookCollectionRemoved(collectionId);

    }
}
