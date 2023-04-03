package de.maxhenkel.enhancedgroups.events;

import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;

import java.util.UUID;

public class AutoJoinGroupEvents {

    public static void onPlayerConnected(PlayerConnectedEvent event) {
        VoicechatConnection connection = event.getConnection();
        UUID persistentGroupId = EnhancedGroups.AUTO_JOIN_GROUP_STORE.getPlayerGroup(connection.getPlayer().getUuid());

        if (persistentGroupId == null) {
            return;
        }

        UUID voicechatId = EnhancedGroups.PERSISTENT_GROUP_STORE.getVoicechatId(persistentGroupId);

        Group group = event.getVoicechat().getGroup(voicechatId);

        if (group == null) {
            return;
        }

        connection.setGroup(group);
    }
}
