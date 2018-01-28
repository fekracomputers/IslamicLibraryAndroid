package com.fekracomputers.islamiclibrary.search.model.FTS;

import android.support.annotation.Nullable;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 27/2/2017.
 */

public abstract class FtsExprNode implements BaseFtsEprNode {
    protected String token;
    @Override
    public boolean isLeaf() {
        return true;
    }

    @Nullable
    @Override
    public BaseFtsEprNode leftChild() {
        return null;
    }

    @Nullable
    @Override
    public BaseFtsEprNode rightChild() {
        return null;
    }
}
