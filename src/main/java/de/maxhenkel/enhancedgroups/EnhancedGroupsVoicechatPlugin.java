package de.maxhenkel.enhancedgroups;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

import javax.annotation.Nullable;

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
        registration.registerEvent(VoicechatServerStartedEvent.class, evt -> SERVER_API = evt.getVoicechat());
    }

}
