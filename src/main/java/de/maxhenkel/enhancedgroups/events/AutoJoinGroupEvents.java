package de.maxhenkel.enhancedgroups.events;

import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;

import java.util.UUID;

public class AutoJoinGroupEvents {

    public static void onPlayerConnected(PlayerConnectedEvent event) {
        VoicechatConnection connection = event.getConnection();

        UUID playerGroupId = EnhancedGroups.AUTO_JOIN_GROUP_STORE.getPlayerGroup(connection.getPlayer().getUuid());
        UUID globalGroupId = EnhancedGroups.AUTO_JOIN_GROUP_STORE.getGlobalGroup();
        boolean globalGroupForced = EnhancedGroups.AUTO_JOIN_GROUP_STORE.getGlobalGroupForced();
        UUID selectedGroupId;

        if (!globalGroupForced && playerGroupId != null) {
            selectedGroupId = playerGroupId;
        } else if (globalGroupId != null) {
            selectedGroupId = globalGroupId;
        } else {
            return;
        }

        UUID voicechatId = EnhancedGroups.PERSISTENT_GROUP_STORE.getVoicechatId(selectedGroupId);

        Group group = event.getVoicechat().getGroup(voicechatId);
        if (group == null) {
            return;
        }

        connection.setGroup(group);
    }
}
