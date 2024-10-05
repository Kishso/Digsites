package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.DigsiteType;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HighlightDigsiteCommand {

    public static final String commandName = "highlightDigsite";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal(commandName)
                .then(argument("digsite", DigsiteArgumentType.digsite())
                        .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                        .executes(ctx -> run(ctx.getSource(), DigsiteArgumentType.getDigsite(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(ServerCommandSource source, Digsite digsite)
    {
        DigsiteBookkeeper.getWorldState(source.getWorld());
        DigsiteType type = digsite.getDigsiteType();

        DisplayEntity.BlockDisplayEntity blockDisplayEntity =
                new DisplayEntity.BlockDisplayEntity(EntityType.BLOCK_DISPLAY, source.getWorld());

        BlockPos pos = digsite.getDigsiteLocation();
        Direction direction = digsite.getDigsiteDirection();
        blockDisplayEntity.setPos(
                pos.getX() + type.getXRange(direction).Lower,
                pos.getY() + type.getYRange(direction).Lower,
                pos.getZ() + type.getZRange(direction).Lower);

        if(source.getPlayer() != null)
        {
            NbtCompound itemIdNbt = new NbtCompound();

            NbtCompound entityNbt = new NbtCompound();
            entityNbt = blockDisplayEntity.writeNbt(entityNbt);

            entityNbt.getCompound("block_state").putString("Name", "minecraft:glass");
            NbtList scaleArray =
                    entityNbt.getCompound("transformation").getList("scale", NbtElement.FLOAT_TYPE);
            scaleArray.setElement(0, NbtFloat.of(type.getXRange(direction).Upper - type.getXRange(direction).Lower + 1.01f));
            scaleArray.setElement(1, NbtFloat.of(type.getYRange(direction).Upper - type.getYRange(direction).Lower + 1.01f));
            scaleArray.setElement(2, NbtFloat.of(type.getZRange(direction).Upper - type.getZRange(direction).Lower + 1.01f));

            blockDisplayEntity.readNbt(entityNbt);
        }

        source.getWorld().spawnEntity(blockDisplayEntity);
        return 0; // Failure



    }

}
