package de.maxhenkel.enhancedgroups;

import de.maxhenkel.admiral.MinecraftAdmiral;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.enhancedgroups.command.*;
import de.maxhenkel.enhancedgroups.config.AutoJoinGroupStore;
import de.maxhenkel.enhancedgroups.config.CommonConfig;
import de.maxhenkel.enhancedgroups.config.PersistentGroupStore;
import de.maxhenkel.enhancedgroups.events.GroupSummaryEvents;
import de.maxhenkel.voicechat.api.Group;
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

    public static EnhancedGroupPermissionManager PERMISSION_MANAGER;

    @Override
    public void onInitialize() {
        Path configFolder = Paths.get(".", "config").resolve(MOD_ID);
        CONFIG = ConfigBuilder.builder(CommonConfig::new).path(configFolder.resolve("%s.properties".formatted(MOD_ID))).build();
        PERSISTENT_GROUP_STORE = new PersistentGroupStore(configFolder.resolve("persistent-groups.json").toFile());
        AUTO_JOIN_GROUP_STORE = new AutoJoinGroupStore(configFolder.resolve("auto-join-groups.json").toFile());
        PERMISSION_MANAGER = new EnhancedGroupPermissionManager();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MinecraftAdmiral.builder(dispatcher, registryAccess)
                .addCommandClasses(
                        AutoJoinGroupCommands.class,
                        AutoJoinGroupGlobalCommands.class,
                        ForceJoinCommands.class,
                        InstantGroupCommands.class,
                        PersistentGroupCommands.class
                )
                .setPermissionManager(PERMISSION_MANAGER)
                .addArgumentTypes(argumentTypeRegistry -> argumentTypeRegistry.register(Group.Type.class, new GroupTypeArgumentSupplier(), new GroupTypeArgumentTypeSupplier()))
                .build());
        GroupSummaryEvents.init();
    }
}
