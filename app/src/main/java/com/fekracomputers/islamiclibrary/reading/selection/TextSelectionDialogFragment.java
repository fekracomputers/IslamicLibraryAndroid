package com.fekracomputers.islamiclibrary.reading.selection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.fekracomputers.islamiclibrary.R;

public class TextSelectionDialogFragment extends DialogFragment {


    public TextSelectionDialogFragment() {

        // Empty constructor is required for DialogFragment

        // Make sure not to add arguments to the constructor

        // Use `newInstance` instead as shown below

    }


    public static TextSelectionDialogFragment newInstance(float x,float y) {

        TextSelectionDialogFragment frag = new TextSelectionDialogFragment();

        Bundle args = new Bundle();

        args.putFloat("x", x);
        args.putFloat("y", y);
        frag.setArguments(args);

        return frag;

    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_text_selection, container);
        Bundle args = getArguments();
        setDialogPosition(args.getFloat("x"), args.getFloat("y"));
        return view;

    }

    private void setDialogPosition(float sourceX, float sourceY) {
        Window window = getDialog().getWindow();

        // set "origin" to top left corner
        window.setGravity(Gravity.TOP | Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();
        params.x = (int) sourceX;

        params.y = (int) sourceY;

        window.setAttributes(params);
    }

    public int dpToPx(float valueInDp) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();

        // Less dimmed background; see http://stackoverflow.com/q/13822842/56285
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0f; // dim only a little bit
        window.setAttributes(params);
    }
}