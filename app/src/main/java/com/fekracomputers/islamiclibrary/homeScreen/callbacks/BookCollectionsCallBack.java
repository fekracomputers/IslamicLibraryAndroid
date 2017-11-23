package com.fekracomputers.islamiclibrary.homeScreen.callbacks;

/**
 * Created by Mohammad on 22/11/2017.
 */

public interface BookCollectionsCallBack {
    void onBookCollectionCahnged(int collectionId);
    void onBookCollectionAdded(int collectionId);
    void onBookCollectionRemoved(int collectionsId);
    void onBookCollectionRenamed(int collectionId, String newName);
    void onBookCollectionMoved(int collectionsId, int oldPosition, int newPosition);

}
