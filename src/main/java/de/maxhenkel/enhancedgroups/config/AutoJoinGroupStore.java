package de.maxhenkel.enhancedgroups.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.maxhenkel.enhancedgroups.EnhancedGroups;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
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

        migrate(file);
        try (Reader reader = new FileReader(file)) {

            Type contentType = new TypeToken<StoreContent>() {
            }.getType();
            content = gson.fromJson(reader, contentType);
        } catch (Exception e) {
            EnhancedGroups.LOGGER.error("Failed to parse auto join groups", e);
        }
        if (content == null) {
            content = new StoreContent();
        }
    }

    private void migrate(File file) {
        try (Reader reader = new FileReader(file)) {
            Type playerGroupsType = new TypeToken<Map<UUID, UUID>>() {
            }.getType();
            Map<UUID, UUID> playerGroups = gson.fromJson(reader, playerGroupsType);

            EnhancedGroups.LOGGER.info("Migrated config");

            content = new StoreContent();
            content.playerGroups = playerGroups;
            save();
        } catch (Exception e) {
            // No migration needed (GSON failed to parse old format)
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
