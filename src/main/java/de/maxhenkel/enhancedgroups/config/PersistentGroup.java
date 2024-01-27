package de.maxhenkel.enhancedgroups.config;

import de.maxhenkel.voicechat.api.Group;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class PersistentGroup {

    private final String name;
    @Nullable
    private final String password;
    private final Type type;
    private final boolean hidden;
    @Nullable
    private UUID id;

    public PersistentGroup(String name, @Nullable String password, Type type, boolean hidden, @Nullable UUID id) {
        this.name = name;
        this.password = password;
        this.type = type;
        this.hidden = hidden;
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
    }

    public PersistentGroup(String name, @Nullable String password, Type type, boolean hidden) {
        this(name, password, type, hidden, null);
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

    public boolean isHidden() {
        return hidden;
    }

    public UUID getId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        return id;
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
