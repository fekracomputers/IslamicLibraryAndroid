package com.fekracomputers.islamiclibrary.homeScreen.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.dictiography.collections.IndexedTreeSet;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.homeScreen.controller.BookCollectionsController;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.widget.HorizontalBookRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohammad on 22/11/2017.
 */

public class HomeScreenRecyclerViewAdapter extends RecyclerView.Adapter<HomeScreenRecyclerViewAdapter.CollectionViewHolder> {

    private static final int NAME_CHANGE_UPDATE = 1;
    private static final int FORCE_REFRESH_UPDATE = 2;
    private static final int CLOSE_CURSOR_UPDATE = 3;


    private IndexedTreeSet<BookCollectionRecyclable> booksCollections = new IndexedTreeSet<>();
    private BookCardEventsCallback bookCardEventsCallback;
    private Context context;
    private BookCollectionsController collectionController;


    public HomeScreenRecyclerViewAdapter(ArrayList<BooksCollection> booksCollections,
                                         BookCardEventsCallback bookCardEventsCallback,
                                         Context context, BookCollectionsController collectionController) {
        this.collectionController = collectionController;
        setHasStableIds(true);
        for (BooksCollection booksCollection : booksCollections) {
            this.booksCollections.add(new BookCollectionRecyclable(booksCollection));
        }

        this.bookCardEventsCallback = bookCardEventsCallback;
        this.context = context;
    }


    @Override
    public CollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HorizontalBookRecyclerView recyclerView = new HorizontalBookRecyclerView(parent.getContext());
        return new CollectionViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(CollectionViewHolder holder, int position) {
        BooksCollection booksCollection = booksCollections.exact(position).getBooksCollection();
        holder.setupRecyclerView(booksCollection, bookCardEventsCallback, false);
    }

    @Override
    public void onBindViewHolder(CollectionViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            BookCollectionRecyclable exact = booksCollections.exact(position);
            if (!exact.isDirty())
                onBindViewHolder(holder, position);
            else
                handlePayloadUpdate(holder, position, exact.getUpdatePayload());

        } else {
            for (Object payload : payloads) {
                if (payload instanceof UpdatePayload) {
                    UpdatePayload payload1 = (UpdatePayload) payload;
                    handlePayloadUpdate(holder, position, payload1);
                }


            }
        }
    }

    private void handlePayloadUpdate(CollectionViewHolder holder, int position, @NonNull UpdatePayload payload1) {
        if (payload1.requestCode == NAME_CHANGE_UPDATE) {
            holder.horizontalBookRecyclerView.rename(payload1.getString());
        } else if (payload1.requestCode == FORCE_REFRESH_UPDATE) {
            BooksCollection booksCollection = booksCollections.exact(position).getBooksCollection();
            holder.setupRecyclerView(booksCollection, bookCardEventsCallback, true);

        } else if (payload1.requestCode == CLOSE_CURSOR_UPDATE) {
            holder.horizontalBookRecyclerView.closeCursor();
        }
        booksCollections.exact(position).setUpdatePayload(null);
    }

    @Override
    public long getItemId(int position) {
        return booksCollections.exact(position).getBooksCollection().getCollectionsId();
    }

    @Override
    public int getItemCount() {
        return booksCollections.size();
    }

    public void notifyAllRecyclersDatasetChanged() {
        UpdatePayload payload = new UpdatePayload(FORCE_REFRESH_UPDATE);
        for (BookCollectionRecyclable booksCollection : booksCollections) {
            booksCollection.setUpdatePayload(payload);
        }
        notifyItemRangeChanged(0, booksCollections.size(), payload);
    }

    public void notifyAllToReAquireCursors() {
        UpdatePayload payload = new UpdatePayload(FORCE_REFRESH_UPDATE);
        for (BookCollectionRecyclable booksCollection : booksCollections) {
            booksCollection.setUpdatePayload(payload);
        }
        notifyItemRangeChanged(0, booksCollections.size(), payload);
    }

    public void notifyAllRecyclersCloseCursors() {
        UpdatePayload payload = new UpdatePayload(CLOSE_CURSOR_UPDATE);
        for (BookCollectionRecyclable booksCollection : booksCollections) {
            booksCollection.setUpdatePayload(payload);
        }
        notifyItemRangeChanged(0, booksCollections.size(), payload);
    }

    public void notifyBookCollectionChanged(BooksCollection booksCollection) {
        UpdatePayload payload = new UpdatePayload(FORCE_REFRESH_UPDATE);
        int order = booksCollection.getOrder();

        booksCollections.exact(order).setUpdatePayload(payload);
        notifyItemChanged(order, payload);
    }

    public void notifyBookCollectionRemoved(BooksCollection booksCollection) {
        booksCollections.remove(new BookCollectionRecyclable(booksCollection));
        notifyItemRemoved(booksCollection.getOrder());
    }

    public void notifyBookCollectionAdded(BooksCollection booksCollection) {
        UpdatePayload payload = new UpdatePayload(FORCE_REFRESH_UPDATE);
        if (booksCollections.add(new BookCollectionRecyclable(booksCollection, payload)))
            notifyItemInserted(booksCollection.getOrder());
    }

    public void notifyBookCollectionRenamed(BooksCollection booksCollection, String newName) {

        notifyItemChanged(booksCollection.getOrder(),
                new UpdatePayload(NAME_CHANGE_UPDATE, newName));
        collectionController.renameCollection(booksCollection, newName);
    }

    public void notifyBookCollectionMoved(int collectionsId, int oldPosition, int newPosition) {
        final BookCollectionRecyclable collectionOne = booksCollections.exact(oldPosition);
        final BookCollectionRecyclable collectionTwo = booksCollections.exact(newPosition);
        if (collectionOne.getBooksCollection().getOrder() != newPosition &&
                collectionTwo.getBooksCollection().getOrder() != oldPosition) {
            booksCollections.remove(collectionOne);
            booksCollections.remove(collectionTwo);

            collectionOne.getBooksCollection().setOrder(newPosition);
            collectionTwo.getBooksCollection().setOrder(oldPosition);

            booksCollections.add(collectionOne);
            booksCollections.add(collectionTwo);

            notifyItemMoved(oldPosition, newPosition);
            notifyItemMoved(newPosition, oldPosition);
        }
    }

    public void onBookCollectionVisibilityChanged(BooksCollection booksCollection, boolean isVisible) {
        if (isVisible) notifyBookCollectionAdded(booksCollection);
        else notifyBookCollectionRemoved(booksCollection);

    }


    class UpdatePayload {
        int requestCode;
        String dataString;

        public UpdatePayload(int requestCode, String dataString) {
            this.requestCode = requestCode;
            this.dataString = dataString;
        }

        public UpdatePayload(int requestCode) {
            this.requestCode = requestCode;

        }

        public String getString() {
            return dataString;
        }
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
            horizontalBookRecyclerView.setupRecyclerView(booksCollection,
                    bookCardEventsCallback
                    , collectionController, forceRefresh);
        }

        public void refreshFromDatabase(int position) {
            horizontalBookRecyclerView
                    .changeCursor(booksCollections.exact(position).getBooksCollection()
                            .reAcquireCursor(context, true));
        }
    }


}



