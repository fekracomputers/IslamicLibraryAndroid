package com.fekracomputers.islamiclibrary.homeScreen;

/**
 * Created by Mohammad on 22/11/2017.
 */

public interface HomeScreenCallBack {
    void notifyBookCollectionCahnged(int collectionId);
    void notifyBookCollectionAdded(int collectionId);
    void notifyBookCollectionRemoved(int collectionId);
}
