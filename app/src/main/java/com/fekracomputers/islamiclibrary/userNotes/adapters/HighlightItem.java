package com.fekracomputers.islamiclibrary.userNotes.adapters;

import android.support.annotation.NonNull;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.Highlight;
import com.fekracomputers.islamiclibrary.widget.NoteCard;

/**
 * Created by Mohammad on 8/11/2017.
 */

public class HighlightItem extends UserNoteItem<Highlight, HighlightItem.ViewHolder> {

    public HighlightItem(Highlight highlight, BookPartsInfo bookPartsInfo, BookInfo bookInfo) {
        super(highlight, bookPartsInfo, bookInfo);
    }

    @NonNull
    public HighlightItem.ViewHolder createViewHolder(@NonNull View itemView) {
        return new HighlightItem.ViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull ViewHolder holder, int position) {
        holder.noteCard.bind(getNote(), bookPartsInfo, bookInfo, holder.getUserNoteInterActionListener());
    }

    @Override
    public int getLayout() {
        return R.layout.item_global_highlight;
    }

    public static class ViewHolder extends UserNoteViewHolder {
        @NonNull
        public final NoteCard noteCard;
        public ViewHolder(View view) {
            super(view);
            noteCard = (NoteCard) view;
            noteCard.setShowBook(true);
            noteCard.setShowCategory(true);
            noteCard.setShowAuthor(true);
        }

    }
}
