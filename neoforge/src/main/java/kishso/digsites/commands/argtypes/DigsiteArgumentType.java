package kishso.digsites.commands.argtypes;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kishso.digsites.DigsiteBookkeeper;
import net.minecraft.commands.CommandSourceStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public record DigsiteArgumentType() {


    public static class DigsiteArgSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) {
            if(context.getSource() instanceof CommandSourceStack) {
                DigsiteBookkeeper keeper = DigsiteBookkeeper.getWorldState(((CommandSourceStack) context.getSource()).getLevel());for (UUID uuid : keeper.getCurrentDigsites()) {
                        builder.suggest(uuid.toString());
                }
            }

            return builder.buildFuture();
        }
    }
}
