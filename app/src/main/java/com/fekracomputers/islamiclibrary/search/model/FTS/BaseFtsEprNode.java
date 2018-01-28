package com.fekracomputers.islamiclibrary.search.model.FTS;


import android.support.annotation.Nullable;

/**
 * Base interface for FTS Query expression Tree
 */
public interface BaseFtsEprNode {
    boolean isLeaf(); // All subclasses must implement

    String value();

    @Nullable
    BaseFtsEprNode leftChild();

    @Nullable
    BaseFtsEprNode rightChild();

}