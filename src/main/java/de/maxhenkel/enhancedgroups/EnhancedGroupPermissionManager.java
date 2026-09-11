package de.maxhenkel.enhancedgroups;

import de.maxhenkel.admiral.permissions.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.Permissions;

import java.util.Map;

public class EnhancedGroupPermissionManager implements PermissionManager<CommandSourceStack> {

    public static final String AUTO_JOIN_GROUP_PERMISSION_STRING = "enhancedgroups:autojoingroup";
    public static final String AUTO_JOIN_GROUP_GLOBAL_PERMISSION_STRING = "enhancedgroups:autojoingroup.global";
    public static final String FORCE_JOIN_GROUP_PERMISSION_STRING = "enhancedgroups:forcejoingroup";
    public static final String INSTANT_GROUP_PERMISSION_STRING = "enhancedgroups:instantgroup";
    public static final String PERSISTENT_GROUP_PERMISSION_STRING = "enhancedgroups:persistentgroup";

    private final Map<String, Permission> permissions;

    public EnhancedGroupPermissionManager() {
        Permission autoJoinGroupPermission = new Permission(AUTO_JOIN_GROUP_PERMISSION_STRING, EnhancedGroups.CONFIG.autoJoinGroupCommandPermissionType.get());
        Permission autoJoinGroupGlobalPermission = new Permission(AUTO_JOIN_GROUP_GLOBAL_PERMISSION_STRING, EnhancedGroups.CONFIG.autoJoinGroupGlobalCommandPermissionType.get());
        Permission forceJoinGroupPermission = new Permission(FORCE_JOIN_GROUP_PERMISSION_STRING, EnhancedGroups.CONFIG.forceJoinGroupCommandPermissionType.get());
        Permission instantGroupPermission = new Permission(INSTANT_GROUP_PERMISSION_STRING, EnhancedGroups.CONFIG.instantGroupCommandPermissionType.get());
        Permission persistentGroupPermission = new Permission(PERSISTENT_GROUP_PERMISSION_STRING, EnhancedGroups.CONFIG.persistentGroupCommandPermissionType.get());

        permissions = Map.of(
                autoJoinGroupPermission.permissionString(), autoJoinGroupPermission,
                autoJoinGroupGlobalPermission.permissionString(), autoJoinGroupGlobalPermission,
                forceJoinGroupPermission.permissionString(), forceJoinGroupPermission,
                instantGroupPermission.permissionString(), instantGroupPermission,
                persistentGroupPermission.permissionString(), persistentGroupPermission
        );
    }

    @Override
    public boolean hasPermission(CommandSourceStack stack, String permissionString) {
        Permission permission = permissions.get(permissionString);
        if (permission == null) {
            return false;
        }
        if (stack.isPlayer()) {
            return permission.hasPermission(stack.getPlayer());
        }
        return stack.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
    }

    private static class Permission {

        private final Identifier permission;
        private final PermissionType type;

        public Permission(String permission, PermissionType type) {
            this.permission = Identifier.parse(permission);
            this.type = type;
        }

        public boolean hasPermission(ServerPlayer player) {
            return switch (type) {
                case EVERYONE -> player.checkPermission(permission, PermissionLevel.ALL);
                case NOONE -> player.checkPermission(permission, false);
                case OPS -> player.checkPermission(permission, PermissionLevel.ADMINS);
            };
        }

        public String permissionString() {
            return permission.toString();
        }
    }

    public enum PermissionType {
        EVERYONE, NOONE, OPS
    }

}
