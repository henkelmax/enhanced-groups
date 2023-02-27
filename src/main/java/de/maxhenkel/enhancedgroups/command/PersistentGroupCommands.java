package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.EnhancedGroupsVoicechatPlugin;
import de.maxhenkel.enhancedgroups.config.PersistentGroup;
import de.maxhenkel.voicechat.api.Group;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PersistentGroupCommands {

    public static final String PERSISTENTGROUP_COMMAND = "persistentgroup";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal(PERSISTENTGROUP_COMMAND).requires(stack -> stack.hasPermission(EnhancedGroups.CONFIG.persistentGroupCommandPermissionLevel.get()));

        RequiredArgumentBuilder<CommandSourceStack, String> nameArg = Commands.argument("name", StringArgumentType.string());

        nameArg.executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), null, Group.Type.NORMAL);
        });

        nameArg.then(Commands.argument("password", StringArgumentType.string()).executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "password"), Group.Type.NORMAL);
        }));

        nameArg.then(Commands.literal("normal").then(Commands.argument("password", StringArgumentType.string()).executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "password"), Group.Type.NORMAL);
        })));
        nameArg.then(Commands.literal("open").then(Commands.argument("password", StringArgumentType.string()).executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "password"), Group.Type.OPEN);
        })));
        nameArg.then(Commands.literal("isolated").then(Commands.argument("password", StringArgumentType.string()).executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "password"), Group.Type.ISOLATED);
        })));

        nameArg.then(Commands.literal("normal").executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), null, Group.Type.NORMAL);
        }));
        nameArg.then(Commands.literal("open").executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), null, Group.Type.OPEN);
        }));
        nameArg.then(Commands.literal("isolated").executes(context -> {
            return addPersistentGroup(context, StringArgumentType.getString(context, "name"), null, Group.Type.ISOLATED);
        }));

        literalBuilder.then(Commands.literal("add").then(nameArg));

        literalBuilder.then(Commands.literal("remove").then(Commands.argument("id", UuidArgument.uuid()).executes(context -> {
            return removePersistentGroup(context, UuidArgument.getUuid(context, "id"));
        })).then(Commands.argument("name", StringArgumentType.string()).executes(context -> {
            return removePersistentGroup(context, StringArgumentType.getString(context, "name"));
        })));

        literalBuilder.then(Commands.literal("list").executes(PersistentGroupCommands::listPersistentGroups));

        dispatcher.register(literalBuilder);
    }

    public static int addPersistentGroup(CommandContext<CommandSourceStack> commandSource, String name, @Nullable String password, Group.Type type) throws CommandSyntaxException {
        if (name.isBlank()) {
            commandSource.getSource().sendFailure(Component.literal("Name cannot be blank"));
            return 1;
        }

        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            commandSource.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        Group vcGroup = EnhancedGroupsVoicechatPlugin.SERVER_API.groupBuilder().setPersistent(true).setName(name).setPassword(password).setType(type).build();

        PersistentGroup persistentGroup = new PersistentGroup(name, password, PersistentGroup.Type.fromGroupType(type));
        EnhancedGroups.PERSISTENT_GROUP_STORE.addGroup(persistentGroup);
        EnhancedGroups.PERSISTENT_GROUP_STORE.addCached(vcGroup.getId(), persistentGroup);

        commandSource.getSource().sendSuccess(Component.literal("Successfully created persistent group " + name), false);

        return 1;
    }

    public static int removePersistentGroup(CommandContext<CommandSourceStack> commandSource, String name) throws CommandSyntaxException {
        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            commandSource.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        Collection<Group> groups = EnhancedGroupsVoicechatPlugin.SERVER_API.getGroups();

        int removedCount = 0;

        for (Group group : groups) {
            if (group.getName().equals(name)) {
                boolean removed = EnhancedGroupsVoicechatPlugin.SERVER_API.removeGroup(group.getId());
                if (removed) {
                    PersistentGroup cachedGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getCached(group.getId());
                    if (cachedGroup == null) {
                        commandSource.getSource().sendFailure(Component.literal("This group was not created by EnhancedGroups"));
                        return 1;
                    }
                    EnhancedGroups.PERSISTENT_GROUP_STORE.removeGroup(cachedGroup);
                    commandSource.getSource().sendSuccess(Component.literal("Removed group " + name), false);
                } else {
                    commandSource.getSource().sendFailure(Component.literal("Could not remove group " + name));
                }
                removedCount++;
            }
        }

        if (removedCount <= 0) {
            commandSource.getSource().sendFailure(Component.literal("Could not find group " + name));
        }

        return 1;
    }

    public static int removePersistentGroup(CommandContext<CommandSourceStack> commandSource, UUID id) throws CommandSyntaxException {
        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            commandSource.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        Group group = EnhancedGroupsVoicechatPlugin.SERVER_API.getGroup(id);

        if (group == null) {
            commandSource.getSource().sendFailure(Component.literal("Group not found"));
            return 1;
        }

        boolean removed = EnhancedGroupsVoicechatPlugin.SERVER_API.removeGroup(id);
        if (removed) {
            PersistentGroup cachedGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getCached(group.getId());
            if (cachedGroup == null) {
                commandSource.getSource().sendFailure(Component.literal("This group was not created by EnhancedGroups"));
                return 1;
            }
            EnhancedGroups.PERSISTENT_GROUP_STORE.removeGroup(cachedGroup);
            commandSource.getSource().sendSuccess(Component.literal("Removed group " + group.getName()), false);
        } else {
            commandSource.getSource().sendFailure(Component.literal("Could not remove group " + group.getName()));
        }

        return 1;
    }

    public static int listPersistentGroups(CommandContext<CommandSourceStack> commandSource) throws CommandSyntaxException {
        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            commandSource.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        List<Group> groups = EnhancedGroupsVoicechatPlugin.SERVER_API.getGroups().stream().filter(Group::isPersistent).toList();

        if (groups.isEmpty()) {
            commandSource.getSource().sendSuccess(Component.literal("There are no persistent groups"), false);
        }


        for (Group group : groups) {
            PersistentGroup cachedGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getCached(group.getId());
            if (cachedGroup == null) {
                continue;
            }
            commandSource.getSource().sendSuccess(Component.literal(group.getName()).append(" ").append(ComponentUtils.wrapInSquareBrackets(Component.literal("Remove")).withStyle(style -> {
                return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + PERSISTENTGROUP_COMMAND + " remove " + group.getId()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to remove group")))
                        .applyFormat(ChatFormatting.GREEN);
            })), false);
        }
        return groups.size();
    }

}
