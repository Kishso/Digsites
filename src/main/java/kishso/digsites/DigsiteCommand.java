package kishso.digsites;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.data.server.loottable.vanilla.VanillaLootTableProviders;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.server.command.LootCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.feature.SimpleRandomFeature;


import java.util.Random;

import static net.minecraft.server.command.CommandManager.literal;

public final class DigsiteCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("triggerDigsiteUpdate")
                                .executes(ctx -> run(ctx.getSource()))); // You can deal with the arguments out here and pipe them into the command.
    }

    public static int run(ServerCommandSource source)
    {
        Vec3d origPos = source.getPosition();
        ServerWorld world = source.getWorld();

        int radius = 6;
        int numBlocksReplaced = 0;
        int numBlocksChecked = 0;
        BlockPos centerPos = new BlockPos((int)origPos.getX(), (int)origPos.getY(), (int)origPos.getZ());

        Random rand = new Random();

        for(int x = (-1*radius); x < radius; x++)
        {
            for(int y = (-1*radius); y < radius; y++)
            {
                for(int z = (-1*radius); z < radius; z++)
                {
                    numBlocksChecked++;
                    BlockPos targetBlock = centerPos.add(x,y,z);
                    BlockState block = world.getBlockState(targetBlock);
                    if(block.isOf(Blocks.GRAVEL))
                    {
                        BlockState newBlockState = Blocks.SUSPICIOUS_GRAVEL.getDefaultState();
                        BrushableBlock newBlock = (BrushableBlock) newBlockState.getBlock();

                        world.setBlockState(targetBlock, newBlockState);

                        if(newBlockState.hasBlockEntity())
                        {
                            BrushableBlockEntity blockEntity = (BrushableBlockEntity)world.getBlockEntity(targetBlock);
                            blockEntity.setLootTable(LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY, rand.nextLong());
                        }

                        numBlocksReplaced++;
                    }
                }
            }
        }
        source.sendMessage(Text.literal(String.format("Replacing %d block(s) out of %d block(s)", numBlocksReplaced, numBlocksChecked)));
        return Command.SINGLE_SUCCESS; // Success
    }
}
