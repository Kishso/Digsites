package kishso.digsites;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class UpdateNearbyDigsites {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("UpdateNearbyDigsites")
                .then(argument("lootTable", StringArgumentType.string())
                        .then(argument("chance", FloatArgumentType.floatArg(0.0f,1.0f))
                                .executes(ctx -> run(ctx.getSource(), StringArgumentType.getString(ctx, "lootTable"), FloatArgumentType.getFloat(ctx, "chance")))))); // You can deal with the arguments out here and pipe them into the command.
    }

    public static int run(ServerCommandSource source, String lootTable, float chance)
    {
        Vec3d origPos = source.getPosition();
        ServerWorld world = source.getWorld();

        int radius = 6;
        int numBlocksReplaced = 0;
        int numBlocksChecked = 0;
        BlockPos centerPos = new BlockPos((int)origPos.getX(), (int)origPos.getY(), (int)origPos.getZ());

        Random rand = new Random();
        Identifier lootTableId = Identifier.tryParse(lootTable);

        if(lootTableId != null) {
            for (int x = (-1 * radius); x < radius; x++) {
                for (int y = (-1 * radius); y < radius; y++) {
                    for (int z = (-1 * radius); z < radius; z++) {
                        numBlocksChecked++;
                        BlockPos targetBlock = centerPos.add(x, y, z);
                        BlockState block = world.getBlockState(targetBlock);
                        if (block.isOf(Blocks.GRAVEL) && rand.nextFloat() <= chance) {
                            BlockState newBlockState = Blocks.SUSPICIOUS_GRAVEL.getDefaultState();
                            BrushableBlock newBlock = (BrushableBlock) newBlockState.getBlock();

                            world.setBlockState(targetBlock, newBlockState);

                            if (newBlockState.hasBlockEntity()) {
                                BrushableBlockEntity blockEntity = (BrushableBlockEntity) world.getBlockEntity(targetBlock);
                                blockEntity.setLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId), rand.nextLong());
                            }

                            numBlocksReplaced++;
                        }
                    }
                }
            }
        }
        source.sendMessage(Text.literal(String.format("Replacing %d block(s) out of %d block(s)", numBlocksReplaced, numBlocksChecked)));
        return Command.SINGLE_SUCCESS; // Success
    }
}