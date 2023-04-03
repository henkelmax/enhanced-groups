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
    private Map<UUID, UUID> playerGroups;

    public AutoJoinGroupStore(File file) {
        this.file = file;
        this.gson = new GsonBuilder().create();
        this.playerGroups = new HashMap<>();
        load();
    }

    public void load() {
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Type playerGroupsType = new TypeToken<Map<UUID, UUID>>() {
            }.getType();
            playerGroups = gson.fromJson(reader, playerGroupsType);
        } catch (Exception e) {
            EnhancedGroups.LOGGER.error("Failed to load auto join groups", e);
        }
        if (playerGroups == null) {
            playerGroups = new HashMap<>();
        }
    }

    public void save() {
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(playerGroups, writer);
        } catch (Exception e) {
            EnhancedGroups.LOGGER.error("Failed to save auto join groups", e);
        }
    }

    @Nullable
    public UUID getPlayerGroup(UUID playerUuid) {
        return playerGroups.get(playerUuid);
    }

    public void setPlayerGroup(UUID playerUuid, UUID groupId) {
        playerGroups.put(playerUuid, groupId);
        save();
    }

    public void removePlayerGroup(UUID playerUuid) {
        playerGroups.remove(playerUuid);
        save();
    }

}
