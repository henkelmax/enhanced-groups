package de.maxhenkel.enhancedgroups.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.maxhenkel.enhancedgroups.EnhancedGroups;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutoJoinGroupStore {
    private final File file;
    private final Gson gson;
    private StoreContent content;
    public AutoJoinGroupStore(File file) {
        this.file = file;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.content = new StoreContent();
        load();
    }

    public void load() {
        if (!file.exists()) {
            EnhancedGroups.LOGGER.error("Failed to load auto join groups");
            return;
        }
        try (Reader reader = new FileReader(file)) {
            content = gson.fromJson(reader, StoreContent.class);
        } catch (Exception e) {
            EnhancedGroups.LOGGER.error("Failed to parse auto join groups", e);
        }
        if (content == null) {
            content = new StoreContent();
        }
    }

    public void save() {
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(content, writer);
        } catch (Exception e) {
            EnhancedGroups.LOGGER.error("Failed to save auto join groups", e);
        }
    }

    @Nullable
    public UUID getPlayerGroup(UUID playerUuid) {
        return content.playerGroups.get(playerUuid);
    }

    public void setPlayerGroup(UUID playerUuid, UUID groupId) {
        content.playerGroups.put(playerUuid, groupId);
        save();
    }

    public void removePlayerGroup(UUID playerUuid) {
        content.playerGroups.remove(playerUuid);
        save();
    }

    @Nullable
    public UUID getGlobalGroup() {
        return content.globalGroup;
    }

    public void setGlobalGroup(UUID groupId) {
        content.globalGroup = groupId;
        save();
    }

    public void removeGlobalGroup() {
        content.globalGroup = null;
        save();
    }

    public boolean getGlobalGroupForced() {
        return content.globalGroupForced;
    }

    public void setGlobalGroupForced(boolean status) {
        content.globalGroupForced = status;
        save();
    }

    private static class StoreContent {
        public Map<UUID, UUID> playerGroups;
        public UUID globalGroup;
        public boolean globalGroupForced;

        StoreContent() {
            playerGroups = new HashMap<>();
            globalGroup = null;
            globalGroupForced = false;
        }
    }
}
