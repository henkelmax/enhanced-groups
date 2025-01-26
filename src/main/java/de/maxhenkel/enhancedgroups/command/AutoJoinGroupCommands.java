package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.OptionalArgument;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.config.PersistentGroup;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

@RequiresPermission("enhancedgroups.autojoingroup")
@Command(AutoJoinGroupCommands.AUTOJOINGROUP_COMMAND)
public class AutoJoinGroupCommands {

    public static final String AUTOJOINGROUP_COMMAND = "autojoingroup";

    @Command("set")
    public int set(CommandContext<CommandSourceStack> context, @Name("group_name") String groupName, @OptionalArgument @Name("password") String password) throws CommandSyntaxException {
        PersistentGroup persistentGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(groupName);
        if (persistentGroup == null) {
            context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
            return 0;
        }
        return autoJoin(context, persistentGroup.getId(), password);
    }

    // This method always needs to be after the String group name one, so it has priority to be processed properly
    @Command("set")
    public int set(CommandContext<CommandSourceStack> context, @Name("id") UUID groupId, @OptionalArgument @Name("password") String password) throws CommandSyntaxException {
        return autoJoin(context, groupId, password);
    }

    @Command("remove")
    public int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        EnhancedGroups.AUTO_JOIN_GROUP_STORE.removePlayerGroup(player.getUUID());
        context.getSource().sendSuccess(() -> Component.literal("Auto join successfully removed"), false);
        return 1;
    }

    public static int autoJoin(CommandContext<CommandSourceStack> context, UUID groupId, @Nullable String password) throws CommandSyntaxException {
        if (EnhancedGroups.AUTO_JOIN_GROUP_STORE.getGlobalGroupForced()) {
            context.getSource().sendFailure(Component.literal("Global auto join is enforced on this server"));
            return 0;
        }

        ServerPlayer player = context.getSource().getPlayerOrException();

        PersistentGroup group = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(groupId);
        if (group == null) {
            context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
            return 0;
        }

        if (group.getPassword() != null && (password == null || !password.equals(group.getPassword()))) {
            context.getSource().sendFailure(Component.literal("Wrong password"));
            return 0;
        }

        EnhancedGroups.AUTO_JOIN_GROUP_STORE.setPlayerGroup(player.getUUID(), group.getId());
        context.getSource().sendSuccess(() -> Component.literal("You will now automatically connect to group '%s' when joining".formatted(group.getName())), false);
        return 1;
    }
}
