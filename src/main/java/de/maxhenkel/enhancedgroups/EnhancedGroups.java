package de.maxhenkel.enhancedgroups;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.enhancedgroups.command.InstantGroupCommands;
import de.maxhenkel.enhancedgroups.config.CommonConfig;
import de.maxhenkel.enhancedgroups.events.GroupSummaryEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class EnhancedGroups implements ModInitializer {

    public static final String MOD_ID = "enhancedgroups";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static CommonConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = ConfigBuilder.build(Paths.get(".", "config").resolve(MOD_ID).resolve("%s.properties".formatted(MOD_ID)), true, CommonConfig::new);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> InstantGroupCommands.register(dispatcher));
        GroupSummaryEvents.init();
    }
}
