package nightclubcraftmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import nightclubcraftmod.network.ClientNetworking;

/**
 * Punto de entrada del mod para el lado del cliente.
 */
@Environment(EnvType.CLIENT)
public class NightClubCraftModClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Inicializar networking del cliente
        ClientNetworking.init();
        
        NightClubCraftMod.LOGGER.info("NightClubCraftMod client initialized");
    }
} 