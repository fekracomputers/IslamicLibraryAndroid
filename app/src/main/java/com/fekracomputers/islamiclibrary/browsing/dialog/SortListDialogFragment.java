package com.fekracomputers.islamiclibrary.browsing.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fekracomputers.islamiclibrary.R;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 5/4/2017.
 */

public class SortListDialogFragment extends DialogFragment {

    public static final String TAG_FRAGMENT_SORT = "fragment_sort";
    private static final java.lang.String KEY_SORT_ARRAY_RES_ID = "KEY_SORT_ARRAY_RES_ID";
    private static final java.lang.String KEY_CURRENT_SORT_INDEX = "KEY_CURRENT_SORT_INDEX";
    @Nullable
    private OnSortDialogListener mListener;

    public SortListDialogFragment() {
    }

    @NonNull
    public static SortListDialogFragment newInstance(@ArrayRes int sortingChoices, int currentSortIndex) {
        SortListDialogFragment frag = new SortListDialogFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_SORT_ARRAY_RES_ID, sortingChoices);
        args.putInt(KEY_CURRENT_SORT_INDEX, currentSortIndex);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(true)
                .setTitle(R.string.library_sort_title)
                .setSingleChoiceItems(args.getInt(KEY_SORT_ARRAY_RES_ID),
                        args.getInt(KEY_CURRENT_SORT_INDEX), (dialog, which) -> {
                            mListener.sortMethodSelected(which);
                            dismiss();
                        });
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnSortDialogListener) {
            mListener = (OnSortDialogListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement OnSortDialogListener");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public interface OnSortDialogListener {
        void sortMethodSelected(int which);
    }
}
