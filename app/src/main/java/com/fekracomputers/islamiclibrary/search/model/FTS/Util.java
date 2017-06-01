package com.fekracomputers.islamiclibrary.search.model.FTS;

import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.utility.ArabicUtilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 22/5/2017.
 */

public class Util {
    private static final Pattern wordPattern = Pattern.compile("([^\\s]+)(\\s|$)");
    @NonNull
    public static String getSearchPrefixQueryString(String query) {

        Matcher m = wordPattern.matcher(ArabicUtilities.cleanTextForSearchingWthStingBuilder(query));
        return m.replaceAll("$1* ");
    }
}
