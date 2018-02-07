package com.fekracomputers.islamiclibrary.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;

/**
 * Created by Mohammad Yahia on 28/12/2016.
 */
public class PageCitation {
    //TODO a lot of improvement to citation types and formats
    public String bookName;
    public String authorName;
    public PageInfo pageInfo;
    private BookPartsInfo bookPartsInfo;
    private Resources resources;

    public PageCitation(String bookName, String authorName, PageInfo pageInfo, BookPartsInfo bookPartsInfo) {

        this.bookName = bookName;
        this.authorName = authorName;
        this.pageInfo = pageInfo;
        this.bookPartsInfo = bookPartsInfo;
    }

    private String getCitationWithoutLineBreaks() {
        String partAndPage = TableOfContentsUtils.formatPageAndPartNumber(bookPartsInfo,
                pageInfo,
                R.string.page_slash_part,
                R.string.page_number_with_label,
                resources);
        return resources.getString(R.string.citation_bottom_with_square_brackets, bookName, authorName, partAndPage);
    }

    @NonNull
    public String getCitationString() {
        return "\n" + getCitationWithoutLineBreaks();
    }

    @NonNull
    public String getCitationHtml() {
        return "<p>" + getCitationWithoutLineBreaks() + "</p>";
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }
}
