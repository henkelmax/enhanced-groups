package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeConverter;
import de.maxhenkel.voicechat.api.Group;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class GroupTypeArgumentTypeSupplier implements ArgumentTypeConverter<CommandSourceStack, String, Group.Type> {

    private static final Component INVALID_GROUP_TYPE = Component.literal("Invalid group type");

    public static final String NORMAL = "normal";
    public static final String OPEN = "open";
    public static final String ISOLATED = "isolated";

    @Nullable
    @Override
    public Group.Type convert(CommandContext commandContext, String value) throws CommandSyntaxException {
        return switch (value) {
            case NORMAL -> Group.Type.NORMAL;
            case OPEN -> Group.Type.OPEN;
            case ISOLATED -> Group.Type.ISOLATED;
            default ->
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(INVALID_GROUP_TYPE), INVALID_GROUP_TYPE);
        };
    }

}
