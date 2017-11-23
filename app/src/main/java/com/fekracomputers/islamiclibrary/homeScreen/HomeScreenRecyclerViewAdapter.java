package com.fekracomputers.islamiclibrary.homeScreen;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.widget.HorizontalBookRecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Mohammad on 22/11/2017.
 */

public class HomeScreenRecyclerViewAdapter extends RecyclerView.Adapter<HomeScreenRecyclerViewAdapter.CollectionViewHolder> {

    private TreeMap<Integer, BooksCollection> booksCollections = new TreeMap<>();
    private BookCardEventsCallback bookCardEventsCallback;
    private Context context;
    private boolean closeCursors = false;


    public HomeScreenRecyclerViewAdapter(ArrayList<BooksCollection> booksCollections,
                                         BookCardEventsCallback bookCardEventsCallback,
                                         Context context) {
        setHasStableIds(true);
        for (BooksCollection booksCollection : booksCollections) {
            this.booksCollections.put(booksCollection.getCollectionsId(), booksCollection);
        }

        this.bookCardEventsCallback = bookCardEventsCallback;
        this.context = context;
    }

    private static int getBookCollectionPositionByID(TreeMap<Integer, BooksCollection> booksCollections,
                                                     int collectionId) {
        return booksCollections.containsKey(collectionId) ?
                booksCollections.headMap(collectionId).size() : -1;
    }

    @Nullable
    static BooksCollection getBookCollectionByPosition(TreeMap<Integer, BooksCollection> booksCollections, int position) {
        return booksCollections.get(getBookCollectionIDByPosition(booksCollections, position));
    }

    static int getBookCollectionIDByPosition(TreeMap<Integer, BooksCollection> booksCollections, int position) {
        Iterator<Integer> iterator = booksCollections.keySet().iterator();
        Integer booksCollection = null;
        for (int i = -1; i < position && iterator.hasNext(); ) {
            i++;
            booksCollection = iterator.next();
        }
        return booksCollection;
    }

    @Override
    public CollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HorizontalBookRecyclerView recyclerView = new HorizontalBookRecyclerView(parent.getContext());
        return new CollectionViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(CollectionViewHolder holder, int position) {
        BooksCollection booksCollection = getBookCollectionByPosition(booksCollections, position);
        if (!closeCursors) {
            holder.setupRecyclerView(booksCollection, bookCardEventsCallback, false);
        } else {
            holder.horizontalBookRecyclerView.closeCursor();
        }

    }

    @Override
    public void onBindViewHolder(CollectionViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            onBindViewHolder(holder,position);
        } else {
            BooksCollection booksCollection = getBookCollectionByPosition(booksCollections, position);
            if (!closeCursors) {
                holder.setupRecyclerView(booksCollection, bookCardEventsCallback, true);
            } else {
                holder.horizontalBookRecyclerView.closeCursor();
            }
        }

    }

    @Override
    public long getItemId(int position) {
        return getBookCollectionIDByPosition(booksCollections, position);
    }

    @Override
    public int getItemCount() {
        return booksCollections.size();
    }

    public void notifyAllRecyclersDatasetChanged() {
        closeCursors = false;
        notifyItemRangeChanged(0,booksCollections.size(),new Object());
    }

    public void notifyAllToReAquireCursors() {
        closeCursors = false;
        notifyItemRangeChanged(0,booksCollections.size(),new Object());
    }

    public void notifyAllRecyclersCloseCursors() {
        closeCursors = true;
        notifyItemRangeChanged(0,booksCollections.size(),new Object());
    }

    public void notifyBookCollectionChanged(int collectionId) {
        closeCursors = false;
        notifyItemChanged(getBookCollectionPositionByID(booksCollections, collectionId),new Object());
    }

    void notifyBookCollectionRemoved(int collectionId) {
        closeCursors = false;
        notifyItemRemoved(getBookCollectionPositionByID(booksCollections, collectionId));
    }

    void notifyBookCollectionAdded(int collectionId) {
        closeCursors = false;
        UserDataDBHelper.GlobalUserDBHelper globalUserDBHelper = UserDataDBHelper.getInstance(context);
        BooksCollection booksCollection = globalUserDBHelper.getBooksCollection(collectionId);
        booksCollections.put(booksCollection.getCollectionsId(), booksCollection);
        notifyItemInserted(getBookCollectionPositionByID(booksCollections,collectionId));
        //notifyDataSetChanged();
    }

    class CollectionViewHolder extends RecyclerView.ViewHolder {
        private HorizontalBookRecyclerView horizontalBookRecyclerView;

        CollectionViewHolder(HorizontalBookRecyclerView horizontalBookRecyclerView) {
            super(horizontalBookRecyclerView);
            this.horizontalBookRecyclerView = horizontalBookRecyclerView;
        }

        void setupRecyclerView(BooksCollection booksCollection,
                               BookCardEventsCallback bookCardEventsCallback,
                               boolean forceRefresh) {
            horizontalBookRecyclerView.setupRecyclerView(booksCollection, bookCardEventsCallback, forceRefresh);
        }

        public void refreshFromDatabase(int position) {
            horizontalBookRecyclerView
                    .changeCursor(getBookCollectionByPosition(booksCollections, position)
                            .reAcquireCursor(context, true));
        }
    }


}



