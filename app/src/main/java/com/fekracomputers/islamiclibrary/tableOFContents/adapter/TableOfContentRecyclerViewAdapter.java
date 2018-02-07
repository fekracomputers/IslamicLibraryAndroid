package com.fekracomputers.islamiclibrary.tableOFContents.adapter;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseContract;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.model.Title;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;
import com.fekracomputers.islamiclibrary.tableOFContents.fragment.TableOfContentFragment;
import com.fekracomputers.islamiclibrary.utility.ArabicUtilities;

/**
 * Created by Mohammad Yahia on 03/10/2016.
 */

public class TableOfContentRecyclerViewAdapter extends RecyclerView.Adapter<TableOfContentRecyclerViewAdapter.ViewHolder> implements Filterable {


    private final int mbook_id;
    private final int coulmn_id_index;
    private final int column_title_text_indexd;
    private final int column_PAGE_ID_indexd;
    private final int column_page_indexd;
    private final int coulmn_is_parent_index;
    private final int coulmn_parentid_index;
    private final int column_partnumber_indexd;
    private BookDatabaseHelper bookDatabaseHelper;
    @Nullable
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn = -1;
    private DataSetObserver mDataSetObserver;
    private TableOfContentFragment.OnTableOfContentTitleClickListener mOnTableOfContentTitleClickListener;
    private OnTableOfContentExpandListener mOnTableOfContentExpandListener;
    @Nullable
    private Filter mFilter;
    @Nullable
    private Cursor mBeforeSearchCursor;
    private boolean mShouldHighlightCurrent;
    private int mCurrentTitleId;
    private Resources resources;

    public TableOfContentRecyclerViewAdapter(@NonNull BookDatabaseHelper bookDatabaseHelper,
                                             Resources resources,
                                             int bookId,
                                             OnTableOfContentExpandListener mOnTitleHistoryClickListener,
                                             TableOfContentFragment.OnTableOfContentTitleClickListener onTableOfContentTitleClickListener) {
        this.bookDatabaseHelper = bookDatabaseHelper;
        this.resources = resources;
        Cursor cursor = bookDatabaseHelper.getTitlesUnder(0);
        this.mOnTableOfContentTitleClickListener = onTableOfContentTitleClickListener;
        init(cursor, cursor.getColumnIndex(BookDatabaseContract.TitlesEntry.COLUMN_NAME_ID));
        mOnTableOfContentExpandListener = mOnTitleHistoryClickListener;
        mbook_id = bookId;
        coulmn_id_index = cursor.getColumnIndex(BookDatabaseContract.TitlesEntry.COLUMN_NAME_ID);
        coulmn_parentid_index = cursor.getColumnIndex(BookDatabaseContract.TitlesEntry.COLUMN_NAME_PARENT_ID);
        column_partnumber_indexd = cursor.getColumnIndex(BookDatabaseContract.PageEntry.COLUMN_NAME_PART_NUMBER);
        column_title_text_indexd = cursor.getColumnIndex(BookDatabaseContract.TitlesEntry.COLUMN_NAME_TITLE);
        column_PAGE_ID_indexd = cursor.getColumnIndex(BookDatabaseContract.TitlesEntry.COLUMN_NAME_PAGE_ID);
        column_page_indexd = cursor.getColumnIndex(BookDatabaseContract.PageEntry.COLUMN_NAME_PAGE_NUMBER);
        coulmn_is_parent_index = cursor.getColumnIndex(BookDatabaseHelper.IS_PARENT);
    }

    @NonNull
    @Override
    public TableOfContentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table_of_content, parent, false);


        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolder(viewHolder, mCursor, position);
    }

    private void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final Cursor cursor, int position
    ) {
        holder.book_id = mbook_id;
        int titleId = cursor.getInt(coulmn_id_index);
        holder.title = new Title(
                titleId,
                cursor.getInt(coulmn_parentid_index),
                cursor.getInt(column_page_indexd),
                cursor.getInt(column_partnumber_indexd),
                cursor.getInt(column_PAGE_ID_indexd),
                cursor.getString(column_title_text_indexd),
                cursor.getInt(coulmn_is_parent_index) == 1
        );


        holder.titlepageNumberTv.setText(
                TableOfContentsUtils.formatPageAndPartNumber(mOnTableOfContentTitleClickListener.getBookPartsInfo(),
                        holder.title.pageInfo,
                        R.string.part_and_page_with_text,
                        R.string.page_number,
                        resources));


        holder.tileText.setText(cursor.getString(column_title_text_indexd));
        if (!holder.title.isParent) {
            holder.expandTitleButton.setVisibility(View.GONE);
        } else {
            holder.expandTitleButton.setVisibility(View.VISIBLE);

        }
        if (mShouldHighlightCurrent && (mCurrentTitleId == holder.title.id)) {
            holder.titleViewElement.setBackgroundColor(0xFFBDBDBD);

        } else {
            holder.titleViewElement.setBackgroundResource(0);
        }

    }

    /**
     * @param cursor      the {@link Cursor} to display its rows
     * @param RowIdColumn zero based index of primary key coulmn of the cursor supplyed
     */
    private void init(@Nullable Cursor cursor, int RowIdColumn) {
        mRowIdColumn = RowIdColumn;
        mCursor = cursor;
        mDataValid = cursor != null;
        mDataSetObserver = new NotifyingDataSetObserver();

        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        super.setHasStableIds(true);
    }

    @Nullable
    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
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
    @Nullable
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

    public void displayChildrenOfWithHighlightCurrent(@NonNull Title title, int currentTitleId) {
        mShouldHighlightCurrent = true;
        mCurrentTitleId = currentTitleId;
        changeCursor(bookDatabaseHelper.getTitlesUnder(title.id));

    }


    public void displayChildrenOf(@NonNull Title title) {
        mShouldHighlightCurrent = false;
        changeCursor(bookDatabaseHelper.getTitlesUnder(title.id));
    }

    @Nullable
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new TitlesFilter();
        }
        return mFilter;
    }

    public void removeFilter() {
        mFilter = null;
        mBeforeSearchCursor = null;
    }


    public interface OnTableOfContentExpandListener {
        void OnOnTableOfContentExpandClicked(Title title);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public int book_id;
        Title title;
        TextView tileText;
        TextView titlepageNumberTv;
        View titleViewElement;
        ImageButton expandTitleButton;

        public ViewHolder(@NonNull View titleView) {
            super(titleView);

            this.titleViewElement = titleView;
            tileText = titleView.findViewById(R.id.title_tv);
            titlepageNumberTv = titleView.findViewById(R.id.title_page_tv);
            expandTitleButton = titleView.findViewById(R.id.btn_sub_titles);
            titleView.setOnClickListener(v -> mOnTableOfContentTitleClickListener.OnOnTableOfContentTitleClicked(title));
            expandTitleButton.setOnClickListener(view -> mOnTableOfContentExpandListener.OnOnTableOfContentExpandClicked(title));
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

    private class TitlesFilter extends Filter {

        @NonNull
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();

            Cursor c = bookDatabaseHelper.searchTitles((ArabicUtilities.cleanTextForSearchingWthStingBuilder((String) constraint) + "*"));
            results.values = c;
            results.count = c.getCount();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            if (getCursor() == mBeforeSearchCursor) {
                //since the original cursor needs to be kept for restoring if the search field is cleared
                swapCursor((Cursor) results.values);
            } else {
                changeCursor((Cursor) results.values);
            }
        }
    }
}

