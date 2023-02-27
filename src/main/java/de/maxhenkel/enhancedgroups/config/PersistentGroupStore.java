package de.maxhenkel.enhancedgroups.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.maxhenkel.voicechat.Voicechat;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PersistentGroupStore {

    private final File file;
    private final Gson gson;
    private List<PersistentGroup> groups;
    private Map<UUID, PersistentGroup> groupCache;

    public PersistentGroupStore(File file) {
        this.file = file;
        this.gson = new GsonBuilder().create();
        this.groups = new ArrayList<>();
        this.groupCache = new HashMap<>();
        load();
    }

    public void load() {
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Type groupListType = new TypeToken<ArrayList<PersistentGroup>>() {
            }.getType();
            groups = gson.fromJson(reader, groupListType);
        } catch (Exception e) {
            Voicechat.LOGGER.error("Failed to load persistent groups", e);
        }
        if (groups == null) {
            groups = new ArrayList<>();
        }
    }

    public void save() {
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(groups, writer);
        } catch (Exception e) {
            Voicechat.LOGGER.error("Failed to save username cache", e);
        }
    }

    @Nullable
    public PersistentGroup getCached(UUID uuid) {
        return groupCache.get(uuid);
    }

    public void addCached(UUID uuid, PersistentGroup group) {
        groupCache.put(uuid, group);
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
