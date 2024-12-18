package kishso.digsites.digsite_events;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

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

    private CompoundTag replacementBlockNbt;

    ReplaceBlockDigsiteEvent(JsonObject jsonEvent) {
        super(jsonEvent);
        JsonObject eventDetailsJson = jsonEvent.getAsJsonObject(DigsiteEvent.JsonConstants.eventsDetails);
        if(eventDetailsJson != null){
            this.replaceChance = eventDetailsJson.get(JsonConstants.replaceChance).getAsFloat();

            if(eventDetailsJson.has(JsonConstants.lookoutBlock)) {
                JsonObject lookoutBlockJson = eventDetailsJson.getAsJsonObject(JsonConstants.lookoutBlock);
                ResourceLocation lookoutBlockId =
                        ResourceLocation.parse(lookoutBlockJson.get(JsonConstants.blockId).getAsString());
                lookoutBlock = BuiltInRegistries.BLOCK.get(lookoutBlockId).get().value();

            }

            if(eventDetailsJson.has(JsonConstants.replacementBlock)){
                JsonObject replacementBlockJson = eventDetailsJson.getAsJsonObject(JsonConstants.replacementBlock);
                ResourceLocation lookoutBlockId =
                        ResourceLocation.parse(replacementBlockJson.get(JsonConstants.blockId).getAsString());
                replacementBlock = BuiltInRegistries.BLOCK.get(lookoutBlockId).get().value();

                if(replacementBlockJson.has(JsonConstants.nbtData)){
                    JsonObject nbtJson = replacementBlockJson.getAsJsonObject(JsonConstants.nbtData);
                    if(nbtJson.has(JsonConstants.nbtDataRawString)){
                        String nbtRawString = nbtJson.get(JsonConstants.nbtDataRawString).getAsString();
                        try {
                            this.replacementBlockNbt = TagParser.parseTag(nbtRawString);
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
        Level digsiteWorld = currentDigsite.getContext().getWorld();

        for (int x = (xRange.Lower); x <= xRange.Upper; x++)
        {
            for (int y = (yRange.Lower); y <= yRange.Upper; y++)
            {
                for (int z = (zRange.Lower); z <= zRange.Upper; z++)
                {
                    if(digsiteWorld.getServer() == null ||
                            !digsiteWorld.getServer().getConnection().running){
                        continue; // Skip if server is not running else getBlockState will hang
                    }

                    BlockPos targetBlock = digsiteLocation.offset(x, y, z);
                    BlockState block = digsiteWorld.getBlockState(targetBlock);
                    if (block.getBlock() == lookoutBlock  && rand.nextFloat() <= replaceChance)
                    {
                        BlockState newBlockState = replacementBlock.defaultBlockState();
                        digsiteWorld.setBlock(targetBlock, newBlockState, Block.UPDATE_ALL_IMMEDIATE);

                        if (newBlockState.hasBlockEntity())
                        {
                            BlockEntity blockEntity = digsiteWorld.getBlockEntity(targetBlock);
                            if(blockEntity != null)
                            {
                                if(replacementBlockNbt != null) {
                                    String nbtData = replacementBlockNbt.getAsString();
                                    String dataCommandStr = String.format("data merge block %d %d %d %s", targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), nbtData);

                                    if (digsiteWorld.getServer() == null) {
                                        continue; //Skip
                                    }

                                    CommandDispatcher<CommandSourceStack> commandDispatcher = digsiteWorld.getServer().getCommands().getDispatcher();
                                    if (commandDispatcher != null) {
                                        CommandSourceStack commandSource = digsiteWorld.getServer().createCommandSourceStack().withSuppressedOutput();
                                        try {
                                            commandDispatcher.execute(dataCommandStr, commandSource);
                                        } catch (CommandSyntaxException e) {
                                            // Replace Failed
                                            continue;
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
