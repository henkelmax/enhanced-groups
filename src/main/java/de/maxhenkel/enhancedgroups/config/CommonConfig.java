package de.maxhenkel.enhancedgroups.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import de.maxhenkel.enhancedgroups.EnhancedGroupPermissionManager;

public class CommonConfig {

    public final ConfigEntry<Double> defaultInstantGroupRange;
    public final ConfigEntry<String> instantGroupName;
    public final ConfigEntry<EnhancedGroupPermissionManager.PermissionType> instantGroupCommandPermissionType;
    public final ConfigEntry<EnhancedGroupPermissionManager.PermissionType> persistentGroupCommandPermissionType;
    public final ConfigEntry<EnhancedGroupPermissionManager.PermissionType> autoJoinGroupCommandPermissionType;
    public final ConfigEntry<EnhancedGroupPermissionManager.PermissionType> autoJoinGroupGlobalCommandPermissionType;
    public final ConfigEntry<EnhancedGroupPermissionManager.PermissionType> forceJoinGroupCommandPermissionType;
    public final ConfigEntry<Boolean> groupSummary;
    public final ConfigEntry<ForcedGroupType> forceGroupType;

    public CommonConfig(ConfigBuilder builder) {
        defaultInstantGroupRange = builder
                .doubleEntry("default_instant_group_range", 128D, 1D, Double.MAX_VALUE)
                .comment("The default range for the /instantgroup command if no range was provided");
        instantGroupName = builder
                .stringEntry("instant_group_name", "Instant Group")
                .comment("The name of the instant group");
        instantGroupCommandPermissionType = builder
                .enumEntry("instant_group_command_permission_level", EnhancedGroupPermissionManager.PermissionType.EVERYONE)
                .comment(
                        "The default permission level of the /instantgroup command",
                        "EVERYONE - Every player can use this command",
                        "OPS - Operators can use this command",
                        "NOONE - The command can't be used by anyone"
                );
        persistentGroupCommandPermissionType = builder
                .enumEntry("persistent_group_command_permission_level", EnhancedGroupPermissionManager.PermissionType.OPS)
                .comment(
                        "The default permission level of the /persistentgroup command",
                        "EVERYONE - Every player can use this command",
                        "OPS - Operators can use this command",
                        "NOONE - The command can't be used by anyone"
                );
        autoJoinGroupCommandPermissionType = builder
                .enumEntry("auto_join_group_command_permission_type", EnhancedGroupPermissionManager.PermissionType.EVERYONE)
                .comment(
                        "The default permission level of the /autojoingroup command",
                        "EVERYONE - Every player can use this command",
                        "OPS - Operators can use this command",
                        "NOONE - The command can't be used by anyone"
                );
        autoJoinGroupGlobalCommandPermissionType = builder
                .enumEntry("auto_join_group_global_command_permission_type", EnhancedGroupPermissionManager.PermissionType.OPS)
                .comment(
                        "The default permission level of the /autojoingroup global command",
                        "EVERYONE - Every player can use this command",
                        "OPS - Operators can use this command",
                        "NOONE - The command can't be used by anyone"
                );
        forceJoinGroupCommandPermissionType = builder
                .enumEntry("force_join_group_command_permission_type", EnhancedGroupPermissionManager.PermissionType.OPS)
                .comment(
                        "The default permission level of the /forcejoingroup command",
                        "EVERYONE - Every player can use this command",
                        "OPS - Operators can use this command",
                        "NOONE - The command can't be used by anyone"
                );
        groupSummary = builder
                .booleanEntry("group_summary", true)
                .comment("Determines if a summary of all groups should be shown when a player joins the server");
        forceGroupType = builder
                .enumEntry("force_group_type", ForcedGroupType.OFF)
                .comment(
                        "Forces all groups to be of the given type",
                        "OFF - No group type is forced",
                        "NORMAL - All groups are forced to be normal groups",
                        "OPEN - All groups are forced to be open groups",
                        "ISOLATED - All groups are forced to be isolated groups"
                );
    }

}
