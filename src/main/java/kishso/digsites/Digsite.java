package kishso.digsites;

import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.UUID;

import static kishso.digsites.Digsites.LOGGER;

public class Digsite {

    private BlockPos location;
    private UUID digsiteId;
    private DigsiteType digsiteType;
    private Direction direction;

    public DigsiteBookkeeper.DigsiteWorldContext currentWorldContext;

    public Digsite(BlockPos position,
                   Direction facingDirection,
                   DigsiteType digsiteType)
    {
        new Digsite(position, facingDirection, digsiteType, UUID.randomUUID());
    }

    public Digsite(BlockPos position,
                   Direction facingDirection,
                   DigsiteType digsiteType,
                   UUID uuid)
    {
        this.location = position;
        this.direction = facingDirection;
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

    public Direction getDigsiteDirection() {
        return direction;
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
        nbt.putInt("digsiteDirection", direction.getId());

        return nbt;
    }

    public static Digsite fromNbt(NbtElement nbt)
    {
        if(nbt instanceof NbtCompound root)
        {
            UUID digsiteId = root.getUuid("digsiteId");
            int[] locationCoords = root.getIntArray("location");
            Direction direction = Direction.byId(root.getInt("digsiteDirection"));

            DigsiteType type = DigsiteBookkeeper.GetDigsiteType(root.getString("digsiteType"));
            if(type == null){
                LOGGER.info("Warning: Digsite {} is missing digsite type...", digsiteId.toString());
            }

            return new Digsite(
                    new BlockPos(locationCoords[0],locationCoords[1],locationCoords[2]), direction, type, digsiteId);
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
