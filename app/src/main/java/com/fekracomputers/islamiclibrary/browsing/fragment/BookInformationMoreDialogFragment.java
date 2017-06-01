package com.fekracomputers.islamiclibrary.browsing.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 28/4/2017.
 */

public class BookInformationMoreDialogFragment extends DialogFragment {
    private static final String TAG = "BookInformationMoreDialogFragment";
    private static final String KEY_DIALOG_FRAGMENT_TITLE = "DialogFragmentTitle";
    private static final String KEY_DIALOG_BODY = "InformationMoreBody";

    public BookInformationMoreDialogFragment() {
    }

    public static BookInformationMoreDialogFragment newInstance(String title, String body) {
        BookInformationMoreDialogFragment frag = new BookInformationMoreDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_DIALOG_FRAGMENT_TITLE, title);
        args.putString(KEY_DIALOG_BODY, body);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_book_info_read_more, container, false);

        Bundle args = getArguments();
        String title = args.getString(KEY_DIALOG_FRAGMENT_TITLE, getResources().getString(R.string.authors));
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            Drawable drawable = getResources()
                    .getDrawable(R.drawable.ic_close_white_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            actionBar.setHomeAsUpIndicator(drawable);
        }


        String body = args.getString(KEY_DIALOG_BODY, getResources().getString(R.string.authors));
        TextView bodyTextView = (TextView) rootView.findViewById(R.id.more_dialog_body_text_view);
        bodyTextView.setText(body);
        setHasOptionsMenu(true);
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            // handle close button click here
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
