package de.maxhenkel.enhancedgroups.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.ConfigEntry;

public class CommonConfig {

    public final ConfigEntry<Double> defaultInstantGroupRange;
    public final ConfigEntry<String> instantGroupName;
    public final ConfigEntry<Integer> instantGroupCommandPermissionLevel;

    public CommonConfig(ConfigBuilder builder) {
        defaultInstantGroupRange = builder.doubleEntry("default_instant_group_range", 128D, 1D, Double.MAX_VALUE);
        instantGroupName = builder.stringEntry("instant_group_name", "Instant Group");
        instantGroupCommandPermissionLevel = builder.integerEntry("instant_group_command_permission_level", 0, 0, Integer.MAX_VALUE);
    }

}
