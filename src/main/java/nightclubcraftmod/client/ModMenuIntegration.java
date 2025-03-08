package nightclubcraftmod.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.client.gui.screen.DevToolsScreen;
import nightclubcraftmod.util.DevEnvironment;

/**
 * Integración con ModMenu para mostrar información personalizada sobre nuestro mod
 * y proporcionar acceso a la pantalla de configuración y herramientas de desarrollo.
 */
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    /**
     * Proporciona una fábrica para crear la pantalla de configuración del mod.
     * En modo desarrollo, devuelve la pantalla de herramientas de desarrollo.
     * En modo producción, devuelve null (por ahora).
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // En modo desarrollo, mostrar la pantalla de herramientas de desarrollo
        if (DevEnvironment.isDevelopmentEnvironment()) {
            return parent -> new DevToolsScreen(parent);
        }
        
        // En modo producción, por ahora no tenemos pantalla de configuración
        // En el futuro, podríamos crear una pantalla de configuración específica
        return parent -> null;
    }
} 