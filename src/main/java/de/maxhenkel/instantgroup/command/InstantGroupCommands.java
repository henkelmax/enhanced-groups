package de.maxhenkel.instantgroup.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.instantgroup.InstantGroup;
import de.maxhenkel.instantgroup.InstantGroupVoicechatPlugin;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class InstantGroupCommands {

    public static final String INSTANTGROUP_COMMAND = "instantgroup";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal(INSTANTGROUP_COMMAND).requires(stack -> stack.hasPermission(InstantGroup.CONFIG.commandPermissionLevel.get()));

        literalBuilder.executes(context -> {
            return instantGroup(context, InstantGroup.CONFIG.defaultGroupRange.get());
        });

        literalBuilder.then(Commands.argument("range", DoubleArgumentType.doubleArg(1D)).executes(context -> {
            return instantGroup(context, DoubleArgumentType.getDouble(context, "range"));
        }));

        dispatcher.register(literalBuilder);
    }

    public static int instantGroup(CommandContext<CommandSourceStack> commandSource, double radius) throws CommandSyntaxException {
        ServerPlayer player = commandSource.getSource().getPlayerOrException();

        if (InstantGroupVoicechatPlugin.SERVER_API == null) {
            commandSource.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        VoicechatConnection playerConnection = InstantGroupVoicechatPlugin.SERVER_API.getConnectionOf(player.getUUID());

        Group group;

        if (playerConnection == null) {
            commandSource.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 1;
        }

        if (playerConnection.isInGroup()) {
            group = playerConnection.getGroup();
        } else {
            group = InstantGroupVoicechatPlugin.SERVER_API.createGroup(InstantGroup.CONFIG.instantGroupName.get(), null);
        }

        List<ServerPlayer> players = player.level.getEntitiesOfClass(ServerPlayer.class, new AABB(player.position().x - radius, player.position().y - radius, player.position().z - radius, player.position().x + radius, player.position().y + radius, player.position().z + radius));

        for (ServerPlayer p : players) {
            VoicechatConnection connection = InstantGroupVoicechatPlugin.SERVER_API.getConnectionOf(p.getUUID());
            if (connection == null) {
                continue;
            }
            if (connection.getGroup() != null) {
                continue;
            }
            connection.setGroup(group);
        }

        return 1;
    }

}
