package com.fekracomputers.islamiclibrary.billing;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.anjlab.android.iab.v3.SkuDetails;


public class InAppDetailsDialogFragment extends DialogFragment {
    private static final String ARG_Id = "itemId";
    private static final String ARG_SKU_DETAILS = "skuDetails";


    private String itemId;
    private SkuDetails skuDetails;

    public InAppDetailsDialogFragment() {
        // Required empty public constructor
    }

    public static InAppDetailsDialogFragment newInstance(String itemId, SkuDetails detlails) {
        InAppDetailsDialogFragment fragment = new InAppDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_Id, itemId);
        args.putParcelable(ARG_SKU_DETAILS, detlails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getString(ARG_Id);
            skuDetails = getArguments().getParcelable(ARG_SKU_DETAILS);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(skuDetails.title);
        alertDialogBuilder.setMessage(skuDetails.description);
        return alertDialogBuilder.create();
    }
}
