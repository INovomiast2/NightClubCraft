package nightclubcraftmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nightclubcraftmod.registry.ModBlockEntities;
import nightclubcraftmod.registry.ModBlocks;
import nightclubcraftmod.registry.ModCommands;
import nightclubcraftmod.registry.ModEvents;
import nightclubcraftmod.registry.ModItems;
import nightclubcraftmod.network.ClientNetworking;
import nightclubcraftmod.network.ServerNetworking;

/**
 * Clase principal del mod NightClubCraft.
 * Este mod transforma Minecraft en una experiencia de vida nocturna y gestión de clubes.
 */
public class NightClubCraftMod implements ModInitializer {
	public static final String MOD_ID = "nightclubcraftmod";
	public static final String MOD_NAME = "NightClubCraft";
	public static final String MOD_VERSION = "DevBuild-0.2.0";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Inicializando {} {}", MOD_NAME, MOD_VERSION);
		LOGGER.info("¡Que comience la fiesta! Let the party begin!");
		
		// Registrar el contenido del mod
		registerContent();
		
		// Inicializar networking del servidor
		ServerNetworking.init();
	}
	
	/**
	 * Registra todo el contenido del mod.
	 */
	private void registerContent() {
		// Registrar bloques
		ModBlocks.register();
		
		// Registrar entidades de bloque
		ModBlockEntities.register();
		
		// Registrar items
		ModItems.register();
		
		// Registrar eventos
		ModEvents.register();
		
		// Registrar comandos
		ModCommands.register();
		
		LOGGER.info("Contenido del mod registrado con éxito");
	}
}