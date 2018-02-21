package com.fekracomputers.islamiclibrary.billing;

import com.anjlab.android.iab.v3.SkuDetails;

/**
 * Created by Mohammad on 7/2/2018.
 */

interface BillingAdapterListener {
    boolean isPurchased(String productId);

    boolean consume(String itemId);

    SkuDetails getDetails(String itemId);

    boolean purchase(String itemId);

    void showDetails(String itemId);
}
