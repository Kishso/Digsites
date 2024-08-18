package kishso.digsites;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;

import java.util.Random;

public class Digsite {

    private BlockPos location;

    private int xDeltaReach;
    private int yDeltaReach;
    private int zDeltaReach;

    private float convertPercentage = 0.05f;
    private int tickFrequency = 24000;
    private int currentFrequencyCount = 0;

    private RegistryKey<LootTable> lootTable;

    public Digsite(BlockPos position, int xDelta, int yDelta, int zDelta, String lootTableIdString)
    {
        this.location = position;
        this.xDeltaReach = xDelta;
        this.yDeltaReach = yDelta;
        this.zDeltaReach = zDelta;

        Identifier lootTableId = Identifier.tryParse(lootTableIdString);
        if(lootTableId != null)
        {
            lootTable = RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId);
        }

    }

    public Digsite()
    {

    }

    public NbtElement toNbt()
    {
        NbtCompound nbt = new NbtCompound();

        nbt.putIntArray("location", new int[]{location.getX(), location.getY(), location.getZ()});
        nbt.putIntArray("deltaReach", new int[]{xDeltaReach, yDeltaReach, zDeltaReach});
        nbt.putString("lootTable", lootTable.getValue().toString());

        return nbt;
    }

    public static Digsite fromNbt(NbtElement nbt)
    {
        if(nbt instanceof NbtCompound)
        {
            NbtCompound root = (NbtCompound)nbt;

            int[] locationCoords = root.getIntArray("location");
            int[] deltaReachList = root.getIntArray("deltaReach");
            String lootTableString = root.getString("lootTable");

            return new Digsite(
                    new BlockPos(locationCoords[0],locationCoords[1],locationCoords[2]),
                    deltaReachList[0], deltaReachList[1],deltaReachList[2],
                    lootTableString);
        }
        return null;
    }

    public int triggerDigsite(World world)
    {
        int numBlocksReplaced = 0;
        Random rand = new Random();

        if(lootTable != null)
        {
            for (int x = (-1 * xDeltaReach); x < xDeltaReach; x++)
            {
                for (int y = (-1 * yDeltaReach); y < yDeltaReach; y++)
                {
                    for (int z = (-1 * zDeltaReach); z < zDeltaReach; z++)
                    {
                        BlockPos targetBlock = location.add(x, y, z);
                        BlockState block = world.getBlockState(targetBlock);
                        if (block.isOf(Blocks.GRAVEL) && rand.nextFloat() <= convertPercentage)
                        {
                            BlockState newBlockState = Blocks.SUSPICIOUS_GRAVEL.getDefaultState();
                            BrushableBlock newBlock = (BrushableBlock) newBlockState.getBlock();

                            world.setBlockState(targetBlock, newBlockState);

                            if (newBlockState.hasBlockEntity())
                            {
                                BrushableBlockEntity blockEntity = (BrushableBlockEntity)world.getBlockEntity(targetBlock);
                                if(blockEntity != null)
                                {
                                    blockEntity.setLootTable(lootTable, rand.nextLong());
                                }
                            }

                            numBlocksReplaced++;
                        }
                    }
                }
            }
        }

        return numBlocksReplaced;
    }


}
