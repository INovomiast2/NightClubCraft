package nightclubcraftmod.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.block.entity.PlotControlBlockEntity;

/**
 * Registro de entidades de bloque del mod.
 */
public class ModBlockEntities {
    
    // Entidades de bloque
    public static final BlockEntityType<PlotControlBlockEntity> PLOT_CONTROL_BLOCK_ENTITY = 
            FabricBlockEntityTypeBuilder.create(PlotControlBlockEntity::new, ModBlocks.PLOT_CONTROL_BLOCK).build();
    
    /**
     * Registra todas las entidades de bloque del mod.
     */
    public static void register() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, 
                new Identifier(NightClubCraftMod.MOD_ID, "plot_control_block_entity"), 
                PLOT_CONTROL_BLOCK_ENTITY);
        
        NightClubCraftMod.LOGGER.info("Registradas las entidades de bloque de " + NightClubCraftMod.MOD_NAME);
    }
} 