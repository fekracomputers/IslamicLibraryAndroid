package com.fekracomputers.islamiclibrary.browsing.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;
import com.fekracomputers.islamiclibrary.browsing.controller.BookCollectionsController;
import com.fekracomputers.islamiclibrary.homeScreen.CollectionDialogFragmnet;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.browsing.util.BrowsingUtils;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.download.downloader.CoverImagesDownloader;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.model.BookCollectionInfo;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsBookmarksActivity;
import com.fekracomputers.islamiclibrary.utility.ArabicUtilities;
import com.fekracomputers.islamiclibrary.widget.BookInformationDetailsCard;
import com.fekracomputers.islamiclibrary.widget.HorizontalBookRecyclerView;

import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_DOWNLOAD_COMPLETED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_DOWNLOAD_REQUESTED;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookInformationFragment extends Fragment implements
        BrowsingActivityListingFragment,
        CollectionDialogFragmnet.CollectionDialogFragmnetListener {
    int bookId;
    int mBookDownloadStatus = DownloadsConstants.STATUS_INVALID;
    private BookInfo mBookInfo;
    private TextView mDownloadButtonText;
    private ImageView mDownloadButtonImage;
    private CardView mDownloadButtonLayout;
    private boolean diplayedInTableOfContent;
    private boolean isGrey;
    private LinearLayout mLinearLayoutContainer;
    private HorizontalBookRecyclerView authorMoreBooksHorizontalBookRecyclerView;
    private HorizontalBookRecyclerView categoryMoreBooksHorizontalBookRecyclerView;
    private BooksInformationDbHelper booksInformationDbHelper;
    private BookCardEventsCallback bookCardEventsCallback;
    private BookCollectionInfo bookCollectionInfo;
    private ImageView favouriteButtonImageView;
    private ImageView collectionButtonImageView;
    private BookCollectionsController bookCollectionsController;
    private TextView collectionCount;

    public BookInformationFragment() {
        // Required empty public constructor
    }


    public static BookInformationFragment newInstance(int bookId) {
        Bundle bundle = new Bundle();
        bundle.putInt("book_id", bookId);

        BookInformationFragment bookInformationFragment = new BookInformationFragment();
        bookInformationFragment.setArguments(bundle);
        return bookInformationFragment;
    }

    /**
     * Checking whether we should display the long discreption textView based on the length of data
     *
     * @param booKLongDescription the text of the book long descreption
     * @return whether to show the textview or not
     */
    private static boolean shouldDisplayCard(String booKLongDescription) {
        //If the dicreption is only one line don't display the textView
        return booKLongDescription != null &&
                !(
                        booKLongDescription.isEmpty() ||
                                booKLongDescription.matches("^\\s+$") || //cosits entirly from white spaces
                                booKLongDescription.matches("^\\[.+\\]\\n*\\r*$")
                );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookId = getArguments().getInt("book_id");
        diplayedInTableOfContent = getActivity().getClass().getSimpleName().equals(TableOfContentsBookmarksActivity.class.getSimpleName());
        BooksInformationDbHelper dbHelper = BooksInformationDbHelper.getInstance(getContext());
        mBookInfo = dbHelper.getBookInfo(bookId);
        bookCollectionInfo = UserDataDBHelper.getInstance(getContext(), bookId).getBookCollectionInfo();
        bookCollectionsController = new BookCollectionsController(getContext(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_book_information, container, false);


        //region Intiallize upper part Views
        TextView bookCategoryTextView = rootView.findViewById(R.id.book_category);
        bookCategoryTextView.setText(mBookInfo.getCategory().getName());
        bookCategoryTextView.setOnClickListener(v -> {
            bookCardEventsCallback.onCategoryClicked(mBookInfo.getCategory());
            // startBookListActivityForCategory(mBookInfo.getCategory().getId(), mBookInfo.getCategory().getName());
        });


        ImageView coverIamgeView = rootView.findViewById(R.id.book_cover);
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.no_book_image);

        Glide.with(getContext()).
                setDefaultRequestOptions(requestOptions).
                load(CoverImagesDownloader.getImageUrl(getContext(), mBookInfo.getBookId())).
                into(coverIamgeView);

        final String bookName = mBookInfo.getName();
        TextView bookNameTv = rootView.findViewById(R.id.book_name_tv);
        bookNameTv.setText(bookName);

        TextView bookAuthorTextView = rootView.findViewById(R.id.book_author);
        bookAuthorTextView.setText(mBookInfo.getAuthorName());
        bookAuthorTextView.setOnClickListener(v -> {
            bookCardEventsCallback.onAuthorClicked(mBookInfo.getAuthorInfo());

//                startBookListActivityForAuthor(mBookInfo.getAuthorInfo().getId(), mBookInfo.getAuthorInfo().getName());
        });
        //endregion

        //region collection info

        favouriteButtonImageView = rootView.findViewById(R.id.fav_button_img_view);
        collectionButtonImageView = rootView.findViewById(R.id.collection_button_img_view);
        collectionCount = rootView.findViewById(R.id.collection_count);
        bindCollections(bookCollectionInfo);

        RelativeLayout favButtonLayout = rootView.findViewById(R.id.fav_button_layout);
        favButtonLayout.setOnClickListener(v -> toggleFavourite());

        RelativeLayout collectionButtonLayout = rootView.findViewById(R.id.collection_button_layout);
        collectionButtonLayout.setOnClickListener(v -> showCollectionsDialog());

        //endregion


        mLinearLayoutContainer = rootView.findViewById(R.id.linear_container);
        isGrey = true;//first card in this region is grey

        maybeAddInformationCard(mBookInfo.getInformationCard(), getString(R.string.information_card));
        maybeAddInformationCard(mBookInfo.getBooKLongDescription(), getString(R.string.about_book));
        maybeAddInformationCard(mBookInfo.getAuthorInfo().getInfo(), getString(R.string.abouth_the_author));


        booksInformationDbHelper = BooksInformationDbHelper.
                getInstance(getContext());
        categoryMoreBooksHorizontalBookRecyclerView = new HorizontalBookRecyclerView(getContext());
        categoryMoreBooksHorizontalBookRecyclerView.setupRecyclerView(bookCardEventsCallback, getCategoryPreviewCursor());
        categoryMoreBooksHorizontalBookRecyclerView.setBackgroundResource(isGrey ? R.color.infoPage_details_gray :
                R.color.infoPage_details_white);
        categoryMoreBooksHorizontalBookRecyclerView.setTitleText(getString(R.string.book_info_similar_books));
        categoryMoreBooksHorizontalBookRecyclerView.setMoreTextViewOnClickListener(v -> {
            //  startBookListActivityForCategory(mBookInfo.getCategory().getId(), mBookInfo.getCategory().getName());
            bookCardEventsCallback.onCategoryClicked(mBookInfo.getCategory());

        });
        isGrey = !isGrey;
        mLinearLayoutContainer.addView(categoryMoreBooksHorizontalBookRecyclerView);


        Cursor authorPreviewCursor = getAuthorPreviewCursor();
        if (authorPreviewCursor.getCount() != 0) {
            authorMoreBooksHorizontalBookRecyclerView = new HorizontalBookRecyclerView(getContext());
            authorMoreBooksHorizontalBookRecyclerView.setVisibility(View.VISIBLE);
            authorMoreBooksHorizontalBookRecyclerView.setupRecyclerView(bookCardEventsCallback, authorPreviewCursor);
            categoryMoreBooksHorizontalBookRecyclerView.setBackgroundResource(isGrey ? R.color.infoPage_details_gray :
                    R.color.infoPage_details_white);
            authorMoreBooksHorizontalBookRecyclerView.setTitleText(
                    getString(
                            R.string.book_info_similar_books_by_authour,
                            ArabicUtilities.prepareForPrefixingLam(mBookInfo.getAuthorInfo().getName())
                    )
            );
            authorMoreBooksHorizontalBookRecyclerView.setMoreTextViewOnClickListener(v ->
                    bookCardEventsCallback.onAuthorClicked(mBookInfo.getAuthorInfo()));
            isGrey = !isGrey;
            mLinearLayoutContainer.addView(authorMoreBooksHorizontalBookRecyclerView);
        }

        mDownloadButtonLayout = rootView.findViewById(R.id.download_button_text_layout);
        mDownloadButtonText = rootView.findViewById(R.id.download_button_text);
        mDownloadButtonImage = rootView.findViewById(R.id.download_image);

        if (diplayedInTableOfContent) {
            rootView.findViewById(R.id.book_information_upper_part_separator).setVisibility(View.GONE);
            rootView.findViewById(R.id.book_information_download_frame).setVisibility(View.GONE);
        } else {
            BooksInformationDbHelper dbHelper = BooksInformationDbHelper.getInstance(getContext());
            bindDownloadStatus(dbHelper.getBookDownloadStatus(mBookInfo.getBookId()));
        }

        return rootView;
    }

    private void showCollectionsDialog() {
        CollectionDialogFragmnet collectionDialogFragmnet = CollectionDialogFragmnet.newInstance(bookCollectionInfo);
        //see this answer http://stackoverflow.com/a/37794319/3061221
        FragmentManager fm = getChildFragmentManager();
        collectionDialogFragmnet.show(fm, CollectionDialogFragmnet.TAG_FRAGMENT_COLLECTION);

    }

    private void toggleFavourite() {
        bookCollectionsController.toggleFavourite(bookCollectionInfo);
        bindCollections(bookCollectionInfo);
    }

    private void bindCollections(BookCollectionInfo bookCollectionInfo) {
        favouriteButtonImageView.setImageResource(bookCollectionInfo.isFavourite() ?
                R.drawable.ic_favorite_pink_with_check_24dp
                : R.drawable.ic_add_favorite_24dp);

        collectionButtonImageView.setImageResource((!bookCollectionInfo.doesBelongToColletionOtherTHanFavourite()) ?
                R.drawable.ic_add_collection :
                R.drawable.ic_collections_bookmark_green_24dp);

        if (bookCollectionInfo.doesBelongToColletionOtherTHanFavourite()) {
            collectionCount.setVisibility(View.VISIBLE);
            collectionCount.setText(getString(R.string.page_number, bookCollectionInfo.getNonFavouriteCollectionCount()));

        } else {
            collectionCount.setVisibility(View.INVISIBLE);
            collectionCount.setText("");
        }
    }

    private Cursor getAuthorPreviewCursor() {
        return booksInformationDbHelper.getBooksFiltered(
                BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID + "=?" +
                        " and " +
                        BooksInformationDBContract.BooksAuthors.TABLE_NAME + "." + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID + "!=?",
                new String[]{String.valueOf(mBookInfo.getAuthorInfo().getId()), String.valueOf(mBookInfo.getBookId())},
                null,
                bookCardEventsCallback.shouldDisplayDownloadedOnly(),
                null);
    }

    private Cursor getCategoryPreviewCursor() {
        return booksInformationDbHelper.getBooksFiltered(
                BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID + "=?" +
                        " and " +
                        BooksInformationDBContract.BooksCategories.TABLE_NAME + "." + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID + "!=?",
                new String[]{String.valueOf(mBookInfo.getCategory().getId()), String.valueOf(mBookInfo.getBookId())},
                null,
                bookCardEventsCallback.shouldDisplayDownloadedOnly(),
                null);
    }

    /**
     * adds a {@link BookInformationDetailsCard} with the supplied header and
     *
     * @param body   the body of the card
     * @param header the header of the card
     */
    private void maybeAddInformationCard(String body, String header) {
        if (shouldDisplayCard(body)) {
            body = body.replaceAll("\\r\\n|\\r", "\n");
            BookInformationDetailsCard booKLongDescriptionDetailsCard = new BookInformationDetailsCard(getContext(),
                    header,
                    body,
                    isGrey,
                    new InformationCardMoreClickListener(body, header)
            );
            mLinearLayoutContainer.addView(booKLongDescriptionDetailsCard);
            this.isGrey = !isGrey;
        }
    }

    public void bindDownloadStatus(int bookDownloadStatus) {
        if (bookDownloadStatus != mBookDownloadStatus) {
            if (bookDownloadStatus < STATUS_DOWNLOAD_REQUESTED) {
                mDownloadButtonText.setText(R.string.download_book);
                mDownloadButtonLayout.setBackgroundResource(R.color.indicator_book_not_downloaded);
                mDownloadButtonImage.setImageResource(R.drawable.ic_download_thin_white);

                mDownloadButtonLayout.setOnClickListener(v -> {
                    BrowsingUtils.startDownloadingBook(mBookInfo, getContext());
                    setDownloadButtonForStartDownloading();
                });
            } else if (bookDownloadStatus >= STATUS_DOWNLOAD_REQUESTED && bookDownloadStatus < STATUS_DOWNLOAD_COMPLETED) {
                setDownloadButtonForStartDownloading();
            } else if (bookDownloadStatus >= STATUS_DOWNLOAD_COMPLETED && bookDownloadStatus < DownloadsConstants.STATUS_FTS_INDEXING_ENDED) {
                setDownloadButtonForStartDownloading(R.string.preparing_book);
            } else if (bookDownloadStatus >= DownloadsConstants.STATUS_FTS_INDEXING_ENDED) {
                mDownloadButtonText.setText(R.string.open);
                mDownloadButtonLayout.setBackgroundResource(R.color.indicator_book_downloaded);
                mDownloadButtonImage.setImageResource(R.drawable.ic_read_glasses_white);
                mDownloadButtonImage.clearAnimation();
                mDownloadButtonLayout.setEnabled(true);
                mDownloadButtonLayout.setClickable(true);
                mDownloadButtonLayout.setOnClickListener(v -> BrowsingUtils.openBookForReading(mBookInfo, getContext()));
            }
            mBookDownloadStatus = bookDownloadStatus;
        }

    }

    private void setDownloadButtonForStartDownloading() {
        setDownloadButtonForStartDownloading(R.string.Downloading);
    }

    private void setDownloadButtonForStartDownloading(@StringRes int msgResId) {
        mDownloadButtonLayout.setEnabled(false);
        mDownloadButtonLayout.setClickable(false);
        mDownloadButtonText.setText(msgResId);
        mDownloadButtonImage.setImageResource(R.drawable.ic_loading_white);
        mDownloadButtonLayout.setBackgroundResource(R.color.indicator_book_downloading);
        mDownloadButtonImage.startAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.rotate_indefinetly));

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookCardEventListener) {
            bookCardEventsCallback = ((BookCardEventListener) context).getBookCardEventCallback();
            ((BookCardEventListener) context).registerListener(this);

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BookCardEventsCallback");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        bookCardEventsCallback = null;
        if (getActivity() instanceof BookCardEventListener)
            ((BookCardEventListener) getActivity()).unRegisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bookCardEventsCallback.mayBeSetTitle("");

    }

    @Override
    public void switchTodownloadedOnly(boolean checked) {
        if (categoryMoreBooksHorizontalBookRecyclerView != null) {
            categoryMoreBooksHorizontalBookRecyclerView.changeCursor(getCategoryPreviewCursor());
        }
        if (authorMoreBooksHorizontalBookRecyclerView != null) {
            authorMoreBooksHorizontalBookRecyclerView.changeCursor(getAuthorPreviewCursor());
        }
    }


    @Override
    public void actionModeDestroyed() {
        if (categoryMoreBooksHorizontalBookRecyclerView != null)
            categoryMoreBooksHorizontalBookRecyclerView.notifyDatasetChanged();
        if (authorMoreBooksHorizontalBookRecyclerView != null)
            authorMoreBooksHorizontalBookRecyclerView.notifyDatasetChanged();
    }

    @Override
    public void actionModeStarted() {
        if (categoryMoreBooksHorizontalBookRecyclerView != null)
            categoryMoreBooksHorizontalBookRecyclerView.notifyDatasetChanged();
        if (authorMoreBooksHorizontalBookRecyclerView != null)
            authorMoreBooksHorizontalBookRecyclerView.notifyDatasetChanged();
    }

    @Override
    public void bookSelectionStatusUpdate() {
        if (categoryMoreBooksHorizontalBookRecyclerView != null)
            categoryMoreBooksHorizontalBookRecyclerView.notifyDatasetChanged();
        if (authorMoreBooksHorizontalBookRecyclerView != null)
            authorMoreBooksHorizontalBookRecyclerView.notifyDatasetChanged();
    }

    @Override
    public int getType() {
        return BrowsingActivity.BOOK_INFORMATION_TYPE;
    }

    @Override
    public void reAcquireCursors() {
        if (categoryMoreBooksHorizontalBookRecyclerView != null)
            categoryMoreBooksHorizontalBookRecyclerView.changeCursor(getCategoryPreviewCursor());
        if (authorMoreBooksHorizontalBookRecyclerView != null)
            authorMoreBooksHorizontalBookRecyclerView.changeCursor(getAuthorPreviewCursor());

        BooksInformationDbHelper dbHelper = BooksInformationDbHelper.getInstance(getContext());
        bindDownloadStatus(dbHelper.getBookDownloadStatus(mBookInfo.getBookId()));
    }

    @Override
    public void closeCursors() {
        if (categoryMoreBooksHorizontalBookRecyclerView != null)
            categoryMoreBooksHorizontalBookRecyclerView.closeCursor();
        if (authorMoreBooksHorizontalBookRecyclerView != null)
            authorMoreBooksHorizontalBookRecyclerView.closeCursor();
    }

    @Override
    public void selectAllItems(int id) {

    }

    @Override
    public void BookDownloadStatusUpdate(int bookId, int downloadStatus) {
        if (categoryMoreBooksHorizontalBookRecyclerView != null)
            categoryMoreBooksHorizontalBookRecyclerView.changeCursor(getCategoryPreviewCursor());
        if (authorMoreBooksHorizontalBookRecyclerView != null)
            authorMoreBooksHorizontalBookRecyclerView.changeCursor(getAuthorPreviewCursor());
        if (bookId == mBookInfo.getBookId()) {
            bindDownloadStatus(downloadStatus);
        }
    }

    @Override
    public void collectionChanged(BookCollectionInfo bookCollectionInfo) {
        this.bookCollectionInfo = bookCollectionInfo;
        bindCollections(bookCollectionInfo);
    }


    private class InformationCardMoreClickListener implements View.OnClickListener {
        private final String body;
        private final String header;

        public InformationCardMoreClickListener(String body, String header) {
            this.body = body;
            this.header = header;
        }

        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            BookInformationMoreDialogFragment newFragment = BookInformationMoreDialogFragment.newInstance(header, body);

            FragmentTransaction transaction = fragmentManager
                    .beginTransaction()
                    //     .setCustomAnimations(R.anim.grow_fade_in_from_bottom,R.anim.shrink_fade_out_from_bottom)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .add(android.R.id.content, newFragment)
                    .addToBackStack(null);
            transaction.commit();


        }
    }
}
