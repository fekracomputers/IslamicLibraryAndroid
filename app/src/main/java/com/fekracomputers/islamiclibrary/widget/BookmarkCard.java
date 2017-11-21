package com.fekracomputers.islamiclibrary.widget;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.userNotes.adapters.UserNoteGroupAdapter;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.Bookmark;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import static com.fekracomputers.islamiclibrary.widget.AnimationUtils.removeBookmarkWithAnimation;

/**
 * Created by Mohammad on 9/11/2017.
 */

public class BookmarkCard extends CardView {
    private boolean isShowBook;
    private boolean isDhowAuthor;
    private boolean isShowCollection;
    private boolean isShowCategory;
    private TextView parentTitleTextView;
    private TextView pageNumberTextView;
    private TextView dateTimeTextView;
    private ImageView bookmarkIcon;

    public BookmarkCard(Context context) {
        this(context, null);
    }

    public BookmarkCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookmarkCard(Context context, AttributeSet attrs, int defStyleAttr) {
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


    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        View rootView = inflate(getContext(), R.layout.bookmark_card, this);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.NoteCard,
                defStyleAttr, 0);

        try {
            isShowBook = a.getBoolean(R.styleable.NoteCard_showBook, false);
            isDhowAuthor = a.getBoolean(R.styleable.NoteCard_showAuthor, false);
            isShowCollection = a.getBoolean(R.styleable.NoteCard_showCollection, false);
            isShowCategory = a.getBoolean(R.styleable.NoteCard_showCategory, false);
        } finally {
            a.recycle();
        }
        parentTitleTextView = rootView.findViewById(R.id.toc_card_body);
        pageNumberTextView = rootView.findViewById(R.id.page_part_number);
        dateTimeTextView = rootView.findViewById(R.id.date_time);
        bookmarkIcon = rootView.findViewById(R.id.bookmark_icon);
    }

    public void bind(@NonNull final Bookmark bookmark,
                     @NonNull final BookPartsInfo bookPartsInfo,
                     @Nullable final BookInfo bookInfo,
                     final UserNoteGroupAdapter.UserNoteInterActionListener userNoteInterActionListener) {
        bookmarkIcon.setVisibility(View.VISIBLE);
        bookmarkIcon.setScaleY(1);

        parentTitleTextView.setText(bookmark.parentTitle.title);
        pageNumberTextView.setText(
                TableOfContentsUtils.formatPageAndPartNumber(bookPartsInfo,
                        bookmark.pageInfo,
                        R.string.part_and_page_with_text,
                        R.string.page_number_with_label,
                        getResources()));


        try {
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, getResources().getConfiguration().locale);

            Date date = bookmark.getDateTime();
            dateTimeTextView.setText(dateFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        setOnClickListener(v -> userNoteInterActionListener.onUserNoteClicked(bookmark));
        bookmarkIcon.setOnClickListener(v -> removeBookmarkWithAnimation(bookmarkIcon,
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bookmarkIcon.setVisibility(View.INVISIBLE);
                        userNoteInterActionListener.onUserNoteRemoved(bookmark);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }
                )
        );
    }


}
