package de.maxhenkel.enhancedgroups;

import de.maxhenkel.admiral.permissions.PermissionManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class EnhancedGroupPermissionManager implements PermissionManager<CommandSourceStack> {

    private final Permission AUTO_JOIN_GROUP_PERMISSION;
    private final Permission AUTO_JOIN_GROUP_GLOBAL_PERMISSION;
    private final Permission FORCE_JOIN_GROUP_PERMISSION;
    private final Permission INSTANT_GROUP_PERMISSION;
    private final Permission PERSISTENT_GROUP_PERMISSION;

    private final List<Permission> PERMISSIONS;

    public EnhancedGroupPermissionManager() {
        AUTO_JOIN_GROUP_PERMISSION = new Permission("enhancedgroups.autojoingroup", EnhancedGroups.CONFIG.autoJoinGroupCommandPermissionType.get());
        AUTO_JOIN_GROUP_GLOBAL_PERMISSION = new Permission("enhancedgroups.autojoingroup.global", EnhancedGroups.CONFIG.autoJoinGroupGlobalCommandPermissionType.get());
        FORCE_JOIN_GROUP_PERMISSION = new Permission("enhancedgroups.forcejoingroup", EnhancedGroups.CONFIG.forceJoinGroupCommandPermissionType.get());
        INSTANT_GROUP_PERMISSION = new Permission("enhancedgroups.instantgroup", EnhancedGroups.CONFIG.instantGroupCommandPermissionType.get());
        PERSISTENT_GROUP_PERMISSION = new Permission("enhancedgroups.persistentgroup", EnhancedGroups.CONFIG.persistentGroupCommandPermissionType.get());

        PERMISSIONS = List.of(
                AUTO_JOIN_GROUP_PERMISSION,
                AUTO_JOIN_GROUP_GLOBAL_PERMISSION,
                FORCE_JOIN_GROUP_PERMISSION,
                INSTANT_GROUP_PERMISSION,
                PERSISTENT_GROUP_PERMISSION
        );
    }

    @Override
    public boolean hasPermission(CommandSourceStack stack, String permission) {
        for (Permission p : PERMISSIONS) {
            if (!p.permission.equals(permission)) {
                continue;
            }
            if (stack.isPlayer()) {
                return p.hasPermission(stack.getPlayer());
            }
            if (p.getType().equals(PermissionType.OPS)) {
                return stack.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR);
            } else {
                return p.hasPermission(null);
            }
        }
        return false;
    }

    private static Boolean loaded;

    private static boolean isFabricPermissionsAPILoaded() {
        if (loaded == null) {
            loaded = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");
            if (loaded) {
                EnhancedGroups.LOGGER.info("Using Fabric Permissions API");
            }
        }
        return loaded;
    }

    private static class Permission {
        private final String permission;
        private final PermissionType type;

        public Permission(String permission, PermissionType type) {
            this.permission = permission;
            this.type = type;
        }

        public boolean hasPermission(@Nullable ServerPlayer player) {
            if (isFabricPermissionsAPILoaded()) {
                return checkFabricPermission(player);
            }
            return type.hasPermission(player);
        }

        private boolean checkFabricPermission(@Nullable ServerPlayer player) {
            if (player == null) {
                return false;
            }
            TriState permissionValue = Permissions.getPermissionValue(player, permission);
            return switch (permissionValue) {
                case DEFAULT -> type.hasPermission(player);
                case TRUE -> true;
                default -> false;
            };
        }

        public PermissionType getType() {
            return type;
        }
    }

    public enum PermissionType {

        EVERYONE, NOONE, OPS;

        boolean hasPermission(@Nullable ServerPlayer player) {
            return switch (this) {
                case EVERYONE -> true;
                case NOONE -> false;
                case OPS ->
                        player != null && player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR);
            };
        }

    }

}
