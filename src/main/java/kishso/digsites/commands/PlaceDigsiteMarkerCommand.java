package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import kishso.digsites.commands.argtypes.DigsiteTypeArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class PlaceDigsiteMarkerCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("placeDigsiteMarker")
                .then(argument("digsiteTypeId", DigsiteTypeArgumentType.digsiteType())
                        .suggests(new DigsiteTypeArgumentType.DigsiteTypeArgSuggestionProvider())
                        .then(argument("location", BlockPosArgumentType.blockPos())
                                .executes(ctx -> run(ctx.getSource(),
                                        DigsiteTypeArgumentType.getDigsiteType(ctx, "digsiteTypeId"),
                                        BlockPosArgumentType.getBlockPos(ctx, "location")
                                ))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, DigsiteType digsiteType, BlockPos pos)
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
        itemDisplayEntity.addCommandTag("digsiteType:"+digsiteType.getDigsiteTypeId());

        source.sendMessage(Text.literal("Placed Digsite Marker!"));
        return Command.SINGLE_SUCCESS; // Success
    }
}
