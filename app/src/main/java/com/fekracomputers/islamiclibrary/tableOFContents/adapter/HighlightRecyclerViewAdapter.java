package com.fekracomputers.islamiclibrary.tableOFContents.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.Highlight;
import com.fekracomputers.islamiclibrary.tableOFContents.fragment.HighlightFragment;
import com.fekracomputers.islamiclibrary.widget.NoteCard;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Highlight} and makes a call to the
 * specified {@link HighlightFragment.onHighlightClickListener}.
 */
public class HighlightRecyclerViewAdapter extends RecyclerView.Adapter<HighlightRecyclerViewAdapter.ViewHolder> {

    private static final String KEY_HIGHLIGHTS_SORT_INDEX = "HighlightsSortKey";
    private static final ArrayList<Comparator<Highlight>> comparators = new ArrayList<>();

    static {
        /*
            <string-array name="highlight_list_sorting" >
        <item>@string/library_sort_by_page</item>
        <item>@string/library_sort_by_date</item>
        <item>@string/library_sort_by_color</item>
         */
        comparators.add((o1, o2) -> o1.pageInfo.pageId - o2.pageInfo.pageId);
        comparators.add((o1, o2) -> {
            try {
                return o1.getDateTime().compareTo(o2.getDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        comparators.add((o1, o2) -> o1.className.compareTo(o2.className));

    }

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
        final Highlight highlight = highlightList.get(position);
        holder.noteCard.bind(highlight, mListener.getBookPartsInfo());
        holder.noteCard.setOnClickListener(v -> mListener.onHighlightClicked(highlight));
    }

    @Override
    public long getItemId(int position) {
        return highlightList.get(position).id;
    }

    public void sortBy(int which) {
        mCurrentSortIndex = which;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_HIGHLIGHTS_SORT_INDEX, which);
        editor.apply();

        Collections.sort(highlightList, comparators.get(which));
        notifyDataSetChanged();
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
        public final NoteCard noteCard;

        public ViewHolder(View view) {
            super(view);
            noteCard = (NoteCard) view;
        }


    }

}
