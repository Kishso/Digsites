package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteArgumentType;
import kishso.digsites.DigsiteBookkeeper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class PlaceDigsiteMarkerCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("placeDigsiteMarker")
                .then(argument("digsiteTypeId", StringArgumentType.string())
                        .then(argument("location", BlockPosArgumentType.blockPos())
                                .executes(ctx -> run(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "digsiteTypeId"),
                                        BlockPosArgumentType.getBlockPos(ctx, "location")
                                ))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, String digsiteTypeId, BlockPos pos)
    {
        DisplayEntity.ItemDisplayEntity itemDisplayEntity =
                new DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, source.getWorld());
        itemDisplayEntity.setPos(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);

        if(source.getPlayer() != null)
        {
            ItemStack holdingItem = source.getPlayer().getMainHandStack();
            String itemId = holdingItem.getRegistryEntry().getIdAsString();

            NbtCompound itemIdNbt = new NbtCompound();
            itemIdNbt.putString("id", itemId);

            NbtCompound entityNbt = new NbtCompound();
            entityNbt = itemDisplayEntity.writeNbt(entityNbt);

            entityNbt.putString("item_display","fixed");
            entityNbt.put("item", itemIdNbt);

            DigsiteBookkeeper worldState = DigsiteBookkeeper.getWorldState(source.getWorld());
            worldState.placedDigsiteMarkers.add(itemDisplayEntity.getUuid());

            itemDisplayEntity.readNbt(entityNbt);
        }

        source.getWorld().spawnEntity(itemDisplayEntity);

        itemDisplayEntity.addCommandTag("isDigsite");
        itemDisplayEntity.addCommandTag("digsiteType:"+digsiteTypeId);

        source.sendMessage(Text.literal("Placed Digsite Marker!"));
        return Command.SINGLE_SUCCESS; // Success
    }
}
