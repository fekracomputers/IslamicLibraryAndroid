package com.fekracomputers.islamiclibrary.browsing.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookCategoryFragment;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.model.BookCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.fekracomputers.islamiclibrary.browsing.adapters.RecyclerViewPartialUpdatePayload.UPDATE_CHECKED;
import static com.fekracomputers.islamiclibrary.browsing.adapters.RecyclerViewPartialUpdatePayload.UPDATE_VISABILITY;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BookCategory} and makes a call to the
 * specified {@link BookCategoryFragment.OnCategoryItemClickListener}.
 */
public class BookCategoryRecyclerViewAdapter
        extends RecyclerView.Adapter<BookCategoryRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    private static final String KEY_BOOKK_LIST_SORT_INDEX_ONLY = "BookCategoryFragmentSortIndex";
    private static final ArrayList<Comparator<BookCategory>> comparators = new ArrayList<>();

    static {
        comparators.add((o1, o2) -> o1.getOrder() - o2.getOrder());
        comparators.add((o1, o2) -> o1.getName().compareTo(o2.getName()));
        comparators.add((o1, o2) -> o2.getNumberOfBooks() - o1.getNumberOfBooks());

    }

    private final BookCategoryFragment.OnCategoryItemClickListener mListener;
    private final Object mLock = new Object();
    @NonNull
    private final SharedPreferences sharedPref;
    private List<BookCategory> mBookCategoryList;
    private Context context;
    private BookGridFilter mFilter;
    private ArrayList<BookCategory> mOriginalValues;
    private int mCurrentSortIndex;
    private int mLayoutManagerType;

    public BookCategoryRecyclerViewAdapter(List<BookCategory> items,
                                           BookCategoryFragment.OnCategoryItemClickListener listener,
                                           @NonNull SharedPreferences sharedPref,
                                           Context context) {
        this.sharedPref = sharedPref;
        mCurrentSortIndex = sharedPref.getInt(KEY_BOOKK_LIST_SORT_INDEX_ONLY, 0);
        mBookCategoryList = items;
        Collections.sort(mBookCategoryList, comparators.get(mCurrentSortIndex));
        mListener = listener;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mBookCategoryList.get(position).getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        switch (mLayoutManagerType) {
            //TODO check if we need another layout for grid view or not
            case BookCategoryFragment.GRID_LAYOUT_MANAGER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_book_category_grid, parent, false);
                break;
            default:
            case BookCategoryFragment.LINEAR_LAYOUT_MANAGER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_book_category_list, parent, false);

        }


        return new BookCategoryRecyclerViewAdapter.ViewHolder(v);
    }

    public void setLayoutManagerType(int layoutManagerType) {
        mLayoutManagerType = layoutManagerType;
    }

    @Override
    public int getItemViewType(int position) {
        return mLayoutManagerType;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        BookCategory category = mBookCategoryList.get(position);
        holder.category = category;
        holder.mCategoryTitleView.setText(category.getName());
        String NumberOfBooksFormatted = context.getString(R.string.number_of_books, category.getNumberOfBooks());
        holder.mNumberOfBooksTextView.setText(NumberOfBooksFormatted);
        holder.downloadIndicator.setBackgroundResource(category.hasDownloadedBooks() ?
                R.color.indicator_book_downloaded :
                R.color.indicator_book_not_downloaded);


        if (null != mListener) {
            if (mListener.isInSelectionMode()) {
                holder.bindCheckBoxVisibilityValue(true);
                holder.bindCheckBoxCheckedValue(mListener.isCategorySelected(holder.category.getId()));
            } else {
                holder.bindCheckBoxVisibilityValue(false);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mBookCategoryList.size();
    }
    /*
     *      <item>@string/library_sort_by_default</item>
     *  <item>@string/library_sort_by_name</item>
     *   <item>@string/library_sort_by_number_of_books</item>
     *   */

    public void changeDataset(List<BookCategory> bookCategories) {
        mBookCategoryList = bookCategories;
        Collections.sort(mBookCategoryList, comparators.get(mCurrentSortIndex));

    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new BookGridFilter();
        }
        return mFilter;
    }

    public int getCurrentSortIndex() {
        return mCurrentSortIndex;
    }

    public void sortBy(int which) {
        mCurrentSortIndex = which;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_BOOKK_LIST_SORT_INDEX_ONLY, which);
        editor.apply();

        Collections.sort(mBookCategoryList, comparators.get(which));
        notifyDataSetChanged();
    }

    public void add(BookCategory categoriesFiltered) {
        if (!mBookCategoryList.contains(categoriesFiltered)) {
            mBookCategoryList.add(categoriesFiltered);
            notifyItemInserted(mBookCategoryList.size() - 1);
        }
    }

    public void setCategoryDownloadStatus(int catId, int downloadStatus) {
        if (downloadStatus > DownloadsConstants.STATUS_DOWNLOAD_REQUESTED && mBookCategoryList.contains(new BookCategory(catId))) {
            int index = getPositonById(catId);
            mBookCategoryList.get(index).setHasDownloadedBooks(true);
            notifyItemChanged(index);
        }
    }

    public int getPositonById(int catId) {
        return mBookCategoryList.indexOf(new BookCategory(catId));
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView mCategoryTitleView;
        final CheckBox mCheckBox;
        final TextView mNumberOfBooksTextView;
        final View downloadIndicator;
        BookCategory category;

        ViewHolder(@NonNull View view) {
            super(view);

            mCategoryTitleView = view.findViewById(R.id.category_title_tv);
            mCheckBox = view.findViewById(R.id.category_checkBox);
            mNumberOfBooksTextView = view.findViewById(R.id.number_of_books_text_view);
            downloadIndicator = view.findViewById(R.id.download_indicator);
            if (null != mListener)
                mCheckBox.setVisibility(mListener.isInSelectionMode() ? View.VISIBLE : View.GONE);
            view.setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been clicked.
                    mListener.OnCategoryItemClick(category);


                }
            });

            view.setOnLongClickListener(v -> {
                boolean handled = false;
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been lonClicked.
                    handled = mListener.OnCategoryItemLongClicked(category.getId());
                    mCheckBox.setChecked(handled);
                }

                return handled;
            });
            mCheckBox.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onCategorySelected(category.getId(), ((CheckBox) v).isChecked());
                }
            });


        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mCategoryTitleView.getText() + "'";
        }


        public void bindCheckBoxVisibilityValue(Boolean isVisible) {
            mCheckBox.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        public void bindCheckBoxCheckedValue(@Nullable Boolean isChecked) {
            if (isChecked != null)
                mCheckBox.setChecked(isChecked);
            else
                mCheckBox.setChecked(mListener.isCategorySelected(category.getId()));


        }
    }

    protected class BookGridFilter extends Filter {

        @NonNull
        @Override
        protected FilterResults performFiltering(@Nullable CharSequence prefix) {


            final FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mBookCategoryList);
                }
            }

            //empty Query
            if (prefix == null || prefix.length() == 0) {
                final ArrayList<BookCategory> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();
                final ArrayList<BookCategory> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<BookCategory> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final BookCategory value = values.get(i);
                    final String valueText = value.getName();

                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            mBookCategoryList = (ArrayList<BookCategory>) results.values;
            notifyDataSetChanged();
        }
    }
}
