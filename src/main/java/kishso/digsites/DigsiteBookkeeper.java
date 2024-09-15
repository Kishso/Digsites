package kishso.digsites;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.UUID;

public class DigsiteBookkeeper extends PersistentState {

    protected HashMap<UUID,Digsite> digsitesInWorld = new HashMap<>();
    protected HashMap<String, DigsiteArgumentType> loadedDigsiteTypes= new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        digsitesInWorld.forEach( (UUID id, Digsite site) ->
            nbt.put(id.toString(), site.toNbt())
        );

        return nbt;
    }

    public static DigsiteBookkeeper createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DigsiteBookkeeper state = new DigsiteBookkeeper();
        for(String id : tag.getKeys())
        {
            UUID uuid = UUID.fromString(id);
            state.AddDigsite(uuid,Digsite.fromNbt(tag.getCompound(id)));
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
