package com.fekracomputers.islamiclibrary.browsing.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.activity.BookListActivity;
import com.fekracomputers.islamiclibrary.browsing.dialog.ConfirmBookDeleteDialogFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.browsing.util.BrowsingUtils;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.model.AuthorInfo;
import com.fekracomputers.islamiclibrary.model.BookCategory;
import com.fekracomputers.islamiclibrary.model.BookInfo;

import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_NOTIFY_WITHOUT_BOOK_ID;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public abstract class BookCardEventsCallback {
    protected FragmentActivity context;
    private BookDownloadReceiver mReceiver;

    public BookCardEventsCallback(FragmentActivity context) {
        this.context = context;
    }

    abstract public void notifyBookDownloadStatusUpdate(int bookId, int downloadStatus);

    abstract public void notifyBookDownloadStatusUpdate();

    public void openBookForReading(BookInfo bookInfo) {
        BrowsingUtils.openBookForReading(bookInfo, context);

    }

    public void StartDownloadingBook(BookInfo bookInfo) {
        BrowsingUtils.startDownloadingBook(bookInfo, context);

    }

    public void OnBookTitleClick(int book_id, String bookTitle) {
    }

    public boolean isInSelectionMode() {
        return false;
    }

    public boolean OnBookItemLongClicked(int bookId) {
        return false;
    }

    public boolean isBookSelected(int bookId) {
        return false;
    }

    public void bookSelected(int bookId, boolean checked) {
    }

    public boolean shouldDisplayDownloadedOnly() {
        return false;
    }

    public void mayBeSetTitle(String title) {
    }

    public void intializeListener() {
        mReceiver = new BookDownloadReceiver(this);
        context.registerReceiver(mReceiver, new IntentFilter(
                DownloadsConstants.BROADCAST_ACTION));
    }

    public void removeBookDownloadBroadcastListener() {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    public void onAuthorClicked(AuthorInfo authorInfo) {
        final Intent intent = new Intent(context, BookListActivity.class);
        intent.putExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID, authorInfo.getId());
        intent.putExtra(BookListFragment.FILTERTYPE, BookListFragment.FILTERBYAuthour);
        intent.putExtra(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME, authorInfo.getName());
        context.startActivity(intent);
    }

    public void onCategoryClicked(BookCategory category) {
        final Intent intent = new Intent(context, BookListActivity.class);
        intent.putExtra(BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID, category.getId());
        intent.putExtra(BookListFragment.FILTERTYPE, BookListFragment.FILTERBYCATEGORY);
        intent.putExtra(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_TITLE, category.getName());
        context.startActivity(intent);
    }

    public void onMoreButtonClicked(final BookInfo bookInfo, View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.book_overflow_delete_book:
                    DialogFragment confirmBatchDownloadDialogFragment = new ConfirmBookDeleteDialogFragment();

                    Bundle confirmDeleteDialogFragmentBundle = new Bundle();
                    confirmDeleteDialogFragmentBundle.putInt(ConfirmBookDeleteDialogFragment.KEY_NUMBER_OF_BOOKS_TO_DELETE, 1);
                    confirmDeleteDialogFragmentBundle.putInt(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID, bookInfo.getBookId());
                    confirmDeleteDialogFragmentBundle.putString(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE, bookInfo.getName());

                    confirmBatchDownloadDialogFragment.setArguments(confirmDeleteDialogFragmentBundle);
                    confirmBatchDownloadDialogFragment.show(context.getSupportFragmentManager(), "ConfirmBookDeleteDialogFragment");
                    return true;
                default:
                    return false;
            }
        });
        popup.inflate(R.menu.book_card_overflow);
        if (bookInfo.getDownloadStatus() < DownloadsConstants.STATUS_DOWNLOAD_COMPLETED) {
            popup.getMenu().removeItem(R.id.book_overflow_delete_book);
        }
        popup.show();
    }

    public void onBookDeleteConfirmation(int bookId) {
        BrowsingUtils.deleteBook(bookId, context);
    }

    public void onBookCollectionClicked(Cursor cursor) {
        //TODO
    }

    public static class BookDownloadReceiver extends BroadcastReceiver {

        private BookCardEventsCallback bookCardEventsCallback;

        public BookDownloadReceiver(BookCardEventsCallback bookCardEventsCallback) {

            this.bookCardEventsCallback = bookCardEventsCallback;
        }

        /**
         * This method is called by the system when a broadcast Intent is matched by this class'
         * intent filters
         *
         * @param context An Android context
         * @param intent  The incoming broadcast Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            int downloadStatus = intent.getIntExtra(DownloadsConstants.EXTRA_DOWNLOAD_STATUS,
                    DownloadsConstants.STATUS_INVALID);
            boolean notifyCangeWithoutBookId = intent.getBooleanExtra(EXTRA_NOTIFY_WITHOUT_BOOK_ID, false);
            if (!notifyCangeWithoutBookId) {
                int bookId = intent.getIntExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, 0);
                bookCardEventsCallback.notifyBookDownloadStatusUpdate(bookId, downloadStatus);

            } else {
                bookCardEventsCallback.notifyBookDownloadStatusUpdate();
            }
        }
    }


}
