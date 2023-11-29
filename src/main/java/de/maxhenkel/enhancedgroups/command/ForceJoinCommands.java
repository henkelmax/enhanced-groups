package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.enhancedgroups.EnhancedGroupsVoicechatPlugin;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@RequiresPermission("enhancedgroups.forcejoingroup")
@Command(ForceJoinCommands.FORCE_JOIN_COMMAND)
public class ForceJoinCommands {

    public static final String FORCE_JOIN_COMMAND = "forcejoingroup";

    @Command
    public int forceJoin(CommandContext<CommandSourceStack> context, @Name("player") ServerPlayer player) throws CommandSyntaxException {
        ServerPlayer executor = context.getSource().getPlayer();

        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            context.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 0;
        }

        VoicechatConnection executorConnection = EnhancedGroupsVoicechatPlugin.SERVER_API.getConnectionOf(executor.getUUID());

        if (executorConnection == null) {
            context.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 0;
        }

        VoicechatConnection playerConnection = EnhancedGroupsVoicechatPlugin.SERVER_API.getConnectionOf(player.getUUID());

        if (playerConnection == null) {
            context.getSource().sendFailure(Component.literal("Player is not connected to voice chat"));
            return 0;
        }

        if (!executorConnection.isInGroup()) {
            context.getSource().sendFailure(Component.literal("You are not in a group"));
            return 0;
        }
        Group group = executorConnection.getGroup();

        playerConnection.setGroup(group);

        context.getSource().sendSuccess(() -> Component.literal("Player successfully joined your group"), false);
        return 1;
    }

}
