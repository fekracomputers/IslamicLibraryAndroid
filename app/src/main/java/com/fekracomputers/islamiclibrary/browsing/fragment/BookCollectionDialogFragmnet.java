package com.fekracomputers.islamiclibrary.browsing.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.adapters.BookCollectionRecyclerViewAdapter;
import com.fekracomputers.islamiclibrary.homeScreen.controller.BookCollectionsController;
import com.fekracomputers.islamiclibrary.model.BookCollectionInfo;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.utility.Util;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * Created by Mohammad on 1/11/2017.
 */

public class BookCollectionDialogFragmnet extends DialogFragment {
    public static final java.lang.String TAG_FRAGMENT_COLLECTION = "BookCollectionDialogFragmnet";
    private static final String KEY_COLLECTION_IDS = "collectionDialogFragmnet.KEY_COLLECTION_IDS";
    private BookCollectionInfo bookCollectionInfo;
    private CollectionDialogFragmnetListener listener;
    private ArrayList<BooksCollection> bookCollections;
    private BookCollectionsController bookCollectionsController;
    @Nullable
    private BookCollectionsController.BookCollectionsControllerCallback bookCollectionsControllerCallback;
    private BookCollectionRecyclerViewAdapter bookCollectionRecyclerViewAdapter;

    public static BookCollectionDialogFragmnet newInstance(BookCollectionInfo bookCollectionInfo) {
        BookCollectionDialogFragmnet frag = new BookCollectionDialogFragmnet();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(KEY_COLLECTION_IDS, gson.toJson(bookCollectionInfo));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        Bundle arguments = getArguments();


        Gson gson = new Gson();
        String serializedBookCollectionInfo = arguments.getString(KEY_COLLECTION_IDS);
        bookCollectionInfo = gson.fromJson(serializedBookCollectionInfo, BookCollectionInfo.class);
        bookCollectionsController = new BookCollectionsController(getContext(), bookCollectionsControllerCallback);
        bookCollections = bookCollectionsController.getAllBookCollections(getContext(), false, true);
        bookCollectionRecyclerViewAdapter = new BookCollectionRecyclerViewAdapter(bookCollections, bookCollectionInfo);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.collection_fragment_dialog, container, false);

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof CollectionDialogFragmnetListener) {
            listener = (CollectionDialogFragmnetListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement CollectionDialogFragmnetListener");
        }

        RecyclerView collectionRecyclerView = rootView.findViewById(R.id.collection_recycler_view);
        bookCollectionRecyclerViewAdapter.setHasStableIds(true);
        collectionRecyclerView.setHasFixedSize(true);
        collectionRecyclerView.setAdapter(bookCollectionRecyclerViewAdapter);
        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Button okButton = rootView.findViewById(R.id.btn_ok);
        okButton.setOnClickListener(v -> {
            listener.collectionChanged(bookCollectionInfo);
            bookCollectionsController.updateCollectionStatus(bookCollectionInfo, bookCollections);
            dismiss();
        });
        EditText newCollectionName = rootView.findViewById(R.id.new_collection_edit_text);
        ImageButton addCollectionButton = rootView.findViewById(R.id.add_collection);
        addCollectionButton.setOnClickListener(v ->
        {
            bookCollectionsController.createNewCollection(newCollectionName.getText().toString());
            bookCollections = bookCollectionsController.getAllBookCollections(getContext(), false, true);
            bookCollectionRecyclerViewAdapter.setCollections(bookCollections);
            bookCollectionRecyclerViewAdapter.notifyDataSetChanged();
            collectionRecyclerView.scrollToPosition(bookCollections.size() - 1);
            newCollectionName.setText("");
        });

        boolean enabled = !newCollectionName.getText().toString().isEmpty();
        setEnabledAddButton(addCollectionButton, enabled);
        //addCollectionButton.setClickable(!newCollectionName.getText().toString().isEmpty());
        newCollectionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    setEnabledAddButton(addCollectionButton, true);
                } else {
                    setEnabledAddButton(addCollectionButton, false);
                }
            }
        });

        Button cancelButton = rootView.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(v -> dismiss());

        return rootView;
    }

    private void setEnabledAddButton(ImageButton addCollectionButton, boolean enabled) {
        Util.setImageButtonEnabled(getContext(),
                enabled,
                addCollectionButton, R.drawable.ic_add_black_24dp);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookCollectionsController.BookCollectionsControllerCallback) {
            bookCollectionsControllerCallback =
                    ((BookCollectionsController.BookCollectionsControllerCallback) context);

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof BookCollectionsController.BookCollectionsControllerCallback) {
            bookCollectionsControllerCallback = null;
        }
    }

    public interface CollectionDialogFragmnetListener {
        void collectionChanged(BookCollectionInfo bookCollectionInfo);
    }

}
