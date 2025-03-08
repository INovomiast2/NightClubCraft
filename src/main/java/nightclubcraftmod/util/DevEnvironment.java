package nightclubcraftmod.util;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Utilidad para detectar si estamos en un entorno de desarrollo.
 */
public class DevEnvironment {
    
    private static final boolean IS_DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
    
    /**
     * Comprueba si estamos en un entorno de desarrollo.
     * @return true si estamos en un entorno de desarrollo, false en caso contrario.
     */
    public static boolean isDevelopmentEnvironment() {
        return IS_DEV_ENV;
    }
    
    /**
     * Ejecuta una acción solo si estamos en un entorno de desarrollo.
     * @param runnable La acción a ejecutar.
     */
    public static void runInDev(Runnable runnable) {
        if (IS_DEV_ENV) {
            runnable.run();
        }
    }
} 