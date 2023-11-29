package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.OptionalArgument;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.EnhancedGroupsVoicechatPlugin;
import de.maxhenkel.enhancedgroups.config.PersistentGroup;
import de.maxhenkel.voicechat.api.Group;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiresPermission("enhancedgroups.persistentgroup")
@Command(PersistentGroupCommands.PERSISTENTGROUP_COMMAND)
public class PersistentGroupCommands {

    public static final String PERSISTENTGROUP_COMMAND = "persistentgroup";

    @Command("add")
    public int add(CommandContext<CommandSourceStack> context, @Name("name") String name, @Name("type") Optional<Group.Type> groupType, @OptionalArgument @Name("password") String password) {
        if (name.isBlank()) {
            context.getSource().sendFailure(Component.literal("Name cannot be blank"));
            return 1;
        }

        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            context.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        Group.Type type = groupType.orElse(Group.Type.NORMAL);

        Group vcGroup = EnhancedGroupsVoicechatPlugin.SERVER_API.groupBuilder().setPersistent(true).setName(name).setPassword(password).setType(type).build();

        PersistentGroup persistentGroup = new PersistentGroup(name, password, PersistentGroup.Type.fromGroupType(type));
        EnhancedGroups.PERSISTENT_GROUP_STORE.addGroup(persistentGroup);
        EnhancedGroups.PERSISTENT_GROUP_STORE.addCached(vcGroup.getId(), persistentGroup);

        context.getSource().sendSuccess(() -> Component.literal("Successfully created persistent group " + name), false);

        return 1;
    }

    public static int addPersistentGroup(CommandContext<CommandSourceStack> commandSource, String name, @Nullable String password, Group.Type type) {
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

        commandSource.getSource().sendSuccess(() -> Component.literal("Successfully created persistent group " + name), false);

        return 1;
    }

    @Command("remove")
    public int remove(CommandContext<CommandSourceStack> context, @Name("name") String name) {
        PersistentGroup group = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(name);
        if (group == null) {
            context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
            return 0;
        }
        return removePersistentGroup(context, group);
    }

    // This method always needs to be after the String group name one, so it has priority to be processed properly
    @Command("remove")
    public int remove(CommandContext<CommandSourceStack> context, @Name("id") UUID id) {
        PersistentGroup group = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(id);
        if (group == null) {
            context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
            return 0;
        }
        return removePersistentGroup(context, group);
    }

    public static int removePersistentGroup(CommandContext<CommandSourceStack> commandSource, PersistentGroup persistentGroup) {
        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            commandSource.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 0;
        }

        UUID voicechatId = EnhancedGroups.PERSISTENT_GROUP_STORE.getVoicechatId(persistentGroup.getId());

        if (voicechatId == null) {
            commandSource.getSource().sendFailure(Component.literal("Group not found or not persistent"));
            return 0;
        }

        Group group = EnhancedGroupsVoicechatPlugin.SERVER_API.getGroup(voicechatId);

        if (group == null) {
            commandSource.getSource().sendFailure(Component.literal("Group not found"));
            return 0;
        }

        boolean removed = EnhancedGroupsVoicechatPlugin.SERVER_API.removeGroup(voicechatId);
        if (removed) {
            EnhancedGroups.PERSISTENT_GROUP_STORE.removeGroup(persistentGroup);
            commandSource.getSource().sendSuccess(() -> Component.literal("Removed group %s".formatted(group.getName())), false);
            return 1;
        } else {
            commandSource.getSource().sendFailure(Component.literal("Could not remove group %s".formatted(group.getName())));
            return 0;
        }
    }

    @Command("list")
    public int list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            context.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        List<PersistentGroup> groups = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroups();

        if (groups.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("There are no persistent groups"), false);
        }

        for (PersistentGroup group : EnhancedGroups.PERSISTENT_GROUP_STORE.getGroups()) {
            context.getSource().sendSuccess(() -> Component.literal(group.getName())
                            .append(" ")
                            .append(ComponentUtils.wrapInSquareBrackets(Component.literal("Remove")).withStyle(style -> {
                                return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + PERSISTENTGROUP_COMMAND + " remove " + group.getId()))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to remove group")))
                                        .applyFormat(ChatFormatting.GREEN);
                            }))
                            .append(" ")
                            .append(ComponentUtils.wrapInSquareBrackets(Component.literal("Auto Join")).withStyle(style -> {
                                return style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + AutoJoinGroupCommands.AUTOJOINGROUP_COMMAND + " set " + group.getId()))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to automatically connect to the group when joining")))
                                        .applyFormat(ChatFormatting.GREEN);
                            }))
                            .append(" ")
                            .append(ComponentUtils.wrapInSquareBrackets(Component.literal("Copy ID")).withStyle(style -> {
                                return style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, group.getId().toString()))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy group ID")))
                                        .applyFormat(ChatFormatting.GREEN);
                            }))
                    , false);
        }
        return groups.size();
    }

}
