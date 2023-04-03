package de.maxhenkel.enhancedgroups;

import de.maxhenkel.enhancedgroups.config.PersistentGroup;
import de.maxhenkel.enhancedgroups.events.AutoJoinGroupEvents;
import de.maxhenkel.enhancedgroups.events.ForceGroupTypeEvents;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.CreateGroupEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

import javax.annotation.Nullable;
import java.util.List;

public class EnhancedGroupsVoicechatPlugin implements VoicechatPlugin {

    @Nullable
    public static VoicechatServerApi SERVER_API;

    @Override
    public String getPluginId() {
        return EnhancedGroups.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {

    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(PlayerConnectedEvent.class, AutoJoinGroupEvents::onPlayerConnected);
        registration.registerEvent(CreateGroupEvent.class, ForceGroupTypeEvents::onCreateGroup);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        SERVER_API = event.getVoicechat();

        List<PersistentGroup> groups = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroups();
        for (PersistentGroup group : groups) {
            Group vcGroup = SERVER_API.groupBuilder()
                    .setPersistent(true)
                    .setName(group.getName())
                    .setPassword(group.getPassword())
                    .setType(group.getType().getType())
                    .build();
            EnhancedGroups.PERSISTENT_GROUP_STORE.addCached(vcGroup.getId(), group);
        }

        EnhancedGroups.LOGGER.info("Added {} persistent groups", groups.size());
    }

}
