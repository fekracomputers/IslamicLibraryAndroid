package com.fekracomputers.islamiclibrary.billing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.fekracomputers.islamiclibrary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BillingActivity extends AppCompatActivity implements BillingAdapterListener {
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArnuaw6mlbCgH3q0bhx1oj2HXlOP";
    BillingProcessor bp;
    @BindView(R.id.recyclerView)
    RecyclerView recyrecyclerView;
    BillingItemsRecyclerViewAdapter billingItemsRecyclerViewAdapter;
    private boolean readyToPurchase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.financial_aid);
        }

        if (!BillingProcessor.isIabServiceAvailable(this)) {
            showToast(R.string.iap_not_available);
        }

        bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                billingItemsRecyclerViewAdapter.notifyProductPurchased(productId, details);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                Timber.e(error);
                // showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;
                billingItemsRecyclerViewAdapter.setReadyToPurchase(true);
                billingItemsRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                billingItemsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        billingItemsRecyclerViewAdapter = new BillingItemsRecyclerViewAdapter(this);
        recyrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyrecyclerView.setHasFixedSize(true);
        recyrecyclerView.setAdapter(billingItemsRecyclerViewAdapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showToast(@StringRes int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        billingItemsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isPurchased(String productId) {
        return bp.isPurchased(productId);
    }

    @Override
    public boolean consume(String itemId) {
        return bp.consumePurchase(itemId);
    }

    @Override
    public SkuDetails getDetails(String itemId) {
        return bp.getPurchaseListingDetails(itemId);
    }

    @Override
    public boolean purchase(String itemId) {
        return bp.purchase(this, itemId);
    }

    @Override
    public void showDetails(String itemId) {
        InAppDetailsDialogFragment
                .newInstance(itemId, bp.getPurchaseListingDetails(itemId))
                .show(getSupportFragmentManager(), itemId);
    }
}
