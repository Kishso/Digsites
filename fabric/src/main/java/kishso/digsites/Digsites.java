package kishso.digsites;

import kishso.digsites.commands.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class Digsites implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("digsites");

	public static final String MOD_ID = "digsites";



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Digsites Mod Init!");

 		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> TriggerDigsiteCommand.register(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PlaceDigsiteMarkerCommand.register(dispatcher));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> CreateDigsiteCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> RemoveDigsiteCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> HighlightDigsiteCommand.register(dispatcher)));

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DigsiteResourceListener());

		ModContainer modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).isPresent()? FabricLoader.getInstance().getModContainer(MOD_ID).get() : null;
		if(modContainer != null){
			ResourceManagerHelper.registerBuiltinResourcePack(Identifier.tryParse(MOD_ID, "default_digsites"),
					modContainer, "Default Digsites",ResourcePackActivationType.DEFAULT_ENABLED);
		}


		ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
			if(entity instanceof DisplayEntity.ItemDisplayEntity)
			{
				if(entity.getCommandTags().contains("isDigsite"))
				{
					DigsiteBookkeeper bookKeeper = DigsiteBookkeeper.getWorldState(serverWorld);
					if(DigsiteBookkeeper.placedDigsiteMarkers.contains(entity.getUuid())){
						LOGGER.info("Found Digsite Place Marker...");
					}
					else {
						LOGGER.info("Found Digsite Structure Marker");
						float entityPitch = entity.getPitch();
						float entityYaw = entity.getYaw();
						Optional<String> digsiteTypeTag = entity.getCommandTags().stream().filter(
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
											entity.getBlockPos(), entityYaw, entityPitch, digsiteType, entity.getUuid());
									bookKeeper.addDigsite(entity.getUuid(), newDigsite);
								}
								LOGGER.info("Placed Digsite Structure");
							}
						}
						entity.remove(Entity.RemovalReason.DISCARDED);
					}

				}
			}
		});

		ServerTickEvents.END_WORLD_TICK.register((listener) -> {
			ServerWorld world = listener.toServerWorld();
			DigsiteBookkeeper bookkeeper = DigsiteBookkeeper.getWorldState(world);

			bookkeeper.UpdateDigsitesInWorld(world);
		});


	}

	public static Identifier id(String path){
		return Identifier.of(MOD_ID, path);
	}
}

