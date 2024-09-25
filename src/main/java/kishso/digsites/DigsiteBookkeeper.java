package kishso.digsites;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static kishso.digsites.Digsites.LOGGER;

public class DigsiteBookkeeper extends PersistentState {

    protected HashMap<UUID,Digsite> digsitesInWorld = new HashMap<>();

    protected static HashMap<String, DigsiteType> loadedDigsiteTypes = new HashMap<>();
    public List<UUID> placedDigsiteMarkers = new ArrayList<>();

    protected static int tickCount = 0;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound digsitesNbt = new NbtCompound();
        digsitesInWorld.forEach( (UUID id, Digsite site) ->
            digsitesNbt.put(id.toString(), site.toNbt())
        );
        nbt.put("digsitesInWorld", digsitesNbt);

        NbtCompound placedMarkersNbt = new NbtCompound();
        placedDigsiteMarkers.forEach((UUID id) ->
                placedMarkersNbt.putUuid(id.toString(), id));
        nbt.put("placedDigsiteMarkers", placedMarkersNbt);

        nbt.putInt("currentTickCount", tickCount);

        return nbt;
    }

    public static DigsiteBookkeeper createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DigsiteBookkeeper state = new DigsiteBookkeeper();

        NbtCompound digsitesNbt = tag.getCompound("digsitesInWorld");
        if(digsitesNbt != null) {
            for (String id : digsitesNbt.getKeys()) {
                UUID uuid = UUID.fromString(id);
                NbtElement digsiteNbt = digsitesNbt.get(id);
                if(digsiteNbt != null) {
                    state.AddDigsite(uuid, Digsite.fromNbt(digsiteNbt));
                }
                else {
                    LOGGER.info("Digsite was null!");
                }
            }
        }

        NbtCompound placedMarkersNbt = tag.getCompound("placedDigsiteMarkers");
        if(placedMarkersNbt != null) {
            for (String id : placedMarkersNbt.getKeys()) {
                UUID uuid = UUID.fromString(id);
                state.placedDigsiteMarkers.add(uuid);
            }
        }

        tickCount =  tag.getInt("currentTickCount");

        return state;
    }

    private static final Type<DigsiteBookkeeper> type = new Type<>(
            DigsiteBookkeeper::new, // If there's no 'StateSaverAndLoader' yet create one
            DigsiteBookkeeper::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public void AddDigsite(UUID digsiteUUID, Digsite newDigsite)
    {
        LOGGER.info("Added Digsite [{}]", digsiteUUID.toString());
        this.digsitesInWorld.put(digsiteUUID, newDigsite);
    }

    public boolean RemoveDigsite(UUID digsiteUUID)
    {
        return digsitesInWorld.remove(digsiteUUID) != null;
    }

    public Digsite GetDigsite(UUID digsiteUUID)
    {
        return this.digsitesInWorld.getOrDefault(digsiteUUID, null);
    }

    public static DigsiteBookkeeper getWorldState(ServerWorld world)
    {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();

        DigsiteBookkeeper digsitesState = persistentStateManager.getOrCreate(type, Digsites.MOD_ID);

        digsitesState.markDirty();

        return digsitesState;
    }

    public static void LoadDigsiteTypes(String digsiteId, DigsiteType type)
    {
        loadedDigsiteTypes.put(digsiteId, type);
    }

//    public static DigsiteType GetDigsiteType(String digsiteId)
//    {
//        if(loadedDigsiteTypes.containsKey(digsiteId)){
//            return loadedDigsiteTypes.get(digsiteId);
//        }
//        return null;
//    }

    public void UpdateTickDigsitesInWorld(ServerWorld world)
    {
        tickCount++;
        digsitesInWorld.forEach((uuid, digsite) -> {
            DigsiteType type = digsite.getDigsiteType();
            if(tickCount % type.getTickFrequency() == 0){
                LOGGER.info("Triggering Digsite [{}]", uuid.toString());
                digsite.triggerDigsite(world);
            }
        });
    }
}
