package com.fekracomputers.islamiclibrary.model;

import android.content.ContentValues;

import com.fekracomputers.islamiclibrary.databases.UserDataDBContract;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Mohammad Yahia on 21/12/2016.
 */
public class HighlightTest {
    String serialized1 = "type:textContent|750$757$2$highlight3$$dcdcdc |757$763$4$highlight1$$cdcdc ";
    PageInfo pagId=new PageInfo(10,0,0);
    @Test
    public void deserializeGeneric() throws Exception {
        ArrayList<ContentValues> highlightsReturn = Highlight.deserializeToContentValues(serialized1,pagId,0);

        ArrayList<ContentValues> highlightsGiven = new ArrayList<>();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID, pagId.pageId);
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID,2);
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_CLASS_NAME,"highlight3");
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_CONTAINER_ELEMENT_ID, 0);
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_TEXT, "dcdcdc ");
        highlightsGiven.add(contentValues);

        contentValues = new ContentValues();
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID, pagId.pageId);
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID,4);
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_CLASS_NAME,"highlight1");
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_CONTAINER_ELEMENT_ID, 0);
        contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_TEXT, "cdcdc ");
        highlightsGiven.add(contentValues);




        assertEquals(highlightsGiven.size(), highlightsReturn.size());


    }

    @Test
    public void deserialize() throws Exception {
              /*
                var parts = [
                characterRange.start,//0
                        characterRange.end,//1
                        highlight.id,//2
                        highlight.classApplier.className,//3
                        highlight.containerElementId//4
                        Text//5
                ];
    */

//    Highlight(String text, int id, String className, int containerElementId, PageInfo pageInfo, String noteText, int bookId) {

        ArrayList<Highlight> highlightsRerurn = Highlight.deserialize(serialized1,pagId,0);
        ArrayList<Highlight> highlightsGiven = new ArrayList<>();
        highlightsGiven.add(new Highlight("dcdcdc ", 2, "highlight3", 0, pagId, "note",0));
        highlightsGiven.add(new Highlight("cdcdc ", 4, "highlight1", 0, pagId, "note",0));
        assertEquals(highlightsRerurn.size(), highlightsRerurn.size());
        for (int i = 0; i < highlightsRerurn.size(); i++) {
            Highlight expectedHighlight = highlightsGiven.get(i);
            Highlight HighlightActual = highlightsGiven.get(i);

            assertEquals(expectedHighlight.containerElementId, HighlightActual.containerElementId);
            assertEquals(expectedHighlight.id, HighlightActual.id);
            assertEquals(expectedHighlight.className, HighlightActual.className);
            assertEquals(expectedHighlight.text, HighlightActual.text);
        }


    }

}