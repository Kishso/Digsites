package kishso.digsites.commands.argtypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DigsiteTypeArgumentType implements ArgumentType<DigsiteType> {

    public DigsiteTypeArgumentType()
    {
    }

    public static DigsiteTypeArgumentType digsiteType()
    {
        return new DigsiteTypeArgumentType();
    }

    public static <S> DigsiteType getDigsiteType( CommandContext<S> context, String name) {
        // Note that you should assume the CommandSource wrapped inside of the CommandContext will always be a generic type.
        // If you need to access the ServerCommandSource make sure you verify the source is a server command source before casting.
        return context.getArgument(name, DigsiteType.class);
    }

    @Override
    public DigsiteType parse(StringReader stringReader) throws CommandSyntaxException {
        int argBeginning = stringReader.getCursor(); // The starting position of the cursor is at the beginning of the argument.
        if (!stringReader.canRead()) {
            stringReader.skip();
        }

        // Now we check the contents of the argument till either we hit the end of the
        // command line (when ''canRead'' becomes false)
        // Otherwise we go till reach a character that cannot compose a UUID
        while (stringReader.canRead() && (!Character.isWhitespace(stringReader.peek()))) { // peek provides the character at the current cursor position.
            stringReader.skip(); // Tells the StringReader to move it's cursor to the next position.
        }

        // Now we substring the specific part we want to see using the starting cursor
        // position and the ends where the next argument starts.
        String digsiteTypeStr = stringReader.getString().substring(argBeginning, stringReader.getCursor());

        DigsiteType type = DigsiteBookkeeper.GetDigsiteType(digsiteTypeStr);
        if( type != null ) {
            return type;
        }
        else {
            return null;
        }
    }

    public static class DigsiteTypeArgSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            final String remaining = builder.getRemaining();
            List<String> digsiteTypeIdStrings = new ArrayList<>();
            for(DigsiteType type : DigsiteBookkeeper.GetAllLoadedDigsiteType()){
                digsiteTypeIdStrings.add(type.getDigsiteTypeId());
            }

            if (context.getSource() instanceof ServerCommandSource source) {
                CommandSource.suggestMatching(digsiteTypeIdStrings, builder);
            }

            return builder.buildFuture();
        }
    }
}
