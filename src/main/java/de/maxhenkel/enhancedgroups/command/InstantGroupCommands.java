package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.*;
import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.EnhancedGroupsVoicechatPlugin;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

@RequiresPermission("enhancedgroups.instantgroup")
@Command(InstantGroupCommands.INSTANTGROUP_COMMAND)
public class InstantGroupCommands {

    public static final String INSTANTGROUP_COMMAND = "instantgroup";

    @Command
    public int instantGroup(CommandContext<CommandSourceStack> context, @Name("range") @Min("1") Optional<Double> optionalRange) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            context.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 0;
        }

        VoicechatConnection playerConnection = EnhancedGroupsVoicechatPlugin.SERVER_API.getConnectionOf(player.getUUID());

        Group group;

        if (playerConnection == null) {
            context.getSource().sendFailure(Component.literal("Voice chat not connected"));
            return 0;
        }

        if (playerConnection.isInGroup()) {
            group = playerConnection.getGroup();
        } else {
            group = EnhancedGroupsVoicechatPlugin.SERVER_API.groupBuilder().setName(EnhancedGroups.CONFIG.instantGroupName.get()).setType(Group.Type.OPEN).build();
        }
        double range = optionalRange.orElse(EnhancedGroups.CONFIG.defaultInstantGroupRange.get());
        List<ServerPlayer> players = player.getLevel().getEntitiesOfClass(ServerPlayer.class, new AABB(player.position().x - range, player.position().y - range, player.position().z - range, player.position().x + range, player.position().y + range, player.position().z + range));

        for (ServerPlayer p : players) {
            VoicechatConnection connection = EnhancedGroupsVoicechatPlugin.SERVER_API.getConnectionOf(p.getUUID());
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
