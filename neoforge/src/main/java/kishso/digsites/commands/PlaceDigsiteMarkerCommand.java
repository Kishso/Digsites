package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import kishso.digsites.commands.argtypes.DigsiteTypeArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Display;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class PlaceDigsiteMarkerCommand extends CrossPlatformCommand{

    public static final String commandName = "placeDigsiteMarker";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(literal(commandName)
                .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                .then(argument("digsiteTypeId", StringArgumentType.string())
                        .suggests(new DigsiteTypeArgumentType.DigsiteTypeArgSuggestionProvider())
                        .then(argument("location", BlockPosArgument.blockPos())
                                .executes(ctx -> run(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "digsiteTypeId"),
                                        BlockPosArgument.getBlockPos(ctx, "location")
                                ))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(CommandSourceStack source, String digsiteTypeId, BlockPos pos)
    {
        DigsiteBookkeeper.getWorldState(source.getLevel());

        DigsiteType digsiteType = DigsiteBookkeeper.GetDigsiteType(digsiteTypeId);
        if(digsiteType == null)
        {
            digsiteType = new DigsiteType(digsiteTypeId);
        }

        Display.ItemDisplay itemDisplayEntity =
                new Display.ItemDisplay(EntityType.ITEM_DISPLAY, source.getLevel());
        itemDisplayEntity.setPos(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);

        if(source.getPlayer() != null)
        {
            ItemStack holdingItem = source.getPlayer().getItemInHand(InteractionHand.MAIN_HAND);
            String itemId = holdingItem.getItem().toString();

            CompoundTag itemIdNbt = new CompoundTag();
            itemIdNbt.putString("id", itemId);

            CompoundTag entityNbt = new CompoundTag();
            itemDisplayEntity.save(entityNbt);

            entityNbt.putString("item_display","fixed");
            entityNbt.put("item", itemIdNbt);

            DigsiteBookkeeper.placedDigsiteMarkers.add(itemDisplayEntity.getUUID());

            itemDisplayEntity.load(entityNbt);
        }

        source.getLevel().addFreshEntity(itemDisplayEntity);

        itemDisplayEntity.addTag("isDigsite");
        itemDisplayEntity.addTag("digsiteType:"+digsiteType.getDigsiteTypeId());

        source.sendSystemMessage(Component.literal("Placed Digsite Marker!"));
        return Command.SINGLE_SUCCESS; // Success
    }
}
