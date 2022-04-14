package de.maxhenkel.instantgroup.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.ConfigEntry;

public class CommonConfig {

    public final ConfigEntry<Double> defaultGroupRange;
    public final ConfigEntry<String> instantGroupName;
    public final ConfigEntry<Integer> commandPermissionLevel;

    public CommonConfig(ConfigBuilder builder) {
        defaultGroupRange = builder.doubleEntry("default_group_range", 128D, 1D, Double.MAX_VALUE);
        instantGroupName = builder.stringEntry("instant_group_name", "Instant Group");
        commandPermissionLevel = builder.integerEntry("command_permission_level", 0, 0, Integer.MAX_VALUE);
    }

}
