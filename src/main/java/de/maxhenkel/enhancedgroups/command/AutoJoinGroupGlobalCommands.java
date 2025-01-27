package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.context.CommandContext;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.config.PersistentGroup;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.UUID;

@RequiresPermission("enhancedgroups.autojoingroup.global")
@Command({AutoJoinGroupCommands.AUTOJOINGROUP_COMMAND, "global"})
public class AutoJoinGroupGlobalCommands {

    @Command("set")
    public int set(CommandContext<CommandSourceStack> context, @Name("group_name") String groupName) {
        PersistentGroup persistentGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(groupName);
        if (persistentGroup == null) {
            context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
            return 0;
        }
        return autoJoin(context, persistentGroup.getId());
    }

    // This method always needs to be after the String group name one, so it has priority to be processed properly
    @Command("set")
    public int set(CommandContext<CommandSourceStack> context, @Name("id") UUID groupId) {
        return autoJoin(context, groupId);
    }

    @Command("remove")
    public int remove(CommandContext<CommandSourceStack> context) {
        EnhancedGroups.AUTO_JOIN_GROUP_STORE.removeGlobalGroup();
        context.getSource().sendSuccess(() -> Component.literal("Global auto join successfully removed"), false);
        return 1;
    }

    @Command("force")
    public int setForce(CommandContext<CommandSourceStack> context, @Name("status") boolean status) {
        EnhancedGroups.AUTO_JOIN_GROUP_STORE.setGlobalGroupForced(status);
        if (status) {
            context.getSource().sendSuccess(() -> Component.literal("Global auto join is enforced from now on"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Global auto join is not enforced anymore"), false);
        }
        return 1;
    }

    public static int autoJoin(CommandContext<CommandSourceStack> context, UUID groupId) {
        PersistentGroup group = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(groupId);
        if (group == null) {
            context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
            return 0;
        }

        if (group.getPassword() != null) {
            context.getSource().sendFailure(Component.literal("Global auto join groups can't be password-protected"));
            return 0;
        }

        EnhancedGroups.AUTO_JOIN_GROUP_STORE.setGlobalGroup(group.getId());
        context.getSource().sendSuccess(() -> Component.literal("Everyone will now automatically connect to group '%s' when joining".formatted(group.getName())), false);
        return 1;
    }
}
