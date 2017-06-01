package com.fekracomputers.islamiclibrary.browsing.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.download.downloader.CoverImagesDownloader;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.model.BookInfo;

import java.util.List;

import static com.fekracomputers.islamiclibrary.browsing.adapters.RecyclerViewPartialUpdatePayload.UPDATE_CHECKED;
import static com.fekracomputers.islamiclibrary.browsing.adapters.RecyclerViewPartialUpdatePayload.UPDATE_DOWNLOAD_STATUS;
import static com.fekracomputers.islamiclibrary.browsing.adapters.RecyclerViewPartialUpdatePayload.UPDATE_VISABILITY;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_DOWNLOAD_REQUESTED;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 03/10/2016.
 */

public class BookListRecyclerViewAdapter extends RecyclerView.Adapter<BookListRecyclerViewAdapter.ViewHolder> {


    public static final int LINEAR_HORIZONTAL_LAYOUT_MANAGER = 2;
    private final int coulmn_title_id;
    private final int coulmn_authourId_id;
    private int itemCount;
    private int coulmn_authour_id;
    private int column_bookId_id;
    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn = -1;
    private DataSetObserver mDataSetObserver;
    private BookCardEventsCallback mListener;
    private int layoutManagerType = BookListFragment.LINEAR_LAYOUT_MANAGER;
    private int coulmn_downloadStatus_id;

    public BookListRecyclerViewAdapter(Context context, Cursor cursor, String IdColumnName, BookCardEventsCallback listener) {
        init(context, cursor, cursor.getColumnIndex(IdColumnName));
        itemCount = cursor.getCount();
        coulmn_title_id = cursor.getColumnIndexOrThrow(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE);
        coulmn_authour_id = cursor.getColumnIndexOrThrow(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME);
        column_bookId_id = cursor.getColumnIndexOrThrow(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID);
        mListener = listener;
        coulmn_authourId_id = cursor.getColumnIndexOrThrow(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID);
        coulmn_downloadStatus_id = cursor.getColumnIndex(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS);
    }

    public int getLayoutManagerType() {
        return layoutManagerType;
    }

    public void setLayoutManagerType(int layoutManagerType) {
        this.layoutManagerType = layoutManagerType;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutManagerType;
    }

    @Override
    public BookListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view.

        View v;
        switch (layoutManagerType) {

            case BookListFragment.GRID_LAYOUT_MANAGER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.boook_list_grid_element, parent, false);
                break;
            case LINEAR_HORIZONTAL_LAYOUT_MANAGER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_book_list_linear_horizontal, parent, false);
                break;
            default:
            case BookListFragment.LINEAR_LAYOUT_MANAGER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_book_list_linear_element, parent, false);

                break;
        }


        return new ViewHolder(v);

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
                    case UPDATE_DOWNLOAD_STATUS:
                        holder.bindDownloadStatus(recyclerViewPartialUpdatePayload.downloadStatus,
                                recyclerViewPartialUpdatePayload.getBookId());
                        break;
                }
            }
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        position = viewHolder.getAdapterPosition();

        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolder(viewHolder, mCursor);
    }

    private void onBindViewHolder(final ViewHolder holder, final Cursor movedCursor) {
        String bookTitle = movedCursor.getString(coulmn_title_id);
        holder.book_cover_name.setText(bookTitle);
        String authourName = movedCursor.getString(coulmn_authour_id);
        holder.book_cover_author.setText(authourName);
        int authorId = movedCursor.getInt(coulmn_authourId_id);
        int bookDownloadStatus = movedCursor.isNull(coulmn_downloadStatus_id) ? DownloadsConstants.STATUS_NOT_DOWNLOAD : movedCursor.getInt(coulmn_downloadStatus_id);
        final int bookId = movedCursor.getInt(column_bookId_id);
        holder.bookInfo = new BookInfo(bookId, bookTitle, authorId, authourName, bookDownloadStatus);
        holder.bindCheckBoxVisibilityValue(mListener.isInSelectionMode());

        if (holder.book_check_box.getVisibility() == View.VISIBLE)
            holder.book_check_box.setChecked(mListener.isBookSelected(holder.bookInfo.getBookId()));
        holder.bindDownloadStatus(bookDownloadStatus);

        Glide.with(mContext).load(CoverImagesDownloader.getImageUrl(mContext, holder.bookInfo.getBookId())).placeholder(R.drawable.no_book_image).into(holder.book_cover_image_view);


        //TODO Is it better to attach the listener here or in the constuctor of view holder


    }

    /**
     * @param context
     * @param cursor      the {@link Cursor} to display its rows
     * @param RowIdColumn zero based index of primary key coulmn of the cursor supplyed
     */

    private void init(Context context, Cursor cursor, int RowIdColumn) {
        mRowIdColumn = RowIdColumn;
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mDataSetObserver = new NotifyingDataSetObserver();

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
        return -1;
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
            //   mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
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

        ImageView book_cover_image_view;
        TextView book_cover_name;
        TextView book_cover_author;
        CheckBox book_check_box;
        BookInfo bookInfo;
        Button downloadButton;
        View downloadIndicator;

        public ViewHolder(final View bookCover) {
            super(bookCover);
            book_cover_image_view = (ImageView) bookCover.findViewById(R.id.book_cover);
            book_cover_name = (TextView) bookCover.findViewById(R.id.book_label);
            book_cover_author = (TextView) bookCover.findViewById(R.id.bookauthor);
            book_check_box = (CheckBox) bookCover.findViewById(R.id.book_ceckBox);
            if (null != mListener)
                book_check_box.setVisibility(mListener.isInSelectionMode() ? View.VISIBLE : View.GONE);
            bookCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnBookTitleClick(bookInfo.getBookId(), bookInfo.getName());
                }
            });
            bookCover.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean handled = false;
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been lonClicked.
                        handled = mListener.OnBookItemLongClicked(bookInfo.getBookId());
                        book_check_box.setChecked(handled);
                    }

                    return handled;
                }
            });

            book_check_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.bookSelected(bookInfo.getBookId(), ((CheckBox) v).isChecked());
                }
            });
            downloadButton = (Button) bookCover.findViewById(R.id.btn_download);
            downloadIndicator = bookCover.findViewById(R.id.download_indicator);
        }


        void bindCheckBoxVisibilityValue(Boolean isVisibale) {
            //when the selection mode is destroyed hide all checkboxes and un-check them
            book_check_box.setVisibility(isVisibale ? View.VISIBLE : View.GONE);
        }

        public void bindCheckBoxCheckedValue(Boolean value) {
            if (value != null)
                book_check_box.setChecked(value);
            else
                book_check_box.setChecked(mListener.isBookSelected(bookInfo.getBookId()));

        }

        public void bindDownloadStatus(int bookDownloadStatus, int bookId) {
            if (bookId == bookInfo.getBookId()) {
                bindDownloadStatus(bookDownloadStatus);
            }
        }

        public void bindDownloadStatus(int bookDownloadStatus) {
            if (bookDownloadStatus < STATUS_DOWNLOAD_REQUESTED) {
//                downloadButton.setImageResource(R.drawable.ic_file_download_black_24dp);
                downloadButton.setText(R.string.download_book);
                downloadIndicator.setBackgroundResource(R.color.indicator_book_not_downloaded);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.StartDownloadingBook(bookInfo);
                        v.setEnabled(false);
                        ((Button) v).setText(R.string.Downloading);
//                        downloadButton.setImageResource(R.drawable.ic_clear_all_black_24dp);
                        downloadIndicator.setBackgroundResource(R.color.indicator_book_downloading);
                    }
                });
            } else if (bookDownloadStatus >= STATUS_DOWNLOAD_REQUESTED && bookDownloadStatus < DownloadsConstants.STATUS_DOWNLOAD_COMPLETED) {
                downloadButton.setText(R.string.Downloading);
//                downloadButton.setImageResource(R.drawable.ic_clear_all_black_24dp);
                downloadIndicator.setBackgroundResource(R.color.indicator_book_downloading);
                downloadButton.setEnabled(false);
            } else if (bookDownloadStatus >= DownloadsConstants.STATUS_DOWNLOAD_COMPLETED && bookDownloadStatus < DownloadsConstants.STATUS_FTS_INDEXING_ENDED) {
                downloadButton.setText(R.string.preparing_book);
//                downloadButton.setImageResource(R.drawable.ic_clear_all_black_24dp);
                downloadIndicator.setBackgroundResource(R.color.indicator_book_downloading);
                downloadButton.setEnabled(false);
            } else if (bookDownloadStatus >= DownloadsConstants.STATUS_FTS_INDEXING_ENDED) {
                downloadButton.setText(R.string.open);
//                downloadButton.setImageResource(R.drawable.book_open_variant);
                downloadIndicator.setBackgroundResource(R.color.indicator_book_downloaded);
                downloadButton.setEnabled(true);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.openBookForReading(bookInfo);
                    }
                });
            }

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

