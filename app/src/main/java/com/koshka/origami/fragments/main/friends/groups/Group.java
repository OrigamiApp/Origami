package com.koshka.origami.fragments.main.friends.groups;

import android.graphics.drawable.Drawable;

/**
 * Created by qm0937 on 10/2/16.
 */

public class Group {
    private String groupName;
    private Drawable groupPic;

    public Group(String groupName, Drawable groupPic) {
        this.groupName = groupName;
        this.groupPic = groupPic;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Drawable getGroupPic() {
        return groupPic;
    }

    public void setGroupPic(Drawable groupPic) {
        this.groupPic = groupPic;
    }
}
