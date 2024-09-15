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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class CreateDigsiteCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("CreateDigsite")
                .then(argument("digsiteId", UuidArgumentType.uuid())
                        .then(argument("location", BlockPosArgumentType.blockPos())
                                .then(argument("xDeltaLower", IntegerArgumentType.integer())
                                .then(argument("xDeltaUpper", IntegerArgumentType.integer())
                                        .then(argument("yDeltaLower", IntegerArgumentType.integer())
                                        .then(argument("yDeltaUpper", IntegerArgumentType.integer())
                                                .then(argument("zDeltaLower", IntegerArgumentType.integer())
                                                .then(argument("zDeltaUpper", IntegerArgumentType.integer())
                                                        .then(argument("lootTableString", StringArgumentType.string())

                                .executes(ctx -> run(ctx.getSource(),
                                        UuidArgumentType.getUuid(ctx, "digsiteId"),
                                        BlockPosArgumentType.getBlockPos(ctx, "location"),
                                        IntegerArgumentType.getInteger(ctx,  "xDeltaLower"),
                                        IntegerArgumentType.getInteger(ctx,  "xDeltaUpper"),
                                        IntegerArgumentType.getInteger(ctx,  "yDeltaLower"),
                                        IntegerArgumentType.getInteger(ctx,  "yDeltaUpper"),
                                        IntegerArgumentType.getInteger(ctx,  "zDeltaLower"),
                                        IntegerArgumentType.getInteger(ctx,  "zDeltaUpper"),
                                        StringArgumentType.getString(ctx, "lootTableString")
                                        )))))))))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, UUID uuid, BlockPos pos,
                          int xDeltaLower, int xDeltaUpper,
                          int yDeltaLower, int yDeltaUpper,
                          int zDeltaLower, int zDeltaUpper,
                          String lootTable)
    {

        DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getWorld());
        Digsite newSite = new Digsite(pos,
                xDeltaLower, xDeltaUpper,
                yDeltaLower, yDeltaUpper,
                zDeltaLower, zDeltaUpper,
                lootTable);
        worldState.AddDigsite(uuid, newSite);

        source.sendMessage(Text.literal("Created Digsite!"));
        return Command.SINGLE_SUCCESS; // Success
    }
}
