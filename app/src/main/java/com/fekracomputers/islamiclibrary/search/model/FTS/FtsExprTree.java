package com.fekracomputers.islamiclibrary.search.model.FTS;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 27/2/2017.
 */

public class FtsExprTree {
    private FtsOperatorNode root;
    private StringBuilder stringRepresentation = new StringBuilder();

    public FtsExprTree(FtsOperatorNode root) {
        this.root = root;
    }


    private void traverse(BaseFtsEprNode node) {
        if (node != null) {
            traverse(node.leftChild());
            stringRepresentation.append(node.value());
            traverse(node.rightChild());
        }
    }

    @Override
    public String toString() {
        traverse(root);
        return stringRepresentation.toString();

    }
}
