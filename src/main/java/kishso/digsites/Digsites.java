package kishso.digsites;

import kishso.digsites.commands.CreateDigsiteCommand;
import kishso.digsites.commands.PlaceDigsiteMarkerCommand;
import kishso.digsites.commands.RemoveDigsiteCommand;
import kishso.digsites.commands.TriggerDigsiteCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


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
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> GetBlockStateCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> CreateDigsiteCommand.register(dispatcher)));
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> RemoveDigsiteCommand.register(dispatcher)));

		ArgumentTypeRegistry.registerArgumentType(Identifier.tryParse(MOD_ID, "digsiteType"),
				DigsiteArgumentType.class, ConstantArgumentSerializer.of(DigsiteArgumentType::digsiteType));

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DigsiteResourceListener());

		ServerChunkEvents.CHUNK_LOAD.register((serverWorld, listener) -> {
			if(listener.hasStructureReferences())
			{
				Set<Structure> structures = listener.getStructureReferences().keySet();

				structures.forEach((s) ->
				{
					if(s instanceof JigsawStructure)
					{
						StructureType structType = s.getType();
						LOGGER.info(s.toString());
					}
				});
			}
		});


	}

	public static Identifier id(String path){
		return Identifier.of(MOD_ID, path);
	}
}

