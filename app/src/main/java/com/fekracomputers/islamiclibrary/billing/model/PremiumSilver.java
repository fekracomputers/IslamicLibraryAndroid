package com.fekracomputers.islamiclibrary.billing.model;

import com.anjlab.android.iab.v3.SkuDetails;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.billing.BillingAdapterListener;

/**
 * Created by Mohammad on 22/2/2018.
 */

public class PremiumSilver extends SkuRowData {
    public final static String SKU_ID = "premium_silver";

    public PremiumSilver(BillingAdapterListener billingAdapterListener) {
        super(billingAdapterListener);
        icon = R.drawable.ic_coin_silver;
        refreshData(billingAdapterListener);

    }

    public void refreshData(BillingAdapterListener billingAdapterListener) {
        SkuDetails details = billingAdapterListener.getDetails(SKU_ID);
        if (details != null) {
            this.title = details.title;
            this.price = details.priceText;
            this.description = details.description;
            this.billingType = SkuType.INAPP;
            this.billingType = details.isSubscription ? SkuType.SUBS : SkuType.INAPP;
            this.purchased = billingAdapterListener.isPurchased(SKU_ID);
        }
    }

    @Override
    public String getSKU() {
        return SKU_ID;
    }

    @Override
    public int getTextArrayResId() {
        return R.array.silver_user_benefits;
    }


}
