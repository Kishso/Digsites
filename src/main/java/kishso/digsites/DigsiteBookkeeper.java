package kishso.digsites;

import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.*;

import static kishso.digsites.Digsites.LOGGER;

public class DigsiteBookkeeper extends PersistentState {

    protected static final HashMap<String, DigsiteType> loadedDigsiteTypes = new HashMap<>();
    public static final List<UUID> placedDigsiteMarkers = new ArrayList<>();

    protected static final HashMap<String, DigsiteWorldContext> globalDigsiteRecord = new HashMap<>();
    protected DigsiteWorldContext currentWorld;

    public static class DigsiteWorldContext {
        private final HashMap<UUID,Digsite> digsites = new HashMap<>();
        protected String worldId;
        protected World world;

        DigsiteWorldContext(String worldId){
            this.worldId = worldId;
        }

        public World getWorld(){
            return world;
        }

        void addDigsite(UUID uuid, Digsite digsite){
            digsite.setContext(this);
            digsites.put(uuid, digsite);
        }

         Digsite getDigsite(UUID uuid){
            if(digsites.containsKey(uuid)){
                return digsites.get(uuid);
            }
            return null;
        }
    }

    public void addDigsite(UUID digsiteUUID, Digsite newDigsite)
    {
        LOGGER.info("Added Digsite [{}]", digsiteUUID.toString());
        newDigsite.setContext(currentWorld);
        currentWorld.digsites.put(digsiteUUID, newDigsite);
    }

    public boolean removeDigsite(UUID digsiteUUID)
    {
        return currentWorld.digsites.remove(digsiteUUID) != null;
    }

    public Set<UUID> getCurrentDigsites(){
        return currentWorld.digsites.keySet();
    }

    public void UpdateDigsitesInWorld(World world)
    {
        currentWorld.digsites.forEach((uuid, digsite) -> {
            DigsiteType type = digsite.getDigsiteType();
            if (type != null) {
                for (DigsiteEvent event : type.getDigsiteEvents()) {
                    if (event.checkTick(world.getTime()) && event.isConditionsMet(digsite)) {
                        event.run(digsite);
                        LOGGER.info("Running Event [{}] at Digsite [{}]", event.getEventName(), uuid.toString());
                    }
                }
            }
        });

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound digsitesNbt = new NbtCompound();
        currentWorld.digsites.forEach( (UUID id, Digsite site) ->
                digsitesNbt.put(id.toString(), site.toNbt())
        );
        nbt.put("digsitesInWorld", digsitesNbt);

        NbtCompound placedMarkersNbt = new NbtCompound();
        placedDigsiteMarkers.forEach((UUID id) ->
                placedMarkersNbt.putUuid(id.toString(), id));
        nbt.put("placedDigsiteMarkers", placedMarkersNbt);

        nbt.putString("currentWorldId", currentWorld.worldId);

        return nbt;
    }

    public static DigsiteBookkeeper createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DigsiteBookkeeper state = new DigsiteBookkeeper();

        DigsiteWorldContext digsitesInWorld = null;
        if(tag.contains("currentWorldId")){
            String worldId = tag.getString("currentWorldId");
            if(globalDigsiteRecord.containsKey(worldId)){
                digsitesInWorld = globalDigsiteRecord.get(worldId);
            }
            else {
                digsitesInWorld = new DigsiteWorldContext(worldId);
                globalDigsiteRecord.put(worldId, digsitesInWorld);
            }
        }

        if(digsitesInWorld == null){
            return state;
        }

        NbtCompound digsitesNbt = tag.getCompound("digsitesInWorld");
        if(digsitesNbt != null) {
            for (String id : digsitesNbt.getKeys()) {
                UUID uuid = UUID.fromString(id);
                NbtElement digsiteNbt = digsitesNbt.get(id);
                if(digsiteNbt != null) {
                    digsitesInWorld.addDigsite(uuid, Digsite.fromNbt(digsiteNbt));
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
                placedDigsiteMarkers.add(uuid);
            }
        }

        return state;
    }

    private static final Type<DigsiteBookkeeper> type = new Type<>(
            DigsiteBookkeeper::new, // If there's no 'StateSaverAndLoader' yet create one
            DigsiteBookkeeper::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );



    public static DigsiteBookkeeper getWorldState(ServerWorld world)
    {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        DigsiteBookkeeper digsitesState = persistentStateManager.getOrCreate(type, Digsites.MOD_ID);

        String worldId = world.getRegistryKey().getValue().toString();
        if(globalDigsiteRecord.containsKey(worldId)){
            digsitesState.currentWorld = globalDigsiteRecord.get(worldId);
        }
        else {
            digsitesState.currentWorld = new DigsiteWorldContext(worldId);
            globalDigsiteRecord.put(digsitesState.currentWorld.worldId, digsitesState.currentWorld);
            digsitesState.currentWorld.world = world;
        }

        digsitesState.markDirty();
        return digsitesState;
    }

    public static void LoadDigsiteTypes(String digsiteId, DigsiteType type)
    {
        loadedDigsiteTypes.put(digsiteId, type);
    }

    public static Collection<DigsiteType> GetAllLoadedDigsiteType()
    {
        return loadedDigsiteTypes.values();
    }

    public static DigsiteType GetDigsiteType(String digsiteId)
    {
        if(loadedDigsiteTypes.containsKey(digsiteId)){
            return loadedDigsiteTypes.get(digsiteId);
        }
        return null;
    }

    public static Digsite searchForDigsite(UUID uuid){
        for(DigsiteWorldContext worldContext : globalDigsiteRecord.values()){
            if(worldContext.digsites.containsKey(uuid)){
                return worldContext.getDigsite(uuid);
            }
        }
        return null;
    }


}
