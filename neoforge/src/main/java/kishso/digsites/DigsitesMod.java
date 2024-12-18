package kishso.digsites;

import com.mojang.brigadier.CommandDispatcher;
import kishso.digsites.commands.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforgespi.language.IConfigurable;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DigsitesMod.MODID)
public class DigsitesMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "digsites";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public DigsitesMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::onAddPackFinders);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }


    public void onAddPackFinders(AddPackFindersEvent event)
    {
        ResourceLocation packLocation = ResourceLocation.tryBuild(MODID,"default_digsites");
        if(packLocation != null) {
            event.addPackFinders(packLocation, PackType.SERVER_DATA,
                    Component.literal("Default Digsites"),
                    PackSource.DEFAULT, false, Pack.Position.BOTTOM);
        }
    }

    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event)
    {
        event.addListener(new DigsiteResourceListener());
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        CreateDigsiteCommand.register(event.getDispatcher());
        HighlightDigsiteCommand.register(event.getDispatcher());
        PlaceDigsiteMarkerCommand.register(event.getDispatcher());
        RemoveDigsiteCommand.register(event.getDispatcher());
        TriggerDigsiteCommand.register(event.getDispatcher());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onEntityLoad(EntityJoinLevelEvent event)
    {

        if(event.getEntity() instanceof Display.ItemDisplay)
        {
            Display.ItemDisplay entity = (Display.ItemDisplay)event.getEntity();
            if(entity.getTags().contains("isDigsite"))
            {
                DigsiteBookkeeper bookKeeper = DigsiteBookkeeper.getWorldState((ServerLevel)event.getLevel());
                if(DigsiteBookkeeper.placedDigsiteMarkers.contains(entity.getUUID())){
                    LOGGER.info("Found Digsite Place Marker...");
                }
                else {
                    LOGGER.info("Found Digsite Structure Marker");
                    float entityPitch = entity.xRotO;
                    float entityYaw = entity.yRotO;
                    Optional<String> digsiteTypeTag = entity.getTags().stream().filter(
                            (str) -> str.contains("digsiteType")
                    ).findFirst();
                    if(digsiteTypeTag.isPresent())
                    {
                        String digsiteTypeStr =
                                Arrays.stream(digsiteTypeTag.get().split(":")).toList().get(1);
                        if(digsiteTypeStr != null)
                        {
                            if(DigsiteBookkeeper.loadedDigsiteTypes.containsKey(digsiteTypeStr)){
                                DigsiteType digsiteType = DigsiteBookkeeper.loadedDigsiteTypes.get(digsiteTypeStr);
                                Digsite newDigsite = new Digsite(
                                        entity.getOnPos(), entityYaw, entityPitch, digsiteType, entity.getUUID());
                                bookKeeper.addDigsite(entity.getUUID(), newDigsite);
                            }
                            LOGGER.info("Placed Digsite Structure");
                        }
                    }
                    entity.discard();
                    entity.remove(Entity.RemovalReason.DISCARDED);
                    // Do not load entity
                    event.setCanceled(true);

                }

            }
        }
    }

    @SubscribeEvent
    public void onEndWorldTick(LevelTickEvent.Post event)
    {
        if(event.getLevel() instanceof ServerLevel) {
            DigsiteBookkeeper bookkeeper = DigsiteBookkeeper.getWorldState((ServerLevel) event.getLevel());
            bookkeeper.UpdateDigsitesInWorld((ServerLevel)event.getLevel());
        }
    }

//    public static Identifier id(String path){
//        return Identifier.of(MOD_ID, path);
//    }
}