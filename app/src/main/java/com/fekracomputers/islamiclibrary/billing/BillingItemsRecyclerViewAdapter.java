package com.fekracomputers.islamiclibrary.billing;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anjlab.android.iab.v3.TransactionDetails;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.billing.model.PremiumDiamond;
import com.fekracomputers.islamiclibrary.billing.model.PremiumGold;
import com.fekracomputers.islamiclibrary.billing.model.PremiumSilver;
import com.fekracomputers.islamiclibrary.billing.model.SkuRowData;
import com.fekracomputers.islamiclibrary.widget.StaticList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mohammad on 7/2/2018.
 */

class BillingItemsRecyclerViewAdapter extends RecyclerView.Adapter<BillingItemsRecyclerViewAdapter.ViewHolder> {

    private BillingAdapterListener billingAdapterListener;
    private ArrayList<SkuRowData> items;
    private boolean readyToPurchase;

    public BillingItemsRecyclerViewAdapter(BillingAdapterListener billingAdapterListener) {
        this.billingAdapterListener = billingAdapterListener;
        items = new ArrayList<>();
        items.add(new PremiumSilver(billingAdapterListener));
        items.add(new PremiumGold(billingAdapterListener));
        items.add(new PremiumDiamond(billingAdapterListener));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_app_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void notifyProductPurchased(String productId, TransactionDetails details) {

    }

    public void setReadyToPurchase(boolean readyToPurchase) {
        this.readyToPurchase = readyToPurchase;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sku_icon)
        ImageView IconImageView;

        @BindView(R.id.benefits_list)
        StaticList benefitsListView;

        @BindView(R.id.product_titlte_textView)
        TextView productTitleTextView;

        @BindView(R.id.purchaseButton)
        Button purchaseButton;

        @BindView(R.id.priceTextView)
        TextView priceTextView;
        private SkuRowData skuRowData;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.purchaseButton)
        void purchase() {
            billingAdapterListener.purchase(skuRowData.getSKU());

        }


        public void setItem(SkuRowData item) {
            skuRowData = item;
            skuRowData.refreshData(billingAdapterListener);
            productTitleTextView.setText(skuRowData.getTitle());
            priceTextView.setText(skuRowData.getPrice());
            purchaseButton.setEnabled(readyToPurchase);
            if (skuRowData.isPurchased()) {
                purchaseButton.setEnabled(false);
                purchaseButton.setText(R.string.item_already_purchsed);
            }
            if (skuRowData.getIcon() != 0) IconImageView.setImageResource(skuRowData.getIcon());
            CharSequence[] benefitsList = itemView.getResources().getTextArray(skuRowData.getTextArrayResId());
            benefitsListView.setAdapter(itemView.getContext(), R.layout.item_sku_benefit, R.id.benefit_text, benefitsList);
        }
    }
}
