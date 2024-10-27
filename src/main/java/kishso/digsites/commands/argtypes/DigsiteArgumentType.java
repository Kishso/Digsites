package kishso.digsites.commands.argtypes;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
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
                CommandSource.suggestMatching(digsiteStrings, builder);
            }

            return builder.buildFuture();
        }
    }
}
