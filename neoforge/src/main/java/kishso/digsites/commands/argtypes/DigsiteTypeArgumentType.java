package kishso.digsites.commands.argtypes;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public record DigsiteTypeArgumentType() {

    public static class DigsiteTypeArgSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
            for(DigsiteType type : DigsiteBookkeeper.GetAllLoadedDigsiteType()){
                builder.suggest(type.getDigsiteTypeId());
            }

            return builder.buildFuture();
        }
    }
}
