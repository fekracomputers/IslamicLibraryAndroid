package com.fekracomputers.islamiclibrary.tableOFContents.adapter;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.Bookmark;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;
import com.fekracomputers.islamiclibrary.tableOFContents.fragment.BookmarkFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.fekracomputers.islamiclibrary.widget.AnimationUtils.removeBookmarkWithAnimation;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Bookmark} and makes a call to the
 * specified {@link BookmarkFragment.onBookmarkClickListener}.
 */
public class BookmarkRecyclerViewAdapter extends RecyclerView.Adapter<BookmarkRecyclerViewAdapter.ViewHolder> {

    private static final String KEY_BOOKMARKS_SORT_INDEX = "BookmarkSortIndexKey";
    private static final ArrayList<Comparator<Bookmark>> comparators = new ArrayList<>();

    static {
        comparators.add((o1, o2) -> o1.pageInfo.pageId - o2.pageInfo.pageId);
        comparators.add((o1, o2) -> {
            try {
                return o1.getDateTime().compareTo(o2.getDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

    }

    private final List<Bookmark> bookmarkList;
    private final BookmarkFragment.onBookmarkClickListener onBookmarkClickListener;
    private final SharedPreferences sharedPref;
    private Context mContext;
    private UserDataDBHelper userDataDBHelper;
    private int mCurrentSortIndex;

    public BookmarkRecyclerViewAdapter(List<Bookmark> items, BookmarkFragment.onBookmarkClickListener listener, Context context, UserDataDBHelper userDataDBHelper, SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        mCurrentSortIndex = sharedPref.getInt(KEY_BOOKMARKS_SORT_INDEX, 0);
        bookmarkList = items;
        Collections.sort(bookmarkList,comparators.get(mCurrentSortIndex));
        onBookmarkClickListener = listener;
        mContext = context;
        this.userDataDBHelper = userDataDBHelper;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return bookmarkList.get(position).pageInfo.pageId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bookmark = bookmarkList.get(position);
        holder.bookmarkIcon.setVisibility(View.VISIBLE);
        holder.bookmarkIcon.setScaleY(1);

        holder.parentTitleTextView.setText(holder.bookmark.parentTitle.title);
        holder.pageNumberTextView.setText(
                TableOfContentsUtils.formatPageAndPartNumber(onBookmarkClickListener.getBookPartsInfo(),
                        holder.bookmark.pageInfo,
                        R.string.part_and_page_with_text,
                        R.string.page_number_with_label,
                        mContext.getResources()));


        try {
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, mContext.getResources().getConfiguration().locale);

            Date date = holder.bookmark.getDateTime();
            holder.dateTimeTextView.setText(dateFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.mView.setOnClickListener(v -> onBookmarkClickListener.onBookmarkClicked(holder.bookmark));
        holder.bookmarkIcon.setOnClickListener(v -> removeBookmarkWithAnimation(holder.bookmarkIcon, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                holder.bookmarkIcon.setVisibility(View.INVISIBLE);
                userDataDBHelper.RemoveBookmark(holder.bookmark.pageInfo.pageId);
                bookmarkList.remove(holder.bookmark);
                notifyItemRemoved(bookmarkList.indexOf(holder.bookmark));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }));
    }

    public void sortBy(int which) {
        mCurrentSortIndex = which;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_BOOKMARKS_SORT_INDEX, which);
        editor.apply();

        Collections.sort(bookmarkList, comparators.get(which));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    /**
     * @param sortByDate true to sort bookmarks by date false to sort them by position in the book
     */
    public void SortAndAnimate(boolean sortByDate) {
        Bookmark.sortByDate = sortByDate;
        for (int i = 1; i < bookmarkList.size(); i++) {
            int j = Math.abs(Collections.binarySearch(bookmarkList.subList(0, i), bookmarkList.get(i)) + 1);
            Collections.rotate(bookmarkList.subList(j, i + 1), j - i);
            notifyItemMoved(i, j);
        }
    }

    public int getmCurrentSortIndex() {
        return mCurrentSortIndex;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView parentTitleTextView;
        final TextView pageNumberTextView;
        final TextView dateTimeTextView;
        final ImageView bookmarkIcon;
        Bookmark bookmark;

        ViewHolder(View view) {
            super(view);
            mView = view;
            parentTitleTextView = view.findViewById(R.id.toc_card_body);
            pageNumberTextView = view.findViewById(R.id.page_part_number);
            dateTimeTextView = view.findViewById(R.id.date_time);
            bookmarkIcon = view.findViewById(R.id.bookmark_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + pageNumberTextView.getText() + "'";
        }


    }

}
