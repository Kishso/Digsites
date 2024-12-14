package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class RemoveDigsiteCommand extends CrossPlatformCommand{

    public static final String commandName = "removeDigsite";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(literal(commandName)
                .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                .then(argument("digsite", UuidArgument.uuid())
                        .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                        .executes(ctx -> run(ctx.getSource(), UuidArgument.getUuid(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(CommandSourceStack source, UUID digsiteId)
    {

        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getLevel());
        Digsite digsite = DigsiteBookkeeper.searchForDigsite(digsiteId);

        if(digsite == null){
            return 0; // Failure
        }

        if(worldState.removeDigsite(digsite.getDigsiteId()))
        {
            source.sendSystemMessage(Component.literal("Digsite Removed!"));
            return Command.SINGLE_SUCCESS; // Success
        }

        source.sendSystemMessage(Component.literal("Digsite not found!"));
        return 0; // Failure



    }
}
