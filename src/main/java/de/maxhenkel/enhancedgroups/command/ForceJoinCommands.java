package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.EnhancedGroupsVoicechatPlugin;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ForceJoinCommands {

    public static final String FORCE_JOIN_COMMAND = "forcejoingroup";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal(FORCE_JOIN_COMMAND).requires(stack -> stack.hasPermission(EnhancedGroups.CONFIG.forceJoinGroupCommandPermissionLevel.get()));

        literalBuilder.then(Commands.argument("player", EntityArgument.player()).executes(context -> {
            ServerPlayer executor = context.getSource().getPlayer();
            ServerPlayer player = EntityArgument.getPlayer(context, "player");

            if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
                context.getSource().sendFailure(Component.literal("Voice chat not connected"));
                return 1;
            }

            VoicechatConnection executorConnection = EnhancedGroupsVoicechatPlugin.SERVER_API.getConnectionOf(executor.getUUID());

            if (executorConnection == null) {
                context.getSource().sendFailure(Component.literal("Voice chat not connected"));
                return 1;
            }

            VoicechatConnection playerConnection = EnhancedGroupsVoicechatPlugin.SERVER_API.getConnectionOf(player.getUUID());

            if (playerConnection == null) {
                context.getSource().sendFailure(Component.literal("Player is not connected to voice chat"));
                return 1;
            }

            if (!executorConnection.isInGroup()) {
                context.getSource().sendFailure(Component.literal("You are not in a group"));
                return 1;
            }
            Group group = executorConnection.getGroup();

            playerConnection.setGroup(group);

            context.getSource().sendSuccess(Component.literal("Player successfully joined your group"), false);

            return 1;
        }));

        dispatcher.register(literalBuilder);
    }

}
