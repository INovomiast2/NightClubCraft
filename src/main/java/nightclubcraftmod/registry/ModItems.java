package nightclubcraftmod.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.item.DevelopmentStickItem;

/**
 * Registro de items del mod.
 */
public class ModItems {
    
    // Items
    public static final Item DEVELOPMENT_STICK = new DevelopmentStickItem(
        new FabricItemSettings()
            .maxCount(1)
            .rarity(Rarity.EPIC)
    );
    
    /**
     * Registra todos los items del mod.
     */
    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(NightClubCraftMod.MOD_ID, "development_stick"), DEVELOPMENT_STICK);
        
        NightClubCraftMod.LOGGER.info("Registrados los items de " + NightClubCraftMod.MOD_NAME);
    }
} 