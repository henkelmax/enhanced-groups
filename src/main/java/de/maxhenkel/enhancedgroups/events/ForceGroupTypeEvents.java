package de.maxhenkel.enhancedgroups.events;

import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.config.ForcedGroupType;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.events.CreateGroupEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class ForceGroupTypeEvents {

    public static void onCreateGroup(CreateGroupEvent event) {
        VoicechatConnection connection = event.getConnection();
        if (connection == null) {
            return;
        }
        ForcedGroupType forcedGroupType = EnhancedGroups.CONFIG.forceGroupType.get();
        if (forcedGroupType.isOff()) {
            return;
        }
        Group group = event.getGroup();
        if (group == null) {
            return;
        }
        if (group.getType().equals(forcedGroupType.getType())) {
            return;
        }

        event.cancel();

        Group forcedGroup = event
                .getVoicechat()
                .groupBuilder()
                .setType(forcedGroupType.getType())
                .setPassword(getPassword(group))
                .setName(group.getName())
                .setPersistent(group.isPersistent())
                .build();

        connection.setGroup(forcedGroup);
    }

    @Nullable
    private static String getPassword(Group group) {
        try {
            Field groupField = group.getClass().getDeclaredField("group");
            groupField.setAccessible(true);
            Object groupObject = groupField.get(group);
            Field passwordField = groupObject.getClass().getDeclaredField("password");
            passwordField.setAccessible(true);
            return (String) passwordField.get(groupObject);
        } catch (Throwable e) {
            EnhancedGroups.LOGGER.warn("Could not get password of group", e);
            return null;
        }
    }

}
