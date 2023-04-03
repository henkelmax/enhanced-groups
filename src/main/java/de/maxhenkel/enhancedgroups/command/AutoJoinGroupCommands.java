package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.config.PersistentGroup;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class AutoJoinGroupCommands {

    public static final String AUTOJOINGROUP_COMMAND = "autojoingroup";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal(AUTOJOINGROUP_COMMAND).requires(stack -> stack.hasPermission(EnhancedGroups.CONFIG.autoJoinGroupCommandPermissionLevel.get()));

        literalBuilder.then(Commands.literal("set").then(Commands.argument("id", UuidArgument.uuid()).executes(context -> {
            return autoJoin(context, UuidArgument.getUuid(context, "id"), null);
        }).then(Commands.argument("password", StringArgumentType.string()).executes(context -> {
            return autoJoin(context, UuidArgument.getUuid(context, "id"), StringArgumentType.getString(context, "password"));
        }))));

        literalBuilder.then(Commands.literal("set").then(Commands.argument("group_name", StringArgumentType.string()).executes(context -> {
                    String groupName = StringArgumentType.getString(context, "group_name");
                    Optional<PersistentGroup> optionalPersistentGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroups().stream().filter(g -> g.getName().trim().equals(groupName.trim())).findFirst();
                    if (optionalPersistentGroup.isEmpty()) {
                        context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
                        return 0;
                    }
                    return autoJoin(context, optionalPersistentGroup.get().getId(), null);
                })
                .then(Commands.argument("password", StringArgumentType.string()).executes(context -> {
                    String groupName = StringArgumentType.getString(context, "group_name");
                    String password = StringArgumentType.getString(context, "password");
                    Optional<PersistentGroup> optionalPersistentGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroups().stream().filter(g -> g.getName().trim().equals(groupName.trim())).findFirst();
                    if (optionalPersistentGroup.isEmpty()) {
                        context.getSource().sendFailure(Component.literal("Group not found or not persistent"));
                        return 0;
                    }
                    return autoJoin(context, optionalPersistentGroup.get().getId(), password);
                }))));

        literalBuilder.then(Commands.literal("remove").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            EnhancedGroups.AUTO_JOIN_GROUP_STORE.removePlayerGroup(player.getUUID());
            context.getSource().sendSuccess(Component.literal("Auto join successfully removed"), false);
            return 1;
        }));

        dispatcher.register(literalBuilder);
    }

    public static int autoJoin(CommandContext<CommandSourceStack> context, UUID groupId, @Nullable String password) throws CommandSyntaxException {
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
        context.getSource().sendSuccess(Component.literal("You will now automatically connect to group '%s' when joining".formatted(group.getName())), false);
        return 1;
    }

}
