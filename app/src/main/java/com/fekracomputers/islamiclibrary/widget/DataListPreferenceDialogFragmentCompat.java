package com.fekracomputers.islamiclibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreferenceDialogFragmentCompat;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;

import java.util.List;


public class DataListPreferenceDialogFragmentCompat extends ListPreferenceDialogFragmentCompat {

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        int selectedIndex = getListPreference().findIndexOfValue(getListPreference().getValue());
        ListAdapter adapter = new StorageArrayAdapter(
                getContext(),
                R.layout.data_storage_location_item,
                getListPreference().getEntries(),
                selectedIndex,
                getListPreference().getStorageList());
        builder.setAdapter(adapter, this);
        super.onPrepareDialogBuilder(builder);
    }


    public static DataListPreferenceDialogFragmentCompat newInstance(Preference preference) {
        DataListPreferenceDialogFragmentCompat fragment = new DataListPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }

    private DataListPreference getListPreference() {
        return (DataListPreference) this.getPreference();
    }

    static class ViewHolder {
        public TextView titleTextView;
        public TextView freeSpaceTextView;
        public CheckedTextView checkedTextView;
        public TextView mountPointTextView;
    }

    public class StorageArrayAdapter extends ArrayAdapter<CharSequence> {
        private int mSelectedIndex = 0;
        private List<StorageUtils.Storage> mFreeSpaces;

        public StorageArrayAdapter(Context context, int textViewResourceId, CharSequence[] objects,
                                   int selectedIndex, List<StorageUtils.Storage> freeSpaces) {
            super(context, textViewResourceId, objects);
            mSelectedIndex = selectedIndex;
            mFreeSpaces = freeSpaces;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                convertView = inflater.inflate(R.layout.data_storage_location_item, parent, false);

                holder = new ViewHolder();
                holder.titleTextView = (TextView) convertView.findViewById(R.id.storage_label);
                holder.freeSpaceTextView = (TextView) convertView.findViewById(R.id.available_free_space);
                holder.mountPointTextView = (TextView) convertView.findViewById(R.id.mount_point);
                holder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checked_text_view);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.titleTextView.setText(getItem(position));
            holder.freeSpaceTextView.setText(getString(R.string.free_space,mFreeSpaces.get(position).getFreeSpace()));
            holder.mountPointTextView.setText(getString(R.string.mount_point,mFreeSpaces.get(position).getMountPoint()));
            holder.checkedTextView.setText(null); // we have a 'custom' label
            if (position == mSelectedIndex) {
                holder.checkedTextView.setChecked(true);
            }

            return convertView;
        }
    }


}
