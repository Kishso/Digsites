package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TriggerDigsiteCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("TriggerDigsite")
                                .then(argument("uuid", UuidArgumentType.uuid())
                .executes(ctx -> run(ctx.getSource(), UuidArgumentType.getUuid(ctx, "uuid"))))); // You can deal with the arguments out here and pipe them into the command.

    }  

    public static int run(ServerCommandSource source, UUID uuid)
    {

        int numBlocksReplaced = 0;
        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getWorld());

        Digsite site = worldState.GetDigsite(uuid);

        if(site != null)
        {
             numBlocksReplaced = site.triggerDigsite(source.getWorld());
        }

        source.sendMessage(Text.literal(String.format("Replacing %d block(s)", numBlocksReplaced)));
        return Command.SINGLE_SUCCESS; // Success
    }
}
