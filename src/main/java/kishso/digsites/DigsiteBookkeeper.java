package kishso.digsites;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DigsiteBookkeeper extends PersistentState {

    protected HashMap<UUID,Digsite> digsitesInWorld = new HashMap<>();
    protected HashMap<String, DigsiteType> loadedDigsiteTypes = new HashMap<>();
    public List<UUID> placedDigsiteMarkers = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound digsitesNbt = new NbtCompound();
        digsitesInWorld.forEach( (UUID id, Digsite site) ->
            digsitesNbt.put(id.toString(), site.toNbt())
        );
        nbt.put("digsitesInWorld", digsitesNbt);

        NbtCompound placedMarkersNbt = new NbtCompound();
        placedDigsiteMarkers.forEach((UUID id) -> {
                placedMarkersNbt.putUuid(id.toString(), id);
            }
        );
        nbt.put("placedDigsiteMarkers", placedMarkersNbt);


        return nbt;
    }

    public static DigsiteBookkeeper createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DigsiteBookkeeper state = new DigsiteBookkeeper();

        NbtCompound digsitesNbt = tag.getCompound("digsitesInWorld");
        if(digsitesNbt != null) {
            for (String id : digsitesNbt.getKeys()) {
                UUID uuid = UUID.fromString(id);
                state.AddDigsite(uuid, Digsite.fromNbt(tag.getCompound(id)));
            }
        }

        NbtCompound placedMarkersNbt = tag.getCompound("placedDigsiteMarkers");
        if(placedMarkersNbt != null) {
            for (String id : placedMarkersNbt.getKeys()) {
                UUID uuid = UUID.fromString(id);
                state.placedDigsiteMarkers.add(uuid);
            }
        }
        return state;
    }

    private static Type<DigsiteBookkeeper> type = new Type<>(
            DigsiteBookkeeper::new, // If there's no 'StateSaverAndLoader' yet create one
            DigsiteBookkeeper::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public void AddDigsite(UUID digsiteUUID, Digsite newDigsite)
    {
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

    public void LoadDigsiteTypes()
    {

    }
}
