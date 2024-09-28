package kishso.digsites;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

import static kishso.digsites.Digsites.LOGGER;

public class Digsite {

    private BlockPos location;
    private UUID digsiteId;
    private DigsiteType digsiteType;
    private RegistryKey<LootTable> lootTable;

    public Digsite(BlockPos position,
                   DigsiteType digsiteType)
    {
        new Digsite(position, digsiteType, UUID.randomUUID());
    }

    public Digsite(BlockPos position,
                   DigsiteType digsiteType,
                   UUID uuid)
    {
        this.location = position;
        this.digsiteId = uuid;
        this.digsiteType = digsiteType;

        Identifier lootTableId = Identifier.tryParse(digsiteType.getLootTableString());
        if(lootTableId != null)
        {
            lootTable = RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId);
        }

    }

    public UUID getDigsiteId()
    {
        return digsiteId;
    }

    public NbtElement toNbt()
    {
        NbtCompound nbt = new NbtCompound();

        nbt.putIntArray("location", new int[]{location.getX(), location.getY(), location.getZ()});
        nbt.putString("digsiteType", digsiteType.getDigsiteTypeId());
        nbt.putUuid("digsiteId", digsiteId);

        return nbt;
    }

    public static Digsite fromNbt(NbtElement nbt)
    {
        if(nbt instanceof NbtCompound root)
        {
            UUID digsiteId = root.getUuid("digsiteId");
            int[] locationCoords = root.getIntArray("location");
            DigsiteType type = DigsiteBookkeeper.GetDigsiteType(root.getString("digsiteType"));

            if(type == null){
                LOGGER.info("Warning: Digsite {} is missing digsite type...", digsiteId.toString());
            }

            return new Digsite(
                    new BlockPos(locationCoords[0],locationCoords[1],locationCoords[2]),
                    type, digsiteId);
        }
        return null;
    }

    public DigsiteType getDigsiteType(){
        return digsiteType;
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
                            world.setBlockState(targetBlock, newBlockState);

                            if (newBlockState.hasBlockEntity())
                            {
                                BrushableBlockEntity blockEntity = (BrushableBlockEntity) world.getBlockEntity(targetBlock);
                                if(blockEntity != null)
                                {
                                    String nbtData = String.format("{LootTable:\"%s\"}", lootTable.getValue().toString());
                                    String dataCommand = String.format("data merge block %d %d %d %s", targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), nbtData);
                                    world.getServer().getCommandManager().executeWithPrefix(world.getServer().getCommandSource(), dataCommand);
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
