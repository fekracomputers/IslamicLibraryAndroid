package com.fekracomputers.islamiclibrary.userNotes.adapters;

import android.support.annotation.NonNull;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.Bookmark;
import com.fekracomputers.islamiclibrary.widget.BookmarkCard;

/**
 * Created by Mohammad on 8/11/2017.
 */

public class BookmarkItem extends UserNoteItem<Bookmark, BookmarkItem.ViewHolder> {

    public BookmarkItem(Bookmark bookmark, BookPartsInfo bookPartsInfo, BookInfo bookInfo) {
        super(bookmark, bookPartsInfo, bookInfo);
    }

    @NonNull
    public BookmarkItem.ViewHolder createViewHolder(@NonNull View itemView) {
        return new BookmarkItem.ViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull ViewHolder holder, int position) {
        holder.bookmarkCard.bind(getNote(), bookPartsInfo, bookInfo, holder.getUserNoteInterActionListener());
    }

    @Override
    public int getLayout() {
        return R.layout.item_global_bookmark;
    }

    public static class ViewHolder extends UserNoteViewHolder {
        public final BookmarkCard bookmarkCard;

        public ViewHolder(View view) {
            super(view);
            bookmarkCard = (BookmarkCard) view;
        }

    }
}
