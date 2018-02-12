package com.fekracomputers.islamiclibrary.billing;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.fekracomputers.islamiclibrary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mohammad on 7/2/2018.
 */

class BillingItemsRecyclerViewAdapter extends RecyclerView.Adapter<BillingItemsRecyclerViewAdapter.ViewHolder> {

    private BillingAdapterListener billingAdapterListener;
    private String[] managedItmsId = {"item_1",
            "item_2",
            "item_3",
            "item_4",
            "item_5"
    };

    public BillingItemsRecyclerViewAdapter(BillingAdapterListener billingAdapterListener) {
        this.billingAdapterListener = billingAdapterListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_app_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setItem(managedItmsId[position]);
    }

    @Override
    public int getItemCount() {
        return managedItmsId.length;
    }

    public void notifyProductPurchased(String productId, TransactionDetails details) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.productIdTextView)
        TextView productIdTextView;
        @BindView(R.id.purchaseButton)
        Button purchaseButton;
        @BindView(R.id.consumeButton)
        Button consumeButton;
        @BindView(R.id.productDetailsButton)
        Button productDetailsButton;
        String itemId;
        private SkuRowData skuRowData;
        @BindView(R.id.statusTextView)
        TextView statusTextView;
        @BindView(R.id.priceTextView)
        TextView priceTextView;
        private boolean isPurchased;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @OnClick(R.id.consumeButton)
        void consume() {
            billingAdapterListener.consume(itemId);
        }

        @OnClick(R.id.purchaseButton)
        void purchase() {
            billingAdapterListener.purchase(itemId);

        }

        @OnClick(R.id.productDetailsButton)
        void showDetails() {
            billingAdapterListener.showDetails(itemId);
        }

        private void updateTextViews() {
            productIdTextView.setText(skuRowData.getTitle());
            priceTextView.setText(skuRowData.getPrice());
            isPurchased = billingAdapterListener.isPurchased(itemId);
            statusTextView.setText(isPurchased ? R.string.item_already_purchsed : R.string.item_not_purchased);
        }

        public void setItem(String itemId) {
            this.itemId = itemId;
            SkuDetails details = billingAdapterListener.getDetails(itemId);
            skuRowData = new SkuRowData(details, SkuRowData.TYPE_NORMAL, SkuRowData.SkuType.INAPP);
            updateTextViews();
        }
    }
}
