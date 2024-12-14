package kishso.digsites;

import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static kishso.digsites.DigsitesMod.LOGGER;

public class Digsite {

    private BlockPos location;
    private UUID digsiteId;
    private DigsiteType digsiteType;
    private float digsiteYaw;
    private float digsitePitch;

    // Lazy load rotated calculations
    DigsiteType.Range<Integer> rotatedXRange = null;
    DigsiteType.Range<Integer> rotatedYRange = null;
    DigsiteType.Range<Integer> rotatedZRange = null;

    public DigsiteBookkeeper.DigsiteWorldContext currentWorldContext;

    public Digsite(BlockPos position,
                   float digsiteYaw,
                   float digsitePitch,
                   DigsiteType digsiteType)
    {
        this(position, digsiteYaw, digsitePitch, digsiteType, UUID.randomUUID());
    }

    public Digsite(BlockPos position,
                   float digsiteYaw,
                   float digsitePitch,
                   DigsiteType digsiteType,
                   UUID uuid)
    {
        this.location = position;
        this.digsiteYaw = digsiteYaw;
        this.digsitePitch = digsitePitch;
        this.digsiteId = uuid;
        this.digsiteType = digsiteType;

        Vec3 lowerVec = new Vec3(digsiteType.getXRange().Lower,digsiteType.getYRange().Lower,digsiteType.getZRange().Lower);
        float yawRadians = (float)Math.toRadians(360 - digsiteYaw);
        float pitchRadians =(float)Math.toRadians(digsitePitch);
        lowerVec = lowerVec.xRot(pitchRadians);
        lowerVec = lowerVec.yRot(yawRadians);


        Vec3 upperVec = new Vec3(digsiteType.getXRange().Upper,digsiteType.getYRange().Upper,digsiteType.getZRange().Upper);
        upperVec = upperVec.xRot(pitchRadians);
        upperVec = upperVec.yRot(yawRadians);


        rotatedXRange = new DigsiteType.Range<>((int)Math.round(Math.min(lowerVec.x, upperVec.x)), (int)Math.round(Math.max(lowerVec.x, upperVec.x)));
        rotatedYRange = new DigsiteType.Range<>((int)Math.round(Math.min(lowerVec.y, upperVec.y)), (int)Math.round(Math.max(lowerVec.y, upperVec.y)));
        rotatedZRange = new DigsiteType.Range<>((int)Math.round(Math.min(lowerVec.z, upperVec.z)), (int)Math.round(Math.max(lowerVec.z, upperVec.z)));

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

    public CompoundTag toNbt()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putIntArray("location", new int[]{location.getX(), location.getY(), location.getZ()});
        nbt.putFloat("digsiteYaw", digsiteYaw);
        nbt.putFloat("digsitePitch", digsitePitch);
        nbt.putString("digsiteType", digsiteType.getDigsiteTypeId());
        nbt.putUUID("digsiteId", digsiteId);

        return nbt;
    }

    public static Digsite fromNbt(CompoundTag nbt)
    {
        if(nbt instanceof CompoundTag root)
        {
            UUID digsiteId = root.getUUID("digsiteId");
            int[] locationCords = root.getIntArray("location");
            float yaw = root.getFloat("digsiteYaw");
            float pitch = root.getFloat("digsitePitch");

            DigsiteType type = DigsiteBookkeeper.GetDigsiteType(root.getString("digsiteType"));
            if(type == null){
                LOGGER.info("Warning: Digsite {} is missing digsite type...", digsiteId.toString());
                type = new DigsiteType(root.getString("digsiteType")); // Create placeholder type
            }

            return new Digsite(
                    new BlockPos(locationCords[0],locationCords[1],locationCords[2]), yaw, pitch, type, digsiteId);
        }
        return null;
    }

    public List<DigsiteEvent> getDigsiteEvents() {
        if(digsiteType != null) {
            return digsiteType.getDigsiteEvents();
        }
        return new ArrayList<>();
    }

    public DigsiteType.Range<Integer> getXRange()
    {
        if(rotatedXRange != null)
        {
            return rotatedXRange;
        }
        return digsiteType.getXRange();
    }

    public DigsiteType.Range<Integer> getYRange()
    {
        if(rotatedYRange != null){
            return rotatedYRange;
        }
        return digsiteType.getYRange();
    }

    public DigsiteType.Range<Integer> getZRange()
    {
        if(rotatedZRange != null){
            return rotatedZRange;
        }
        return digsiteType.getZRange();
    }
}
