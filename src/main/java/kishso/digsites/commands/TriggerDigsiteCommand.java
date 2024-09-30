package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import java.util.UUID;

import static kishso.digsites.Digsites.LOGGER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TriggerDigsiteCommand {

    public static final String commandName = "runDigsiteEvents";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal(commandName)
                                .then(argument("digsite", DigsiteArgumentType.digsite())
                                        .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                .executes(ctx -> run(ctx.getSource(), DigsiteArgumentType.getDigsite(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }  

    public static int run(ServerCommandSource source, Digsite digsite)
    {
        DigsiteType type = digsite.getDigsiteType();
        if(type != null) {
            for (DigsiteEvent event : type.getDigsiteEvents()) {
                if (event.isConditionsMet(digsite)) {
                    event.run(digsite);

                }
            }
        }
        return Command.SINGLE_SUCCESS; // Success
    }
}
