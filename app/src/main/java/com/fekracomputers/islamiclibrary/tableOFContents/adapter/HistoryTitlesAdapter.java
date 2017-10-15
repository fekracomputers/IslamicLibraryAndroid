package com.fekracomputers.islamiclibrary.tableOFContents.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.Title;

import java.util.LinkedList;


/**
 * Created by Mohammad Yahia on 13/11/2016.
 */
public class HistoryTitlesAdapter extends RecyclerView.Adapter<HistoryTitlesAdapter.ViewHolder> {
    private LinkedList<Title> mTitlesList ;
    private OnTitleHistoryClickListener mOnTitleHistoryClickListener;

    public HistoryTitlesAdapter(LinkedList<Title> historyList, OnTitleHistoryClickListener onTitleHistoryClickListener) {
        this.mTitlesList=historyList;
        mOnTitleHistoryClickListener = onTitleHistoryClickListener;
    }

    @Override
    public HistoryTitlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table_of_content_hstory, parent, false);
        return new HistoryTitlesAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title_history_element.setText(mTitlesList.get(position).title);

    }

    @Override
    public int getItemCount() {
        return mTitlesList.size();
    }


    public interface OnTitleHistoryClickListener {

        void OnTitleHistoryClicked(int titlePosition);


    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        final TextView title_history_element;

        ViewHolder(View v) {
            super(v);
            title_history_element = v.findViewById(R.id.title_history_element);
            v.setOnClickListener(view -> mOnTitleHistoryClickListener.OnTitleHistoryClicked(getAdapterPosition()));

        }
    }
}
