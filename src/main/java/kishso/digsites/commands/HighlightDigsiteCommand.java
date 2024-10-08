package kishso.digsites.commands;

import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

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

        DisplayEntity.BlockDisplayEntity blockDisplayEntity =
                new DisplayEntity.BlockDisplayEntity(EntityType.BLOCK_DISPLAY, source.getWorld());

        BlockPos pos = digsite.getDigsiteLocation();
        blockDisplayEntity.setPos(
                pos.getX() + digsite.getXRange().Lower,
                pos.getY() + digsite.getYRange().Lower,
                pos.getZ() + digsite.getZRange().Lower);

        if(source.getPlayer() != null)
        {
            NbtCompound entityNbt = new NbtCompound();
            entityNbt = blockDisplayEntity.writeNbt(entityNbt);

            entityNbt.getCompound("block_state").putString("Name", "minecraft:glass");
            NbtList scaleArray =
                    entityNbt.getCompound("transformation").getList("scale", NbtElement.FLOAT_TYPE);
            scaleArray.setElement(0, NbtFloat.of(digsite.getXRange().Upper - digsite.getXRange().Lower + 1.01f));
            scaleArray.setElement(1, NbtFloat.of(digsite.getYRange().Upper - digsite.getYRange().Lower + 1.01f));
            scaleArray.setElement(2, NbtFloat.of(digsite.getZRange().Upper - digsite.getZRange().Lower + 1.01f));

            blockDisplayEntity.readNbt(entityNbt);
        }

        source.getWorld().spawnEntity(blockDisplayEntity);
        return 0; // Failure



    }

}