package com.fekracomputers.islamiclibrary.billing.model;

import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringDef;

import com.fekracomputers.islamiclibrary.billing.BillingAdapterListener;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Mohammad on 7/2/2018.
 */

public abstract class SkuRowData {


    protected BillingAdapterListener billingAdapterListener;
    @DrawableRes
    protected int icon = 0;
    protected String title, price, description;
    protected @SkuType
    String billingType;
    boolean purchased;

    public boolean isPurchased() {
        return purchased;
    }

    public SkuRowData(BillingAdapterListener billingAdapterListener) {
        this.billingAdapterListener = billingAdapterListener;
    }


    public int getIcon() {
        return icon;
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


    public @SkuType
    String getSkuType() {
        return billingType;
    }

    public abstract String getSKU();

    @ArrayRes
    public abstract int getTextArrayResId();

    public abstract void refreshData(BillingAdapterListener billingAdapterListener);

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


}
