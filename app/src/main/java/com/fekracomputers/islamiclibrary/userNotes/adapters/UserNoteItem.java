package com.fekracomputers.islamiclibrary.userNotes.adapters;

import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.UserNote;
import com.xwray.groupie.Item;

/**
 * Created by Mohammad on 8/11/2017.
 */

public abstract class UserNoteItem
        <NoteType extends UserNote, viewHolderType extends UserNoteViewHolder>
        extends Item<viewHolderType> {

    public NoteType note;
    protected BookPartsInfo bookPartsInfo;
    protected BookInfo bookInfo;

    public UserNoteItem(NoteType note, BookPartsInfo bookPartsInfo, BookInfo bookInfo) {
        super();
        this.note = note;
        this.bookPartsInfo = bookPartsInfo;
        this.bookInfo = bookInfo;
    }

    public NoteType getNote() {
        return note;
    }


}
