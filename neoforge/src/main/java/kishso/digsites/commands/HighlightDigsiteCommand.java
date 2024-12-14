package kishso.digsites.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.Digsite;
import kishso.digsites.DigsiteBookkeeper;
import kishso.digsites.commands.argtypes.DigsiteArgumentType;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Display;

import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HighlightDigsiteCommand extends CrossPlatformCommand{

    public static final String commandName = "highlightDigsite";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(literal(commandName)
                .requires(serverCommandSource -> ((CommandSourceStack)serverCommandSource).hasPermission(2))
                .then(argument("digsite", UuidArgument.uuid())
                        .suggests(new DigsiteArgumentType.DigsiteArgSuggestionProvider())
                        .executes(ctx -> run(ctx.getSource(), UuidArgument.getUuid(ctx, "digsite"))))); // You can deal with the arguments out here and pipe them into the command.

    }

    public static int run(CommandSourceStack source, UUID digsiteId)
    {
        DigsiteBookkeeper.getWorldState(source.getLevel());

        Digsite digsite = DigsiteBookkeeper.searchForDigsite(digsiteId);
        if(digsite == null)
        {
            return 0;
        }

        Display.BlockDisplay blockDisplayEntity =
                new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, source.getLevel());

        BlockPos pos = digsite.getDigsiteLocation();
        blockDisplayEntity.setPos(
                pos.getX() + digsite.getXRange().Lower,
                pos.getY() + digsite.getYRange().Lower,
                pos.getZ() + digsite.getZRange().Lower);

        if(source.getPlayer() != null)
        {
            CompoundTag entityNbt = new CompoundTag();
            blockDisplayEntity.save(entityNbt);

            entityNbt.getCompound("block_state").putString("Name", "minecraft:glass");
            ListTag scaleArray =
                    entityNbt.getCompound("transformation").getList("scale", Tag.TAG_FLOAT);
            scaleArray.set(0, FloatTag.valueOf(digsite.getXRange().Upper - digsite.getXRange().Lower + 1.01f));
            scaleArray.set(1, FloatTag.valueOf(digsite.getYRange().Upper - digsite.getYRange().Lower + 1.01f));
            scaleArray.set(2, FloatTag.valueOf(digsite.getZRange().Upper - digsite.getZRange().Lower + 1.01f));

            blockDisplayEntity.load(entityNbt);
        }

        // source.getLevel().spawnEntity(blockDisplayEntity);
        source.getLevel().getLevel().addFreshEntity(blockDisplayEntity);
        return Command.SINGLE_SUCCESS;



    }

}
