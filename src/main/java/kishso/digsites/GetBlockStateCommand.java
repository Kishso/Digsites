package kishso.digsites;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class GetBlockStateCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("getBlockState")
                        .then(argument("blockPos", BlockPosArgumentType.blockPos())
                        .executes(ctx -> run(ctx.getSource(),
                            BlockPosArgumentType.getBlockPos(ctx, "blockPos")
                        )))
        ); // You can deal with the arguments out here and pipe them into the command.
    }

    public static int run(ServerCommandSource source, BlockPos targetBlockPos)
    {

        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = source.getWorld();

        if (player != null)
        {
            BlockState targetBlock = world.getBlockState(targetBlockPos);
            Block block = targetBlock.getBlock();
            player.sendMessage(Text.literal(String.format("Block Id: %s", targetBlock.getRegistryEntry().getIdAsString())));

            player.sendMessage(Text.literal(String.format("Loot Table Key: %s", block.getLootTableKey())));



            if(block instanceof BrushableBlock brushableBlock)
            {
                BlockEntity blockEntity = world.getBlockEntity(targetBlockPos);
                player.sendMessage(Text.literal(String.format("Loot Table : %s", brushableBlock.getSettings().toString())));
            }

            for(Property property :targetBlock.getProperties())
            {
                player.sendMessage(Text.literal(String.format("Property %s: %s", property.getName(), property.getValues().toString())));
            }
        }
        return Command.SINGLE_SUCCESS; // Success
    }
}
