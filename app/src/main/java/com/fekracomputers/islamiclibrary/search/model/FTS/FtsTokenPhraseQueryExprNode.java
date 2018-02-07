package com.fekracomputers.islamiclibrary.search.model.FTS;

import android.support.annotation.NonNull;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 27/2/2017.
 */

public class FtsTokenPhraseQueryExprNode extends FtsExprNode {
    @NonNull
    @Override
    public String value() {
        return "\""+token+"\"";
    }
}
