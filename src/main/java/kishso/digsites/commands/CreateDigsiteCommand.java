package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class CreateDigsiteCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("CreateDigsite")
                .then(argument("digsiteId", UuidArgumentType.uuid())
                        .then(argument("location", BlockPosArgumentType.blockPos())
                                .then(argument("xDelta", IntegerArgumentType.integer())
                                        .then(argument("yDelta", IntegerArgumentType.integer())
                                                .then(argument("zDelta", IntegerArgumentType.integer())
                                                        .then(argument("lootTableString", StringArgumentType.string())

                                .executes(ctx -> run(ctx.getSource(),
                                        UuidArgumentType.getUuid(ctx, "digsiteId"),
                                        BlockPosArgumentType.getBlockPos(ctx, "location"),
                                        IntegerArgumentType.getInteger(ctx,  "xDelta"),
                                        IntegerArgumentType.getInteger(ctx,  "yDelta"),
                                        IntegerArgumentType.getInteger(ctx,  "zDelta"),
                                        StringArgumentType.getString(ctx, "lootTableString")
                                        ))))))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, UUID uuid, BlockPos pos,
                          int xDelta, int yDelta, int zDelta, String lootTable)
    {

        DigsiteBookkeeper serverState = DigsiteBookkeeper.getServerState(source.getServer());
        Digsite newSite = new Digsite(pos, xDelta, yDelta,zDelta, lootTable);
        serverState.AddDigsite(uuid, newSite);

        source.sendMessage(Text.literal("Created Digsite!"));
        return Command.SINGLE_SUCCESS; // Success
    }
}
