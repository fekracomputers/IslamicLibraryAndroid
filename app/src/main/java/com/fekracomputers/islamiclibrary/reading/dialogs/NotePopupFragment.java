package com.fekracomputers.islamiclibrary.reading.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.Highlight;

import static android.view.MotionEvent.INVALID_POINTER_ID;


public class NotePopupFragment extends DialogFragment {

    private static final String HIGHLIGHT_TEXT_KEY = "highlight.text";
    private static final String HIGHLIGHT_COLOR_KEY = "highlight.color";
    private static final String HIGHLIGHT_DARK_COLOR_KEY = "highlight.DarkColor";
    private static final String HIGHLIGHT_NOTE_KEY = "highlight.noteText";

    private EditText mNoteEditText;


    public NotePopupFragment() {
    }

    public static NotePopupFragment newInstance(Highlight highlight) {

        NotePopupFragment frag = new NotePopupFragment();

        Bundle args = new Bundle();

        args.putString(HIGHLIGHT_TEXT_KEY, highlight.text);
        args.putString(HIGHLIGHT_NOTE_KEY, highlight.noteText);
        args.putInt(HIGHLIGHT_COLOR_KEY, Highlight.getHighlightColor(highlight.className));
        args.putInt(HIGHLIGHT_DARK_COLOR_KEY, Highlight.getDarkHighlightColor(highlight.className));
        frag.setArguments(args);

        return frag;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_highlight, container);
        v.findViewById(R.id.page_part_number).setVisibility(View.GONE);
        v.findViewById(R.id.date_time).setVisibility(View.GONE);

        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.requestFeature(Window.FEATURE_NO_TITLE);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();


        TextView mHighlightText = view.findViewById(R.id.text_view_highlight_text);

        mHighlightText.setOnTouchListener(new MovingTouchListener(getDialog().getWindow()));
        String title = args.getString(HIGHLIGHT_TEXT_KEY, "TEXT");
        mHighlightText.setText(title);
        int highlightColor = args.getInt(HIGHLIGHT_COLOR_KEY);
        mHighlightText.setBackgroundColor(highlightColor);


        mNoteEditText = view.findViewById(R.id.toc_card_body);
        String note = args.getString(HIGHLIGHT_NOTE_KEY, "");
        mNoteEditText.setText(note);
        int highlightDarkColor = args.getInt(HIGHLIGHT_DARK_COLOR_KEY);
        mNoteEditText.setBackgroundColor(highlightDarkColor);
        if (note.isEmpty()) {
            // Show soft keyboard automatically and request focus to field
            mNoteEditText.requestFocus();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        HighlightNoteDialogListener listener = (HighlightNoteDialogListener) getParentFragment();
        listener.onFinishHighlightNoteDialog(mNoteEditText.getText().toString());
    }

    public interface HighlightNoteDialogListener {

        void onFinishHighlightNoteDialog(@NonNull String inputText);

    }

    private class MovingTouchListener implements View.OnTouchListener {
        private float mLastTouchY;
        private float mLastTouchX;
        private int mActivePointerId = INVALID_POINTER_ID;

        private Window window;

        MovingTouchListener(Window window) {


            this.window = window;
        }

        @Override

        public boolean onTouch(View v, MotionEvent event) {
            event.offsetLocation(event.getRawX() - event.getX(), event.getRawY() - event.getY());
            final int action = event.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    final int pointerIndex = event.getActionIndex();
                    final float x = event.getX(pointerIndex);
                    final float y = event.getY(pointerIndex);

                    // Remember where we started (for dragging)
                    mLastTouchX = x;
                    mLastTouchY = y;
                    // Save the ID of this pointer (for dragging)
                    mActivePointerId = event.getPointerId(0);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position
                    final int pointerIndex =
                            event.findPointerIndex(mActivePointerId);

                    final float x = event.getX(pointerIndex);
                    final float y = event.getY(pointerIndex);

                    // Calculate the distance moved
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.x += dx;
                    layoutParams.y -= dy;
                    window.setAttributes(layoutParams);


                    // Remember this touch position for the next move event
                    mLastTouchX = x;
                    mLastTouchY = y;

                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {

                    final int pointerIndex = event.getActionIndex();
                    final int pointerId = event.getPointerId(pointerIndex);

                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchX = event.getX(newPointerIndex);
                        mLastTouchY = event.getY(newPointerIndex);
                        mActivePointerId = event.getPointerId(newPointerIndex);
                    }
                    break;
                }
            }
            return true;
        }
    }
}
