package com.fekracomputers.islamiclibrary.userNotes.adapters;

import android.view.View;

/**
 * Created by Mohammad on 9/11/2017.
 */

class UserNoteViewHolder extends com.xwray.groupie.ViewHolder {
    protected UserNoteGroupAdapter.UserNoteInterActionListener listener;

    public UserNoteViewHolder(View rootView) {
        super(rootView);
    }

    public void setUserNoteInterActionListener(UserNoteGroupAdapter.UserNoteInterActionListener listener) {
        this.listener=listener;
    }

    public UserNoteGroupAdapter.UserNoteInterActionListener getUserNoteInterActionListener() {
        return listener;
    }
}
