package com.fekracomputers.islamiclibrary.homeScreen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.fekracomputers.islamiclibrary.R;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 16/5/2017.
 */
public class RenameCollectionDialogFragment extends DialogFragment {

    public static final java.lang.String KEY_OLD_NAME = "homeScreen.RenameCollectionDialogFragment.KEY_OLD_NAME";
    public static final java.lang.String KEY_COLLECTION_ID = "homeScreen.RenameCollectionDialogFragment.KEY_COLLECTION_ID";
    // Use this instance of the interface to deliver action events
    RenameCollectionListener mListener;


    public RenameCollectionDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        String oldName = getArguments().getString(KEY_OLD_NAME);
        int collectionId = getArguments().getInt(KEY_COLLECTION_ID);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        editText.setText(oldName);
        editText.setSelectAllOnFocus(true);
        builder
                .setMessage(R.string.dialog_msg_rename)
                .setTitle(R.string.dialog_title_rename)
                .setView(editText)
                .setPositiveButton(R.string.ok, (dialog, whichButton) -> {
                    String newName = editText.getText().toString();
                    mListener.onCollectionRenamed(collectionId,newName);
                })
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> {
                    dismiss();
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (RenameCollectionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RenameCollectionListener");
        }
    }


    public interface RenameCollectionListener {
        void onCollectionRenamed(int collectionId,String newName);
    }


}
