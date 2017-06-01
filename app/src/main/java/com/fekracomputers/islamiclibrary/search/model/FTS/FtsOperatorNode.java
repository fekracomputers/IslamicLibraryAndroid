package com.fekracomputers.islamiclibrary.search.model.FTS;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 27/2/2017.
 */


class FtsOperatorNode implements BaseFtsEprNode {
    public  enum OPERATOR_TYPE {AND,OR}

    private BaseFtsEprNode left; // Left child
    private BaseFtsEprNode right; // Right child
    private String operator; // Operator value

    public FtsOperatorNode(OPERATOR_TYPE op,
                           BaseFtsEprNode l, BaseFtsEprNode r) {
        operator = op== FtsOperatorNode.OPERATOR_TYPE.AND?" ":" OR ";
        left = l;
        right = r;
    }

    public boolean isLeaf() {
        return false;
    }

    public BaseFtsEprNode leftChild() {
        return left;
    }

    public BaseFtsEprNode rightChild() {
        return right;
    }

    public String value() {
        return operator;
    }


}
