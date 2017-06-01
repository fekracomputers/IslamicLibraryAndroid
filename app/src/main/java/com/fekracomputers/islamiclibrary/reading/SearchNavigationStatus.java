package com.fekracomputers.islamiclibrary.reading;


public class SearchNavigationStatus {
    public final boolean currentSpreadContainsMatch;
    public final boolean hasNext;
    public final boolean hasPrevious;
    public final int numMatches;
    public final int numMatchesBeforeSpread;

    public SearchNavigationStatus(  boolean hasPrevious, boolean hasNext, int numMatchesBeforeSpread, int numMatches, boolean currentSpreadContainsMatch) {
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.numMatchesBeforeSpread = numMatchesBeforeSpread;
        this.numMatches = numMatches;
        this.currentSpreadContainsMatch = currentSpreadContainsMatch;
    }
}
