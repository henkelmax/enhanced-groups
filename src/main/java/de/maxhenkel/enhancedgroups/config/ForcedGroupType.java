package de.maxhenkel.enhancedgroups.config;

import de.maxhenkel.voicechat.api.Group;

import javax.annotation.Nullable;

public enum ForcedGroupType {
    OFF(null),
    NORMAL(Group.Type.NORMAL),
    OPEN(Group.Type.OPEN),
    ISOLATED(Group.Type.ISOLATED);

    @Nullable
    private Group.Type type;

    ForcedGroupType(Group.Type type) {
        this.type = type;
    }

    public boolean isOff() {
        return this == OFF;
    }

    @Nullable
    public Group.Type getType() {
        return type;
    }

    public static ForcedGroupType fromGroupType(Group.Type type) {
        for (ForcedGroupType t : values()) {
            if (t.getType() == type) {
                return t;
            }
        }
        return NORMAL;
    }
}
