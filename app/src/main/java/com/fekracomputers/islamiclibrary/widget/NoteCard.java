package com.fekracomputers.islamiclibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.userNotes.adapters.UserNoteGroupAdapter;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.Highlight;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Mohammad on 9/11/2017.
 */

public class NoteCard extends CardView {
    private boolean isShowBook;
    private boolean isDhowAuthor;
    private boolean isShowCollection;
    private boolean isShowCategory;
    private boolean isEditable;
    private TextView partPageNumberTextView;
    private TextView dateTimeTextView;
    private EditText noteTextTextView;
    private TextView highlightTextTextView;

    public NoteCard(Context context) {
        this(context, null);
    }

    public NoteCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoteCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    public boolean isShowBook() {
        return isShowBook;
    }

    public void setShowBook(boolean showBook) {
        isShowBook = showBook;
    }

    public boolean isDhowAuthor() {
        return isDhowAuthor;
    }

    public void setDhowAuthor(boolean dhowAuthor) {
        isDhowAuthor = dhowAuthor;
    }

    public boolean isShowCollection() {
        return isShowCollection;
    }

    public void setShowCollection(boolean showCollection) {
        isShowCollection = showCollection;
    }

    public boolean isShowCategory() {
        return isShowCategory;
    }

    public void setShowCategory(boolean showCategory) {
        isShowCategory = showCategory;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        View rootView = inflate(getContext(), R.layout.note_card, this);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.NoteCard,
                defStyleAttr, 0);

        try {
            isShowBook = a.getBoolean(R.styleable.NoteCard_showBook, false);
            isDhowAuthor = a.getBoolean(R.styleable.NoteCard_showAuthor, false);
            isShowCollection = a.getBoolean(R.styleable.NoteCard_showCollection, false);
            isShowCategory = a.getBoolean(R.styleable.NoteCard_showCategory, false);
            isEditable = a.getBoolean(R.styleable.NoteCard_editable, false);
        } finally {
            a.recycle();
        }
        partPageNumberTextView = rootView.findViewById(R.id.page_part_number);
        dateTimeTextView = rootView.findViewById(R.id.date_time);
        noteTextTextView = rootView.findViewById(R.id.toc_card_body);
        highlightTextTextView = rootView.findViewById(R.id.text_view_highlight_text);
    }

    public void bind(@NonNull final Highlight highlight,
                     @NonNull final BookPartsInfo bookPartsInfo,
                     @Nullable final BookInfo bookInfo,
                     final UserNoteGroupAdapter.UserNoteInterActionListener userNoteInterActionListener) {
        setOnClickListener(v -> userNoteInterActionListener.onUserNoteClicked(highlight));
        partPageNumberTextView.setText(String.valueOf(highlight.pageInfo.pageNumber));
        partPageNumberTextView.setText(
                TableOfContentsUtils.formatPageAndPartNumber(bookPartsInfo,
                        highlight.pageInfo,
                        R.string.part_and_page_with_text,
                        R.string.page_number_with_label,
                        getResources()));

        highlightTextTextView.setText(highlight.text);
        highlightTextTextView.setBackgroundColor(Highlight.getHighlightColor(highlight.className));

        if (highlight.hasNote()) {
            noteTextTextView.setText(highlight.noteText);
            noteTextTextView.setBackgroundColor(Highlight.getDarkHighlightColor(highlight.className));
            noteTextTextView.setVisibility(View.VISIBLE);
            if (isEditable) {
                noteTextTextView.setFocusable(true);
            } else {
                noteTextTextView.setFocusable(false);
                noteTextTextView.setClickable(true);
                noteTextTextView.setTextIsSelectable(true);
            }

        } else {
            noteTextTextView.setVisibility(View.GONE);
        }


        try {
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, getResources().getConfiguration().locale);
            Date date = highlight.getDateTime();
            dateTimeTextView.setText(dateFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void bind(Highlight highlight, BookPartsInfo bookPartsInfo) {
        bind(highlight, bookPartsInfo, null, null);
    }
}
