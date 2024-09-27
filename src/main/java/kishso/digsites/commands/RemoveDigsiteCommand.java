package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class RemoveDigsiteCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("RemoveDigsite")
                .then(argument("digsite", DigsiteArgumentType.digsite())
                        .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                        .executes(ctx -> run(ctx.getSource(), DigsiteArgumentType.getDigsite(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, Digsite digsite)
    {

        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getWorld());

        if(worldState.RemoveDigsite(digsite.getDigsiteId()))
        {
            source.sendMessage(Text.literal("Digsite Removed!"));
            return Command.SINGLE_SUCCESS; // Success
        }

        source.sendMessage(Text.literal("Digsite not found!"));
        return 0; // Failure



    }
}
