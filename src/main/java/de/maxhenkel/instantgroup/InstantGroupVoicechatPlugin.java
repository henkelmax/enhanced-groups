package de.maxhenkel.instantgroup;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

import javax.annotation.Nullable;

public class InstantGroupVoicechatPlugin implements VoicechatPlugin {

    @Nullable
    public static VoicechatServerApi SERVER_API;

    @Override
    public String getPluginId() {
        return InstantGroup.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {

    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, evt -> SERVER_API = evt.getVoicechat());
    }

}
