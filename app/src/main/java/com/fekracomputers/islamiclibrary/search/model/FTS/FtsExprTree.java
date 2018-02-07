package com.fekracomputers.islamiclibrary.search.model.FTS;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 27/2/2017.
 */

public class FtsExprTree {
    private FtsOperatorNode root;
    @NonNull
    private StringBuilder stringRepresentation = new StringBuilder();

    public FtsExprTree(FtsOperatorNode root) {
        this.root = root;
    }


    private void traverse(@Nullable BaseFtsEprNode node) {
        if (node != null) {
            traverse(node.leftChild());
            stringRepresentation.append(node.value());
            traverse(node.rightChild());
        }
    }

    @NonNull
    @Override
    public String toString() {
        traverse(root);
        return stringRepresentation.toString();

    }
}
