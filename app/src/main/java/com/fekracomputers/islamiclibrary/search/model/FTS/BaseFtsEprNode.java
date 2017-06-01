package com.fekracomputers.islamiclibrary.search.model.FTS;


/**
 * Base interface for FTS Query expression Tree
 */
public interface BaseFtsEprNode {
    boolean isLeaf(); // All subclasses must implement

    String value();

    BaseFtsEprNode leftChild();

    BaseFtsEprNode rightChild();

}