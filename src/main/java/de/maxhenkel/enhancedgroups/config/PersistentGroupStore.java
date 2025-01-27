package de.maxhenkel.enhancedgroups.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.maxhenkel.enhancedgroups.EnhancedGroups;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PersistentGroupStore {

    private final File file;
    private final Gson gson;
    private List<PersistentGroup> groups;
    private final Map<UUID, PersistentGroup> groupCache;

    public PersistentGroupStore(File file) {
        this.file = file;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.groups = new ArrayList<>();
        this.groupCache = new HashMap<>();
        load();
    }

    public void load() {
        if (!file.exists()) {
            EnhancedGroups.LOGGER.error("Failed to load persistent groups");
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Type groupListType = new TypeToken<ArrayList<PersistentGroup>>() {
            }.getType();
            groups = gson.fromJson(reader, groupListType);
        } catch (Exception e) {
            EnhancedGroups.LOGGER.error("Failed to parse persistent groups", e);
        }
        if (groups == null) {
            groups = new ArrayList<>();
        }

        for (PersistentGroup group : groups) {
            group.getId(); // This generates IDs for all groups that don't have one
        }
        save();
    }

    public void save() {
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(groups, writer);
        } catch (Exception e) {
            EnhancedGroups.LOGGER.error("Failed to save persistent groups", e);
        }
    }

    @Nullable
    public PersistentGroup getCached(UUID uuid) {
        return groupCache.get(uuid);
    }

    public void addCached(UUID uuid, PersistentGroup group) {
        groupCache.put(uuid, group);
    }

    public void clearCache() {
        groupCache.clear();
    }

    @Nullable
    public PersistentGroup getGroup(String name) {
        return groups.stream().filter(g -> g.getName().trim().equals(name.trim())).findFirst().orElse(null);
    }

    @Nullable
    public PersistentGroup getGroup(UUID id) {
        return groups.stream().filter(g -> g.getId().equals(id)).findFirst().orElse(null);
    }

    @Nullable
    public UUID getVoicechatId(UUID persistentGroupId) {
        return groupCache.entrySet().stream().filter(e -> e.getValue().getId().equals(persistentGroupId)).findFirst().map(Map.Entry::getKey).orElse(null);
    }

    public List<PersistentGroup> getGroups() {
        return groups;
    }

    public void addGroup(PersistentGroup group) {
        groups.add(group);
        save();
    }

    public void removeGroup(PersistentGroup group) {
        groups.remove(group);
        save();
    }

}
