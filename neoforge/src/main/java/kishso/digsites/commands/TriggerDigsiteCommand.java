package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;

import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class TriggerDigsiteCommand extends CrossPlatformCommand{

    public static final String commandName = "runDigsiteEvents";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(literal(commandName)
                .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                    .then(argument("digsite", UuidArgument.uuid())
                            .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                .executes(ctx -> run(ctx.getSource(), UuidArgument.getUuid(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(CommandSourceStack source, UUID digsiteId)
    {
        DigsiteBookkeeper.getWorldState(source.getLevel());
        Digsite digsite = DigsiteBookkeeper.searchForDigsite(digsiteId);

        if(digsite != null) {
            for (DigsiteEvent event : digsite.getDigsiteEvents()) {
                if (event.isConditionsMet(digsite)) {
                    source.sendSystemMessage(Component.literal(String.format("Running event [%s]", event.getEventName())));
                    event.run(digsite);
                }
            }
        }

        return Command.SINGLE_SUCCESS; // Success
    }
}
