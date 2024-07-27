package kishso.digsites;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class DigsiteBookkeeper extends PersistentState {

    protected HashMap<UUID,Digsite> digsitesInWorld = new HashMap<UUID,Digsite>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        digsitesInWorld.forEach( (UUID id, Digsite site) ->
        {
            nbt.put(id.toString(), site.toNbt());
        });

        return nbt;
    }

    public static DigsiteBookkeeper createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DigsiteBookkeeper state = new DigsiteBookkeeper();
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

    public Digsite GetDigsite(UUID digsiteUUID)
    {
        return this.digsitesInWorld.getOrDefault(digsiteUUID, null);
    }

    public static DigsiteBookkeeper getServerState(MinecraftServer server)
    {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        DigsiteBookkeeper digsitesState = persistentStateManager.getOrCreate(type, Digsites.MOD_ID);

        digsitesState.markDirty();

        return digsitesState;
    }
}
