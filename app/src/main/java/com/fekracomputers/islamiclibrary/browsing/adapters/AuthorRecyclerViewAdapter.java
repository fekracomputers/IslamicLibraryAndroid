package com.fekracomputers.islamiclibrary.browsing.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.AuthorListFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.model.AuthorInfo;

import java.util.List;

import static com.fekracomputers.islamiclibrary.browsing.adapters.RecyclerViewPartialUpdatePayload.UPDATE_CHECKED;
import static com.fekracomputers.islamiclibrary.browsing.adapters.RecyclerViewPartialUpdatePayload.UPDATE_VISABILITY;

/**
 * Created by Mohammad Yahia on 27/10/2016.
 */
public class AuthorRecyclerViewAdapter extends RecyclerView.Adapter<AuthorRecyclerViewAdapter.ViewHolder> {


    private final int COULMN_HAS_DOWNLOADED_BOOKS;
    private final int COULMN_NUMBER_OF_BOOKS;
    private final Context context;
    private int itemCount;
    private final int COULMN_AUTHOUR_ID;
    private final int COLUMN_AUTHOUR_NAME_ID;
    private final int COLUMN_AUTHOUR_DEATH_DATE_ID;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn = -1;
    private DataSetObserver mDataSetObserver;
    private AuthorListFragment.OnAuthorItemClickListener mListener;
    private int layoutManagerType;

    public AuthorRecyclerViewAdapter(Cursor cursor, String IdColumnName, AuthorListFragment.OnAuthorItemClickListener listener, Context context) {
        this.context = context;
        init(cursor, cursor.getColumnIndex(IdColumnName));
        COLUMN_AUTHOUR_NAME_ID = cursor.getColumnIndexOrThrow(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME);
        COULMN_AUTHOUR_ID = cursor.getColumnIndexOrThrow(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID);
        COLUMN_AUTHOUR_DEATH_DATE_ID = cursor.getColumnIndexOrThrow(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR);
        COULMN_NUMBER_OF_BOOKS = cursor.getColumnIndex(BooksInformationDBContract.AuthorEntry.COUNT_OF_BOOKS);
        COULMN_HAS_DOWNLOADED_BOOKS = cursor.getColumnIndex(BooksInformationDBContract.AuthorEntry.HAS_DOWNLOADED_BOOKS);
        itemCount = cursor.getCount();
        mListener = listener;
    }


    public int getLayoutManagerType() {
        return layoutManagerType;
    }

    public void setLayoutManagerType(int layoutManagerType) {
        this.layoutManagerType = layoutManagerType;
    }

    @Override
    public AuthorRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        switch (layoutManagerType) {
            //TODO check if we need another layout for grid view or not
            case BookListFragment.GRID_LAYOUT_MANAGER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_authour_grid, parent, false);
                break;
            default:
            case BookListFragment.LINEAR_LAYOUT_MANAGER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_authour_list, parent, false);

        }


        return new AuthorRecyclerViewAdapter.ViewHolder(v);

    }

    @Override
    public int getItemViewType(int position) {
        return layoutManagerType;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        int size = payloads.size();

        if (size == 0) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            for (Object o : payloads) {
                RecyclerViewPartialUpdatePayload recyclerViewPartialUpdatePayload = ((RecyclerViewPartialUpdatePayload) o);
                switch (recyclerViewPartialUpdatePayload.requestCode) {
                    case UPDATE_VISABILITY:
                        holder.bindCheckBoxVisibilityValue(recyclerViewPartialUpdatePayload.booleanValue());
                        break;
                    case UPDATE_CHECKED:
                        holder.bindCheckBoxCheckedValue(recyclerViewPartialUpdatePayload.booleanValue());
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(AuthorRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        position = viewHolder.getAdapterPosition();
        Log.d("adapter", position + "");
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolder(viewHolder, mCursor);
    }

    private void onBindViewHolder(final AuthorRecyclerViewAdapter.ViewHolder holder, final Cursor movedCursor) {

        String authorName = movedCursor.getString(COLUMN_AUTHOUR_NAME_ID);
        int authourId = movedCursor.getInt(COULMN_AUTHOUR_ID);
        holder.authorNameTv.setText(authorName);
        int authourHigriDate = movedCursor.getInt(COLUMN_AUTHOUR_DEATH_DATE_ID);
        if (authourHigriDate != -1) {
            holder.authorDeathYear.setVisibility(View.VISIBLE);

            String deathYearFormatted = context.getString(R.string.death_hijri_year, authourHigriDate);
            holder.authorDeathYear.setText(deathYearFormatted);
        } else {
            holder.authorDeathYear.setVisibility(View.INVISIBLE);
        }


        holder.downloadIndicator.setBackgroundResource(movedCursor.getInt(COULMN_HAS_DOWNLOADED_BOOKS) == 1 ?
                R.color.indicator_book_downloaded :
                R.color.indicator_book_not_downloaded);


        int numberOfBooks = movedCursor.getInt(COULMN_NUMBER_OF_BOOKS);
        String NumberOfBooksFormatted = context.getString(R.string.number_of_books, numberOfBooks);
        holder.authourNumberOfBooksTextView.setText(NumberOfBooksFormatted);
        holder.authourId = authourId;
        holder.authorInfo = new AuthorInfo(authourId, authourHigriDate, authorName);

        if (null != mListener) {
            if (mListener.isInSelectionMode()) {
                holder.bindCheckBoxVisibilityValue(true);
                holder.bindCheckBoxCheckedValue(mListener.isAuthorSelected(authourId));
            } else {
                holder.bindCheckBoxVisibilityValue(false);

            }

        }

    }

    /**
     * @param cursor      the {@link Cursor} to display its rows
     * @param RowIdColumn zero based index of primary key coulmn of the cursor supplyed
     */
    private void init(Cursor cursor, int RowIdColumn) {
        mRowIdColumn = RowIdColumn;
        mCursor = cursor;
        mDataValid = cursor != null;
        mDataSetObserver = new AuthorRecyclerViewAdapter.NotifyingDataSetObserver();

        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        super.setHasStableIds(true);
    }


    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return itemCount;
        }
        return 0;

    }


    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            itemCount = newCursor.getCount();
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private final TextView authourNumberOfBooksTextView;
        TextView authorNameTv;
        TextView authorDeathYear;
        int authourId;
        CheckBox mCheckBox;
        AuthorInfo authorInfo;
        View downloadIndicator;


        public ViewHolder(final View authourView) {
            super(authourView);

            authorNameTv = authourView.findViewById(R.id.author_name);
            authorDeathYear = authourView.findViewById(R.id.death_year);
            authourNumberOfBooksTextView = authourView.findViewById(R.id.number_of_books_text_view);
            downloadIndicator = authourView.findViewById(R.id.download_indicator);
            mCheckBox = authourView.findViewById(R.id.author_checkBox);
            if (null != mListener)
                mCheckBox.setVisibility(mListener.isInSelectionMode() ? View.VISIBLE : View.GONE);
            authourView.setOnClickListener(v -> mListener.OnAuthorItemItemClick(authorInfo));
            authourView.setOnLongClickListener(v -> {
                boolean handled = false;
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been lonClicked.
                    handled = mListener.OnAuthorItemLongClicked(authourId);
                    mCheckBox.setChecked(handled);
                }

                return handled;
            });
            mCheckBox.setOnClickListener(v -> mListener.onAuthorSelected(authourId, ((CheckBox) v).isChecked()));
        }


        public void bindCheckBoxVisibilityValue(Boolean isVisible) {
            mCheckBox.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        public void bindCheckBoxCheckedValue(Boolean isChecked) {
            if (isChecked != null)
                mCheckBox.setChecked(isChecked);
            else
                mCheckBox.setChecked(mListener.isAuthorSelected(authourId));

        }
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }

}
