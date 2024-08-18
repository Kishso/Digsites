package kishso.digsites;

import it.unimi.dsi.fastutil.longs.LongSet;
import kishso.digsites.commands.CreateDigsiteCommand;
import kishso.digsites.commands.RemoveDigsiteCommand;
import kishso.digsites.commands.TriggerDigsiteCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Digsites implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("digsites");

	public static final String MOD_ID = "kishso.digsites";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Digsites Mod Init!");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> TriggerDigsiteCommand.register(dispatcher));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> GetBlockStateCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> CreateDigsiteCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> RemoveDigsiteCommand.register(dispatcher)));

		ServerChunkEvents.CHUNK_LOAD.register((serverWorld, listener) -> {
			if(listener.hasStructureReferences())
			{
				Set<Structure> structures = listener.getStructureReferences().keySet();

				structures.forEach((s) ->
				{
					if(s instanceof JigsawStructure)
					{

					}
				});
			}
		});


	}

	public static Identifier id(String path){
		return Identifier.of(MOD_ID, path);
	}
}

