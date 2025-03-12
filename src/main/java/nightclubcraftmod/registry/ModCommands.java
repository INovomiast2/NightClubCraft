package nightclubcraftmod.registry;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.command.DevCommand;
import nightclubcraftmod.command.PlotCommand;

/**
 * Registro de comandos del mod.
 */
public class ModCommands {
    
    /**
     * Registra todos los comandos del mod.
     */
    public static void register() {
        CommandRegistrationCallback.EVENT.register(PlotCommand::register);
        CommandRegistrationCallback.EVENT.register(DevCommand::register);
        
        NightClubCraftMod.LOGGER.info("Registrados los comandos de " + NightClubCraftMod.MOD_NAME);
    }
} 