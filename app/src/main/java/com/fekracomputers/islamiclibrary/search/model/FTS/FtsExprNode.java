package com.fekracomputers.islamiclibrary.search.model.FTS;

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

    @Override
    public BaseFtsEprNode leftChild() {
        return null;
    }

    @Override
    public BaseFtsEprNode rightChild() {
        return null;
    }
}
