package de.maxhenkel.enhancedgroups.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeSupplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

public class GroupTypeArgumentSupplier implements ArgumentTypeSupplier<CommandSourceStack, CommandBuildContext, String> {

    public static final SuggestionProvider<CommandSourceStack> GROUP_TYPE_SUGGESTION_PROVIDER = (context, builder) -> SharedSuggestionProvider.suggest(new String[]{GroupTypeArgumentTypeSupplier.NORMAL, GroupTypeArgumentTypeSupplier.OPEN, GroupTypeArgumentTypeSupplier.ISOLATED}, builder);

    @Override
    public ArgumentType<String> get() {
        return StringArgumentType.word();
    }

    @Override
    public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
        return GROUP_TYPE_SUGGESTION_PROVIDER;
    }

}
