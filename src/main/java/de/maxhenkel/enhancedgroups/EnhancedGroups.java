package de.maxhenkel.enhancedgroups;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.enhancedgroups.command.AutoJoinGroupCommands;
import de.maxhenkel.enhancedgroups.command.InstantGroupCommands;
import de.maxhenkel.enhancedgroups.command.PersistentGroupCommands;
import de.maxhenkel.enhancedgroups.config.AutoJoinGroupStore;
import de.maxhenkel.enhancedgroups.config.CommonConfig;
import de.maxhenkel.enhancedgroups.config.PersistentGroupStore;
import de.maxhenkel.enhancedgroups.events.GroupSummaryEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class EnhancedGroups implements ModInitializer {

    public static final String MOD_ID = "enhancedgroups";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static CommonConfig CONFIG;
    public static PersistentGroupStore PERSISTENT_GROUP_STORE;
    public static AutoJoinGroupStore AUTO_JOIN_GROUP_STORE;

    @Override
    public void onInitialize() {
        Path configFolder = Paths.get(".", "config").resolve(MOD_ID);
        CONFIG = ConfigBuilder.build(configFolder.resolve("%s.properties".formatted(MOD_ID)), true, CommonConfig::new);
        PERSISTENT_GROUP_STORE = new PersistentGroupStore(configFolder.resolve("persistent-groups.json").toFile());
        AUTO_JOIN_GROUP_STORE = new AutoJoinGroupStore(configFolder.resolve("auto-join-groups.json").toFile());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            InstantGroupCommands.register(dispatcher);
            PersistentGroupCommands.register(dispatcher);
            AutoJoinGroupCommands.register(dispatcher);
        });
        GroupSummaryEvents.init();
    }
}
