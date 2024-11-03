package kishso.digsites.digsite_events;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ReplaceBlockDigsiteEvent extends DigsiteEvent{

    public static class JsonConstants {
        final static String replaceChance = "replace_chance";
        final static String lookoutBlock = "is_block";
        final static String replacementBlock = "replace_block_with";
        final static String blockId = "id";
        final static String nbtData = "nbt_data";

        final static String nbtDataRawString = "raw_string";
    }

    private float replaceChance = 0.0f;

    private Block lookoutBlock;
    private Block replacementBlock;

    private NbtCompound replacementBlockNbt;

    ReplaceBlockDigsiteEvent(JsonObject jsonEvent) {
        super(jsonEvent);
        JsonObject eventDetailsJson = jsonEvent.getAsJsonObject(DigsiteEvent.JsonConstants.eventsDetails);
        if(eventDetailsJson != null){
            this.replaceChance = eventDetailsJson.get(JsonConstants.replaceChance).getAsFloat();

            if(eventDetailsJson.has(JsonConstants.lookoutBlock)){
                JsonObject lookoutBlockJson = eventDetailsJson.getAsJsonObject(JsonConstants.lookoutBlock);
                Identifier lookoutBlockId =
                        Identifier.tryParse(lookoutBlockJson.get(JsonConstants.blockId).getAsString());
                lookoutBlock = Registries.BLOCK.get(lookoutBlockId);
            }

            if(eventDetailsJson.has(JsonConstants.replacementBlock)){
                JsonObject replacementBlockJson = eventDetailsJson.getAsJsonObject(JsonConstants.replacementBlock);
                Identifier replacementBlockId =
                        Identifier.tryParse(replacementBlockJson.get(JsonConstants.blockId).getAsString());
                replacementBlock = Registries.BLOCK.get(replacementBlockId);

                if(replacementBlockJson.has(JsonConstants.nbtData)){
                    JsonObject nbtJson = replacementBlockJson.getAsJsonObject(JsonConstants.nbtData);
                    if(nbtJson.has(JsonConstants.nbtDataRawString)){
                        String nbtRawString = nbtJson.get(JsonConstants.nbtDataRawString).getAsString();
                        try {
                            this.replacementBlockNbt = StringNbtReader.parse(nbtRawString);
                        } catch (CommandSyntaxException e) {
                            this.replacementBlockNbt = null;
                        }
                    }
                }
            }
        }


    }

    @Override
    public boolean isConditionsMet(Digsite currentDigsite) {
        return true;
    }

    @Override
    public void run(Digsite currentDigsite) {
        Random rand = new Random();

        DigsiteType.Range<Integer> xRange = currentDigsite.getXRange();
        DigsiteType.Range<Integer> yRange = currentDigsite.getYRange();
        DigsiteType.Range<Integer> zRange = currentDigsite.getZRange();

        BlockPos digsiteLocation = currentDigsite.getDigsiteLocation();
        World digsiteWorld = currentDigsite.getContext().getWorld();

        for (int x = (xRange.Lower); x <= xRange.Upper; x++)
        {
            for (int y = (yRange.Lower); y <= yRange.Upper; y++)
            {
                for (int z = (zRange.Lower); z <= zRange.Upper; z++)
                {
                    BlockPos targetBlock = digsiteLocation.add(x, y, z);
                    BlockState block = digsiteWorld.getBlockState(targetBlock);
                    if (block.isOf(lookoutBlock) && rand.nextFloat() <= replaceChance)
                    {
                        BlockState newBlockState = replacementBlock.getDefaultState();
                        digsiteWorld.setBlockState(targetBlock, newBlockState);

                        if (newBlockState.hasBlockEntity())
                        {
                            BlockEntity blockEntity = digsiteWorld.getBlockEntity(targetBlock);
                            if(blockEntity != null)
                            {
                                String nbtData = replacementBlockNbt.asString();
                                String dataCommandStr = String.format("data merge block %d %d %d %s", targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), nbtData);

                                if(digsiteWorld.getServer() == null){
                                    continue; //Skip
                                }
                                CommandManager commandManager = digsiteWorld.getServer().getCommandManager();
                                if(commandManager != null){
                                    commandManager.executeWithPrefix(digsiteWorld.getServer().getCommandSource().withSilent(), dataCommandStr);

                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
