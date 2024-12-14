package kishso.digsites.commands.argtypes;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kishso.digsites.DigsiteBookkeeper;
import net.minecraft.server.command.ServerCommandSource;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public record DigsiteArgumentType() {


    public static class DigsiteArgSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            DigsiteBookkeeper keeper = DigsiteBookkeeper.getWorldState(context.getSource().getWorld());
            List<String> digsiteStrings = new ArrayList<>();
            for(UUID uuid : keeper.getCurrentDigsites()){
                digsiteStrings.add(uuid.toString());
            }

            if (context.getSource() instanceof ServerCommandSource) {
                context.getSource().suggestMatching(digsiteStrings, builder);
            }

            return builder.buildFuture();
        }
    }
}
