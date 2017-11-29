package com.fekracomputers.islamiclibrary.homeScreen.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dictiography.collections.IndexedTreeSet;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.homeScreen.controller.BookCollectionsController;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.utility.Util;

import java.util.ArrayList;


public class CollectionRecyclerViewAdapter
        extends RecyclerView.Adapter<CollectionRecyclerViewAdapter.ViewHolder>

{
    private IndexedTreeSet<BooksCollection> bookCollections = new IndexedTreeSet<>();
    private BookCollectionsController bookCollectionsController;

    public CollectionRecyclerViewAdapter(ArrayList<BooksCollection> bookCollections, BookCollectionsController bookCollectionsController) {
        this.bookCollections.addAll(bookCollections);
        this.bookCollectionsController = bookCollectionsController;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_full_control, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BooksCollection booksCollection = bookCollections.exact(position);
        holder.booksCollection = booksCollection;
        holder.setEnabledButtons();
        holder.collectionNameTextView.setText(booksCollection.getName());
        holder.hideSwitch.setChecked(holder.booksCollection.isVisibile());
    }

    @Override
    public int getItemCount() {
        return bookCollections.size();
    }

    public long getItemId(int position) {
        return bookCollections.exact(position).getCollectionsId();
    }


    public void onBookCollectionAdded(BooksCollection booksCollection) {
        if (bookCollections.add(booksCollection))
            notifyItemInserted(booksCollection.getOrder());
    }

    public void onBookCollectionRemoved(BooksCollection booksCollection) {
        if (bookCollections.remove(booksCollection)) {
            notifyItemRemoved(booksCollection.getOrder());
        }
    }

    public void onBookCollectionMoved(int collectionsId, int oldPosition, int newPosition) {
        final BooksCollection collectionOne = bookCollections.exact(oldPosition);
        final BooksCollection collectionTwo = bookCollections.exact(newPosition);
        if (collectionOne.getOrder() != newPosition && collectionTwo.getOrder() != oldPosition) {
            bookCollections.remove(collectionOne);
            bookCollections.remove(collectionTwo);

            collectionOne.setOrder(newPosition);
            collectionTwo.setOrder(oldPosition);

            bookCollections.add(collectionOne);
            bookCollections.add(collectionTwo);

            notifyItemMoved(oldPosition, newPosition);
            notifyItemMoved(newPosition, oldPosition);
        }
    }

    public void onBookCollectionRenamed(BooksCollection booksCollection, String newName) {
        BooksCollection exact = bookCollections.exact(booksCollection.getOrder());
        if (!exact.getName().equals(newName)) {
            exact.setName(newName);
            notifyItemChanged(booksCollection.getOrder());
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView collectionNameTextView;
        private final Switch hideSwitch;
        BooksCollection booksCollection;
        LinearLayout buttonBar;
        private View view;

        ViewHolder(View view) {
            super(view);
            collectionNameTextView = view.findViewById(R.id.title);
            buttonBar = view.findViewById(R.id.button_bar);

            hideSwitch = view.findViewById(R.id.menu_item_hide);
            ImageButton clearCollection = view.findViewById(R.id.menu_item_clear);
            ImageButton deleteCollection = view.findViewById(R.id.menu_delete_collection);
            ImageButton renameCollection = view.findViewById(R.id.menu_item_rename);
            ImageButton moveUp = view.findViewById(R.id.menu_move_up);
            ImageButton moveDown = view.findViewById(R.id.menu_move_down);
            clearCollection.setOnClickListener(this::onClick);
            deleteCollection.setOnClickListener(this::onClick);
            renameCollection.setOnClickListener(this::onClick);
            moveUp.setOnClickListener(this::onClick);
            moveDown.setOnClickListener(this::onClick);
            this.view = view;
        }

        public void onClick(View v) {
            bookCollectionsController.collectionActionHandler(v.getId(), booksCollection, view.getContext(), null);
        }

        void setEnabledButtons() {
            if (booksCollection != null) {

                hideSwitch.setOnCheckedChangeListener(null);
                hideSwitch.setChecked(booksCollection.isVisibile());
                hideSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                        bookCollectionsController.updateCollectionVisibility(booksCollection, isChecked)
                );

                int childCount = buttonBar.getChildCount();
                for (int i = 1; i < childCount; i++) {
                    ImageButton imageButton = (ImageButton) buttonBar.getChildAt(i);
                    Util.setImageButtonEnabled(
                            view.getContext(),
                            booksCollection.isActionSupported(imageButton.getId()),
                            imageButton,
                            BooksCollection.getActionResId(imageButton.getId()))
                    ;
                }
            }

        }
    }
}
