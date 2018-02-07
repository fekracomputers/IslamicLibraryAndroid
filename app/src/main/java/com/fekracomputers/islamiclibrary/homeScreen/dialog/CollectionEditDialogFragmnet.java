package com.fekracomputers.islamiclibrary.homeScreen.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.homeScreen.adapters.CollectionRecyclerViewAdapter;
import com.fekracomputers.islamiclibrary.homeScreen.callbacks.BookCollectionsCallBack;
import com.fekracomputers.islamiclibrary.homeScreen.controller.BookCollectionsController;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.utility.Util;

import java.util.ArrayList;


/**
 * Created by Mohammad on 1/11/2017.
 */

public class CollectionEditDialogFragmnet extends DialogFragment implements BookCollectionsCallBack {
    @Nullable
    private BookCollectionsController bookCollectionsController;
    @Nullable
    private BookCollectionsController.BookCollectionsControllerCallback collectionsControllerCallback;
    @Nullable
    private CollectionRecyclerViewAdapter collectionRecyclerViewAdapter;

    @NonNull
    public static CollectionEditDialogFragmnet newInstance() {
        CollectionEditDialogFragmnet frag = new CollectionEditDialogFragmnet();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookCollectionsController = new BookCollectionsController(getContext(), collectionsControllerCallback);
        ArrayList<BooksCollection> booksCollections = bookCollectionsController
                .getAllBookCollections(getContext(), false, false);

        collectionRecyclerViewAdapter = new CollectionRecyclerViewAdapter(booksCollections,
                bookCollectionsController);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.collection_edit_fragment_dialog, container, false);


        setHasOptionsMenu(false);


        RecyclerView collectionRecyclerView = rootView.findViewById(R.id.collection_recycler_view);
        collectionRecyclerViewAdapter.setHasStableIds(true);
        collectionRecyclerView.setHasFixedSize(true);
        collectionRecyclerView.setAdapter(collectionRecyclerViewAdapter);
        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false));

        EditText newCollectionName = rootView.findViewById(R.id.new_collection_edit_text);
        ImageButton addCollectionButton = rootView.findViewById(R.id.add_collection);
        addCollectionButton.setOnClickListener(v ->
        {
            BooksCollection newCollection = bookCollectionsController.createNewCollection(newCollectionName.getText().toString());
            collectionRecyclerViewAdapter.onBookCollectionAdded(newCollection);
            newCollectionName.setText("");
        });

        boolean enabled = !newCollectionName.getText().toString().isEmpty();
        setEnabledAddButton(addCollectionButton, enabled);
        newCollectionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                if (s.length() != 0) {
                    setEnabledAddButton(addCollectionButton, true);
                } else {
                    setEnabledAddButton(addCollectionButton, false);
                }
            }
        });

        return rootView;
    }


    private void setEnabledAddButton(@NonNull ImageButton addCollectionButton, boolean enabled) {
        Util.setImageButtonEnabled(getContext(),
                enabled,
                addCollectionButton,
                R.drawable.ic_add_black_24dp);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookCollectionsController.BookCollectionsControllerCallback) {
            collectionsControllerCallback =
                    ((BookCollectionsController.BookCollectionsControllerCallback) context);
            collectionsControllerCallback.registerBookCollectionCallBack(this);


        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BookCollectionsController.BookCollectionsControllerCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof BookCollectionsController.BookCollectionsControllerCallback) {
            collectionsControllerCallback.unRegisterBookCollectionCallBack(this);

            collectionsControllerCallback = null;

        }
    }

    @Override
    public void onBookCollectionCahnged(BooksCollection booksCollection) {
        //do nothing
    }

    @Override
    public void onBookCollectionAdded(BooksCollection booksCollection) {
        collectionRecyclerViewAdapter.onBookCollectionAdded(booksCollection);
    }

    @Override
    public void onBookCollectionRemoved(BooksCollection booksCollection) {
        collectionRecyclerViewAdapter.onBookCollectionRemoved(booksCollection);
    }

    @Override
    public void onBookCollectionRenamed(BooksCollection booksCollection, String newName) {
        collectionRecyclerViewAdapter.onBookCollectionRenamed(booksCollection, newName);
    }

    @Override
    public void onBookCollectionMoved(int collectionsId, int oldPosition, int newPosition) {
        collectionRecyclerViewAdapter.onBookCollectionMoved(collectionsId, oldPosition, newPosition);
    }

    @Override
    public void onBookCollectionVisibilityChanged(BooksCollection booksCollection, boolean isVisible) {
        collectionRecyclerViewAdapter.onBookCollectionVisibilityChanged(booksCollection, isVisible);

    }


}
