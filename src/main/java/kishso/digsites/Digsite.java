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
    private int currentFrequencyCount = 0;

    private DigsiteType digsiteType;
    private RegistryKey<LootTable> lootTable;

    public Digsite(BlockPos position,
                   DigsiteType digsiteType)
    {
        this.location = position;

        this.digsiteType = digsiteType;

        Identifier lootTableId = Identifier.tryParse(digsiteType.getLootTableString());
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
        nbt.put("digsiteType", digsiteType.toNbt());

        return nbt;
    }

    public static Digsite fromNbt(NbtElement nbt)
    {
        if(nbt instanceof NbtCompound)
        {
            NbtCompound root = (NbtCompound)nbt;

            int[] locationCoords = root.getIntArray("location");
            DigsiteType type = DigsiteType.fromNbt(root.getCompound("digsiteType"));

            return new Digsite(
                    new BlockPos(locationCoords[0],locationCoords[1],locationCoords[2]),
                    type);
        }
        return null;
    }

    public int triggerDigsite(World world)
    {
        int numBlocksReplaced = 0;
        Random rand = new Random();

        DigsiteType.Range<Integer> xRange = digsiteType.getXRange();
        DigsiteType.Range<Integer> yRange = digsiteType.getYRange();
        DigsiteType.Range<Integer> zRange = digsiteType.getZRange();

        float convertPercentage = digsiteType.getConvertPercentage();

        if(lootTable != null)
        {
            for (int x = (xRange.Lower); x < xRange.Upper; x++)
            {
                for (int y = (yRange.Lower); y < yRange.Upper; y++)
                {
                    for (int z = (zRange.Lower); z < zRange.Upper; z++)
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
