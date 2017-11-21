package com.fekracomputers.islamiclibrary.userNotes.adapters;

import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.Group;
import com.xwray.groupie.UpdatingGroup;

import java.util.List;

/**
 * Created by Mohammad on 19/11/2017.
 */

public class UpdatableExpandingGroup extends ExpandableGroup {
    private UpdatingGroup updatingGroup;

    public UpdatableExpandingGroup(Group expandableItem) {
        super(expandableItem);
        updatingGroup=new UpdatingGroup();
    }

    public void update(List<UserNoteItem> list) {
        updatingGroup.update(list);

    }

}
