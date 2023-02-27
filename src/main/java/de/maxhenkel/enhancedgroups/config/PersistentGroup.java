package de.maxhenkel.enhancedgroups.config;

import de.maxhenkel.voicechat.api.Group;

import javax.annotation.Nullable;

public class PersistentGroup {

    private final String name;
    @Nullable
    private final String password;
    private final Type type;

    public PersistentGroup(String name, @Nullable String password, Type type) {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        NORMAL(Group.Type.NORMAL),
        OPEN(Group.Type.OPEN),
        ISOLATED(Group.Type.ISOLATED);

        private Group.Type type;

        Type(Group.Type type) {
            this.type = type;
        }

        public Group.Type getType() {
            return type;
        }

        public static Type fromGroupType(Group.Type type) {
            for (Type t : values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return NORMAL;
        }
    }
}
