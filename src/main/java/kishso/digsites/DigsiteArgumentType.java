package kishso.digsites;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DigsiteArgumentType implements ArgumentType<String> {

    protected String value = "none";

    public DigsiteArgumentType()
    {
    }

    public static DigsiteArgumentType digsiteType()
    {
        return new DigsiteArgumentType();
    }

    public static String getDigsiteType(CommandContext<ServerCommandSource> ctx, String argId)
    {
        return ctx.getArgument(argId, String.class);
    }

    @Override
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        return stringReader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        List<String> digsiteTypes = new ArrayList<String>();

        digsiteTypes.add("digsite_normal");
        digsiteTypes.add("digsite_lush");

        if (context.getSource() instanceof ServerCommandSource source) {
            return CommandSource.suggestMatching(digsiteTypes, builder);
        }

        return builder.buildFuture();
    }
}
