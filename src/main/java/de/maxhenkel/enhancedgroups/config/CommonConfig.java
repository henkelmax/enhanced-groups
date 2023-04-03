package de.maxhenkel.enhancedgroups.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.ConfigEntry;

public class CommonConfig {

    public final ConfigEntry<Double> defaultInstantGroupRange;
    public final ConfigEntry<String> instantGroupName;
    public final ConfigEntry<Integer> instantGroupCommandPermissionLevel;
    public final ConfigEntry<Integer> persistentGroupCommandPermissionLevel;
    public final ConfigEntry<Integer> autoJoinGroupCommandPermissionLevel;
    public final ConfigEntry<Boolean> groupSummary;

    public CommonConfig(ConfigBuilder builder) {
        defaultInstantGroupRange = builder
                .doubleEntry("default_instant_group_range", 128D, 1D, Double.MAX_VALUE)
                .comment("The default range for the instant group command if no range was provided");
        instantGroupName = builder
                .stringEntry("instant_group_name", "Instant Group")
                .comment("The name of the instant group");
        instantGroupCommandPermissionLevel = builder
                .integerEntry("instant_group_command_permission_level", 0, 0, Integer.MAX_VALUE)
                .comment("The permission level of the instantgroup command");
        persistentGroupCommandPermissionLevel = builder
                .integerEntry("persistent_group_command_permission_level", 0, 0, Integer.MAX_VALUE)
                .comment("The permission level of the persistentgroup command");
        autoJoinGroupCommandPermissionLevel = builder
                .integerEntry("auto_join_group_command_permission_level", 0, 0, Integer.MAX_VALUE)
                .comment("The permission level of the autojoingroup command");
        groupSummary = builder
                .booleanEntry("group_summary", true)
                .comment("If a summary of all groups should be shown when a player joins the server");
    }

}
