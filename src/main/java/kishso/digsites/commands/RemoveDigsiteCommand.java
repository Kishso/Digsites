package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class RemoveDigsiteCommand {

    public static final String commandName = "removeDigsite";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal(commandName)
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(argument("digsite", UuidArgumentType.uuid())
                        .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                        .executes(ctx -> run(ctx.getSource(), UuidArgumentType.getUuid(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, UUID digsiteId)
    {

        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getWorld());
        Digsite digsite = DigsiteBookkeeper.searchForDigsite(digsiteId);

        if(digsite == null){
            return 0; // Failure
        }

        if(worldState.removeDigsite(digsite.getDigsiteId()))
        {
            source.sendMessage(Text.literal("Digsite Removed!"));
            return Command.SINGLE_SUCCESS; // Success
        }

        source.sendMessage(Text.literal("Digsite not found!"));
        return 0; // Failure



    }
}
