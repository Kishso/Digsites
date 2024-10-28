package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TriggerDigsiteCommand {

    public static final String commandName = "runDigsiteEvents";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal(commandName)
                                .then(argument("digsite", UuidArgumentType.uuid())
                                        .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                .executes(ctx -> run(ctx.getSource(), UuidArgumentType.getUuid(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }  

    public static int run(ServerCommandSource source, UUID digsiteId)
    {
        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getWorld());
        Digsite digsite = worldState.searchForDigsite(digsiteId);

        if(digsite != null) {
            for (DigsiteEvent event : digsite.getDigsiteEvents()) {
                if (event.isConditionsMet(digsite)) {
                    source.sendMessage(Text.literal(String.format("Running event [%s]", event.getEventName())));
                    event.run(digsite);
                }
            }
        }

        return Command.SINGLE_SUCCESS; // Success
    }
}
