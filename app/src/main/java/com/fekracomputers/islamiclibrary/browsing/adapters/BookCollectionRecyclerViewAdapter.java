package com.fekracomputers.islamiclibrary.browsing.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.BookCollectionInfo;
import com.fekracomputers.islamiclibrary.model.BooksCollection;

import java.util.ArrayList;

/**
 * Created by Mohammad on 23/11/2017.
 */
public class BookCollectionRecyclerViewAdapter extends RecyclerView.Adapter<BookCollectionRecyclerViewAdapter.ViewHolder> {
    private ArrayList<BooksCollection> bookCollections;
    private BookCollectionInfo bookCollectionInfo;

    public BookCollectionRecyclerViewAdapter(
                                         ArrayList<BooksCollection> bookCollections,
                                         BookCollectionInfo bookCollectionInfo) {
        this.bookCollections = bookCollections;
        this.bookCollectionInfo = bookCollectionInfo;
    }

    public void setCollections(ArrayList<BooksCollection> bookCollections) {
        this.bookCollections = bookCollections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_with_check_box, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BooksCollection booksCollection = bookCollections.get(position);
        holder.booksCollection = booksCollection;
        holder.collectionNameTextView.setText(booksCollection.getName());
        holder.checkBox.setChecked(bookCollectionInfo.doBelongTo(booksCollection));
    }

    @Override
    public int getItemCount() {
        return bookCollections.size();
    }

    public long getItemId(int position) {
        return bookCollections.get(position).getCollectionsId();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView collectionNameTextView;
        final CheckBox checkBox;
        BooksCollection booksCollection;

        ViewHolder(@NonNull View view) {
            super(view);
            collectionNameTextView = view.findViewById(R.id.title);
            checkBox = view.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(v ->
                    bookCollectionInfo
                            .setBelongToCollection(
                                    booksCollection,
                                    ((CheckBox) v).isChecked()));
        }
    }

}
