package com.fekracomputers.islamiclibrary.tableOFContents;

import android.content.res.Resources;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.PageInfo;

/**
 * بسم الله الرحمن الرحيم
 * A collection of static functions to be used in table of cntent fragments
 */

public class TableOfContentsUtils {

    /**
     * @param multiPartStringRes a string with two integer formatting arguments %1$d part number %2$d pageNumber
     * @param uniPartStringRes   a string with one integer formatting arguments %1$d pageNumber
     */
    public static String formatPageAndPartNumber(BookPartsInfo bookPartsInfo,
                                                 PageInfo pageInfo,
                                                 int multiPartStringRes,
                                                 int uniPartStringRes, Resources resources) {

        if (bookPartsInfo.isMultiPart()) {
            if (pageInfo.pageNumber == 0 && pageInfo.partNumber == 0) {
                return resources.getString(R.string.zero_zero_page_placeholder_multi_part);
            } else {
                return resources.getString(multiPartStringRes, pageInfo.partNumber, pageInfo.pageNumber);
            }
        } else {
            if (pageInfo.pageNumber == 0 && pageInfo.partNumber == 0) {
                return resources.getString(R.string.zero_zero_page_placeholder_single_part);
            } else {
                return resources.getString(uniPartStringRes, pageInfo.pageNumber);
            }
        }
    }
}
