package kishso.digsites;

import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static kishso.digsites.DigsitesMod.LOGGER;

public class DigsiteBookkeeper extends SavedData {

    protected static final HashMap<String, DigsiteType> loadedDigsiteTypes = new HashMap<>();
    public static final List<UUID> placedDigsiteMarkers = new ArrayList<>();

    protected static final HashMap<String, DigsiteWorldContext> globalDigsiteRecord = new HashMap<>();
    protected DigsiteWorldContext currentWorld;

    // Create new instance of saved data
    public static DigsiteBookkeeper create() {
        return new DigsiteBookkeeper();
    }

    // Load existing instance of saved data
    public static DigsiteBookkeeper load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        DigsiteBookkeeper state = create();

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

        CompoundTag digsitesNbt = tag.getCompound("digsitesInWorld");
        if(digsitesNbt != null) {
            for (String id : digsitesNbt.getAllKeys()) {
                UUID uuid = UUID.fromString(id);
                CompoundTag digsiteNbt = digsitesNbt.getCompound(id);
                if(digsiteNbt != null) {
                    digsitesInWorld.addDigsite(uuid, Digsite.fromNbt(digsiteNbt));
                }
                else {
                    LOGGER.info("Digsite was null!");
                }
            }
        }

        CompoundTag placedMarkersNbt = tag.getCompound("placedDigsiteMarkers");
        if(placedMarkersNbt != null) {
            for (String id : placedMarkersNbt.getAllKeys()) {
                UUID uuid = UUID.fromString(id);
                placedDigsiteMarkers.add(uuid);
            }
        }

        return state;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag digsitesNbt = new CompoundTag();
        currentWorld.digsites.forEach( (UUID id, Digsite site) ->
                digsitesNbt.put(id.toString(), site.toNbt())
        );
        nbt.put("digsitesInWorld", digsitesNbt);

        CompoundTag placedMarkersNbt = new CompoundTag();
        placedDigsiteMarkers.forEach((UUID id) ->
                placedMarkersNbt.putUUID(id.toString(), id));
        nbt.put("placedDigsiteMarkers", placedMarkersNbt);

        nbt.putString("currentWorldId", currentWorld.worldId);

        return nbt;
    }

    public static class DigsiteWorldContext {
        private final HashMap<UUID,Digsite> digsites = new HashMap<>();
        protected String worldId;
        protected ServerLevel world;

        DigsiteWorldContext(String worldId){
            this.worldId = worldId;
        }

        public Level getWorld(){
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

    public void UpdateDigsitesInWorld(ServerLevel world)
    {
        currentWorld.digsites.forEach((uuid, digsite) -> {
            for (DigsiteEvent event : digsite.getDigsiteEvents()) {
                if (event.checkTick(world.getGameTime()) && event.isConditionsMet(digsite)) {
                    event.run(digsite);
                    LOGGER.info("Running Event [{}] at Digsite [{}]", event.getEventName(), uuid.toString());
                }
            }
        });

    }

//    @Override
//    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//        NbtCompound digsitesNbt = new NbtCompound();
//        currentWorld.digsites.forEach( (UUID id, Digsite site) ->
//                digsitesNbt.put(id.toString(), site.toNbt())
//        );
//        nbt.put("digsitesInWorld", digsitesNbt);
//
//        NbtCompound placedMarkersNbt = new NbtCompound();
//        placedDigsiteMarkers.forEach((UUID id) ->
//                placedMarkersNbt.putUuid(id.toString(), id));
//        nbt.put("placedDigsiteMarkers", placedMarkersNbt);
//
//        nbt.putString("currentWorldId", currentWorld.worldId);
//
//        return nbt;
//    }

//    public static DigsiteBookkeeper createFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
//        DigsiteBookkeeper state = new DigsiteBookkeeper();
//
//        DigsiteWorldContext digsitesInWorld = null;
//        if(tag.contains("currentWorldId")){
//            String worldId = tag.getString("currentWorldId");
//            if(globalDigsiteRecord.containsKey(worldId)){
//                digsitesInWorld = globalDigsiteRecord.get(worldId);
//            }
//            else {
//                digsitesInWorld = new DigsiteWorldContext(worldId);
//                globalDigsiteRecord.put(worldId, digsitesInWorld);
//            }
//        }
//
//        if(digsitesInWorld == null){
//            return state;
//        }
//
//        CompoundTag digsitesNbt = tag.getCompound("digsitesInWorld");
//        if(digsitesNbt != null) {
//            for (String id : digsitesNbt.getAllKeys()) {
//                UUID uuid = UUID.fromString(id);
//                Tag digsiteNbt = digsitesNbt.get(id);
//                if(digsiteNbt != null) {
//                    digsitesInWorld.addDigsite(uuid, Digsite.fromNbt(digsiteNbt));
//                }
//                else {
//                    LOGGER.info("Digsite was null!");
//                }
//            }
//        }
//
//        CompoundTag placedMarkersNbt = tag.getCompound("placedDigsiteMarkers");
//        if(placedMarkersNbt != null) {
//            for (String id : placedMarkersNbt.getAllKeys()) {
//                UUID uuid = UUID.fromString(id);
//                placedDigsiteMarkers.add(uuid);
//            }
//        }
//
//        return state;
//    }

    private static final SavedData.Factory<DigsiteBookkeeper> type = new SavedData.Factory<>(
            DigsiteBookkeeper::new, // If there's no 'StateSaverAndLoader' yet create one
            DigsiteBookkeeper::load, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );



    public static DigsiteBookkeeper getWorldState(ServerLevel world)
    {
        DigsiteBookkeeper digsitesState = world.getDataStorage().computeIfAbsent(type, DigsitesMod.MODID);

        String worldId = world.dimension().registry().getPath();
        if(globalDigsiteRecord.containsKey(worldId)){
            digsitesState.currentWorld = globalDigsiteRecord.get(worldId);
            if(digsitesState.currentWorld.world == null){
                digsitesState.currentWorld.world = world;
            }
        }
        else {
            digsitesState.currentWorld = new DigsiteWorldContext(worldId);
            globalDigsiteRecord.put(digsitesState.currentWorld.worldId, digsitesState.currentWorld);
            digsitesState.currentWorld.world = world;
        }

        digsitesState.setDirty();
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

