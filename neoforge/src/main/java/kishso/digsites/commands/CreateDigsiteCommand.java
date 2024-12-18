package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import kishso.digsites.commands.argtypes.DigsiteTypeArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public final class CreateDigsiteCommand extends CrossPlatformCommand{

    public static final String commandName = "createDigsite";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(literal(commandName)
                .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                .then(argument("digsiteType", StringArgumentType.string())
                        .suggests(new DigsiteTypeArgumentType.DigsiteTypeArgSuggestionProvider())
                        .then(argument("location", BlockPosArgument.blockPos())
                                .executes(ctx -> run(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "digsiteType"),
                                        BlockPosArgument.getBlockPos(ctx, "location")))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(CommandSourceStack source, String typeId, BlockPos pos)
    {
        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getLevel());
        DigsiteType type = DigsiteBookkeeper.GetDigsiteType(typeId);

        if(type == null)
        {
            type = new DigsiteType(typeId);
        }

        Digsite newSite = new Digsite(pos, 0.0f, 0.0f, type);
        worldState.addDigsite(newSite.getDigsiteId(), newSite);

        source.sendSystemMessage(Component.literal("Created Digsite!"));
        return Command.SINGLE_SUCCESS; // Success
    }
}
