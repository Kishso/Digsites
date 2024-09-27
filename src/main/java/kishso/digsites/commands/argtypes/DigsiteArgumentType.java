package kishso.digsites.commands.argtypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DigsiteArgumentType implements ArgumentType<Digsite> {

    public DigsiteArgumentType()
    {
    }

    public static DigsiteArgumentType digsite()
    {
        return new DigsiteArgumentType();
    }

    public static <S> Digsite getDigsite(CommandContext<S> context, String name) {
        // Note that you should assume the CommandSource wrapped inside of the CommandContext will always be a generic type.
        // If you need to access the ServerCommandSource make sure you verify the source is a server command source before casting.
        return context.getArgument(name, Digsite.class);
    }

    @Override
    public Digsite parse(StringReader stringReader) throws CommandSyntaxException {
        int argBeginning = stringReader.getCursor(); // The starting position of the cursor is at the beginning of the argument.
        if (!stringReader.canRead()) {
            stringReader.skip();
        }

        // Now we check the contents of the argument till either we hit the end of the
        // command line (when ''canRead'' becomes false)
        // Otherwise we go till reach a character that cannot compose a UUID
        while (stringReader.canRead() && (Character.isLetterOrDigit(stringReader.peek()) || stringReader.peek() == '-')) { // peek provides the character at the current cursor position.
            stringReader.skip(); // Tells the StringReader to move it's cursor to the next position.
        }

        // Now we substring the specific part we want to see using the starting cursor
        // position and the ends where the next argument starts.
        UUID digsiteId = UUID.fromString(stringReader.getString().substring(argBeginning, stringReader.getCursor()));

        return DigsiteBookkeeper.GetDigsite(digsiteId);
    }

    public static class DigsiteArgSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            List<String> digsiteStrings = new ArrayList<>();
            for(UUID uuid : DigsiteBookkeeper.GetCurrentDigsites()){
                digsiteStrings.add(uuid.toString());
            }

            if (context.getSource() instanceof ServerCommandSource) {
                CommandSource.suggestMatching(digsiteStrings, builder);
            }

            return builder.buildFuture();
        }
    }
}
