package kishso.digsites.commands.argtypes;

import com.mojang.brigadier.context.CommandContext;
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

public record DigsiteTypeArgumentType() {

    public static class DigsiteTypeArgSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            List<String> digsiteTypeIdStrings = new ArrayList<>();
            for(DigsiteType type : DigsiteBookkeeper.GetAllLoadedDigsiteType()){
                digsiteTypeIdStrings.add(type.getDigsiteTypeId());
            }

            if (context.getSource() instanceof ServerCommandSource) {
                CommandSource.suggestMatching(digsiteTypeIdStrings, builder);
            }

            return builder.buildFuture();
        }
    }
}
