package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import kishso.digsites.commands.argtypes.DigsiteTypeArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class CreateDigsiteCommand {

    public static final String commandName = "createDigsite";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal(commandName)
                .then(argument("digsiteType", DigsiteTypeArgumentType.digsiteType())
                        .suggests(new DigsiteTypeArgumentType.DigsiteTypeArgSuggestionProvider())
                        .then(argument("location", BlockPosArgumentType.blockPos())
                                .executes(ctx -> run(ctx.getSource(),
                                        DigsiteTypeArgumentType.getDigsiteType(ctx, "digsiteType"),
                                        BlockPosArgumentType.getBlockPos(ctx, "location")))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, DigsiteType type, BlockPos pos)
    {
        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getWorld());

        Digsite newSite = new Digsite(pos, Direction.SOUTH, type);
        worldState.addDigsite(newSite.getDigsiteId(), newSite);

        source.sendMessage(Text.literal("Created Digsite!"));
        return Command.SINGLE_SUCCESS; // Success
    }
}
