package com.fekracomputers.islamiclibrary.billing;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.anjlab.android.iab.v3.SkuDetails;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Mohammad on 7/2/2018.
 */

public class SkuRowData {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private String sku, title, price, description;
    private @RowTypeDef
    int type;
    private @SkuType
    String billingType;

    public SkuRowData(@Nullable SkuDetails details, @RowTypeDef int rowType,
                      @SkuType String billingType) {

        if (details != null) {
            this.sku = details.productId;
            this.title = details.title;
            this.price = details.priceText;
            this.description = details.description;
            this.type = rowType;
            this.billingType = billingType;
        }
    }

    public SkuRowData(String title) {
        this.title = title;
        this.type = TYPE_HEADER;
    }

    public String getSku() {
        return sku;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public @RowTypeDef
    int getRowType() {
        return type;
    }

    public @SkuType
    String getSkuType() {
        return billingType;
    }

    /**
     * Supported SKU types.
     */
    @StringDef({SkuType.INAPP, SkuType.SUBS})
    @Retention(SOURCE)
    public @interface SkuType {
        /**
         * A type of SKU for in-app products.
         */
        String INAPP = "inapp";
        /**
         * A type of SKU for subscriptions.
         */
        String SUBS = "subs";
    }

    /**
     * Types for adapter rows
     */
    @Retention(SOURCE)
    @IntDef({TYPE_HEADER, TYPE_NORMAL})

    public @interface RowTypeDef {
    }
}
