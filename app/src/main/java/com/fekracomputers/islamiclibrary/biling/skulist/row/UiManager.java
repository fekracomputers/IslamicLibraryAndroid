/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fekracomputers.islamiclibrary.biling.skulist.row;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.biling.billing.BillingProvider;
import com.fekracomputers.islamiclibrary.biling.skulist.SkusAdapter;


/**
 * Renders the UI for a particular row by delegating specifics to corresponding handlers
 */
public class UiManager implements RowViewHolder.OnButtonClickListener {
    private final RowDataProvider mRowDataProvider;
    private final UiDelegatesFactory mDelegatesFactory;

    public UiManager(RowDataProvider rowDataProvider, BillingProvider billingProvider) {
        mRowDataProvider = rowDataProvider;
        mDelegatesFactory = new UiDelegatesFactory(billingProvider);
    }

    public UiDelegatesFactory getDelegatesFactory() {
        return mDelegatesFactory;
    }

    public final RowViewHolder onCreateViewHolder(ViewGroup parent, @SkusAdapter.RowTypeDef int viewType) {
        // Selecting a flat layout for header rows
        if (viewType == SkusAdapter.TYPE_HEADER) {
            View item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sku_details_row_header, parent, false);
            return new RowViewHolder(item, this);
        } else {
            View item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sku_details_row, parent, false);
            return new RowViewHolder(item, this);
        }
    }

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        if (data != null) {
            holder.title.setText(data.getTitle());
            // For non-header rows we need to feel other data and init button's state
            if (data.getRowType() != SkusAdapter.TYPE_HEADER) {
                mDelegatesFactory.onBindViewHolder(data, holder);
            }
        }
    }

    public void onButtonClicked(int position) {
        SkuRowData data = mRowDataProvider.getData(position);
        if (data != null) {
            mDelegatesFactory.onButtonClicked(data);
        }
    }
}
