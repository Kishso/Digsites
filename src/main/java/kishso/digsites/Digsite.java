package kishso.digsites;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kishso.digsites.digsite_events.DigsiteEvent;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
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

    public DigsiteBookkeeper.DigsiteWorldContext currentWorldContext;

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

    }

    public UUID getDigsiteId()
    {
        return digsiteId;
    }

    public BlockPos getDigsiteLocation() {
        return location;
    }

    public void setContext (DigsiteBookkeeper.DigsiteWorldContext context){
        this.currentWorldContext = context;
    }

    public DigsiteBookkeeper.DigsiteWorldContext getContext(){
        return this.currentWorldContext;
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
                    new BlockPos(locationCoords[0],locationCoords[1],locationCoords[2]), type, digsiteId);
        }
        return null;
    }

    public DigsiteType getDigsiteType(){
        return digsiteType;
    }

    public void runEvents(World world){
        for(DigsiteEvent event : digsiteType.getDigsiteEvents()){
            if(event.checkTick(world.getLevelProperties().getTime())
                    && event.isConditionsMet(this)){
                event.run(this);
            }
        }
    }
}
