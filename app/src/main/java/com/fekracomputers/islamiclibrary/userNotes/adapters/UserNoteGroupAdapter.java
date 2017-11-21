package com.fekracomputers.islamiclibrary.userNotes.adapters;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fekracomputers.islamiclibrary.model.UserNote;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.ViewHolder;

/**
 * Created by Mohammad on 9/11/2017.
 */

public class UserNoteGroupAdapter extends GroupAdapter {
    private UserNoteInterActionListener userNoteInterActionListener;

    public void setUserNoteInterActionListener(UserNoteInterActionListener listener) {
        this.userNoteInterActionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutResId) {
        ViewHolder viewHolder = super.onCreateViewHolder(parent, layoutResId);
        if (viewHolder instanceof UserNoteViewHolder) {
            UserNoteViewHolder userNoteViewHolder = (UserNoteViewHolder) viewHolder;
            userNoteViewHolder.setUserNoteInterActionListener(userNoteInterActionListener);
        }
        return viewHolder;
    }

    public interface UserNoteInterActionListener {
        void onUserNoteClicked(UserNote userNote);

        void onUserNoteRemoved(UserNote userNote);
    }
}
