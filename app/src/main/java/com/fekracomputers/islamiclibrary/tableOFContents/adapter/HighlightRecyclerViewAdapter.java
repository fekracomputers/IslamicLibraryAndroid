package com.fekracomputers.islamiclibrary.tableOFContents.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.Highlight;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;
import com.fekracomputers.islamiclibrary.tableOFContents.fragment.HighlightFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Highlight} and makes a call to the
 * specified {@link HighlightFragment.onHighlightClickListener}.
 */
public class HighlightRecyclerViewAdapter extends RecyclerView.Adapter<HighlightRecyclerViewAdapter.ViewHolder> {

    private static final String KEY_HIGHLIGHTS_SORT_INDEX = "HighlightsSortKey";
    private final List<Highlight> highlightList;
    private final HighlightFragment.onHighlightClickListener mListener;
    private final SharedPreferences sharedPref;
    private int mCurrentSortIndex;
    private Context mContext;

    public HighlightRecyclerViewAdapter(List<Highlight> items,
                                        HighlightFragment.onHighlightClickListener listener,
                                        Context context,
                                        SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        mCurrentSortIndex = sharedPref.getInt(KEY_HIGHLIGHTS_SORT_INDEX, 0);
        highlightList = items;
        Collections.sort(highlightList, comparators.get(mCurrentSortIndex));
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_highlight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.highlight = highlightList.get(position);

        holder.partPageNumberTextView.setText(String.valueOf(holder.highlight.pageInfo.pageNumber));
        holder.partPageNumberTextView.setText(
                TableOfContentsUtils.formatPageAndPartNumber(mListener.getBookPartsInfo(),
                        holder.highlight.pageInfo,
                        R.string.part_and_page_with_text,
                        R.string.page_number_with_label,
                        mContext.getResources()));

        holder.HighlightTextTextView.setText(holder.highlight.text);
        holder.HighlightTextTextView.setBackgroundColor(Highlight.getHighlightColor(holder.highlight.className));

        if (holder.highlight.hasNote()) {
            holder.noteTextTextView.setText(holder.highlight.noteText);
            holder.noteTextTextView.setBackgroundColor(Highlight.getDarkHighlightColor(holder.highlight.className));
            holder.noteTextTextView.setVisibility(View.VISIBLE);
            
        } else {
            holder.noteTextTextView.setVisibility(View.GONE);

        }

        try {
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, mContext.getResources().getConfiguration().locale);

            Date date = holder.highlight.getDateTime();
            holder.dateTimeTextView.setText(dateFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHighlightClicked(holder.highlight);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return highlightList.get(position).rowId;
    }

    public void sortBy(int which) {
        mCurrentSortIndex = which;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_HIGHLIGHTS_SORT_INDEX, which);
        editor.apply();

        Collections.sort(highlightList, comparators.get(which));
        notifyDataSetChanged();
    }
    private static final ArrayList<Comparator<Highlight>> comparators = new ArrayList<>();

    static {
        /*
            <string-array name="highlight_list_sorting" >
        <item>@string/library_sort_by_page</item>
        <item>@string/library_sort_by_date</item>
        <item>@string/library_sort_by_color</item>
         */
        comparators.add(new Comparator<Highlight>() {
            @Override
            public int compare(Highlight o1, Highlight o2) {
                return o1.pageInfo.pageId - o2.pageInfo.pageId;
            }
        });
        comparators.add(new Comparator<Highlight>() {
            @Override
            public int compare(Highlight o1, Highlight o2) {
                try {
                    return o1.getDateTime().compareTo(o2.getDateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        comparators.add(new Comparator<Highlight>() {
            @Override
            public int compare(Highlight o1, Highlight o2) {
                return o1.className.compareTo(o2.className);
            }
        });

    }


    @Override
    public int getItemCount() {
        return highlightList.size();
    }

    /**
     * @param sortByDate true to sort highlights by date false to sort them by position in the book
     */
    public void SortAndAnimate(boolean sortByDate) {
        Highlight.sortByDate = sortByDate;
        for (int i = 1; i < highlightList.size(); i++) {
            int j = Math.abs(Collections.binarySearch(highlightList.subList(0, i), highlightList.get(i)) + 1);
            Collections.rotate(highlightList.subList(j, i + 1), j - i);
            notifyItemMoved(i, j);
        }
    }

    public int getCurrentSortIndex() {
        return mCurrentSortIndex;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView partPageNumberTextView;
        public final TextView dateTimeTextView;
        private final TextView noteTextTextView;
        private final TextView HighlightTextTextView;
        public Highlight highlight;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            partPageNumberTextView = (TextView) view.findViewById(R.id.page_part_number);
            dateTimeTextView = (TextView) view.findViewById(R.id.date_time);
            noteTextTextView = (TextView) view.findViewById(R.id.toc_card_body);
            HighlightTextTextView = (TextView) view.findViewById(R.id.text_view_highlight_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + partPageNumberTextView.getText() + "'";
        }
    }

}
