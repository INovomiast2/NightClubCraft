package nightclubcraftmod.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.block.PlotControlBlock;
import nightclubcraftmod.block.PlotFenceBlock;
import nightclubcraftmod.block.light_pole.ClassicLightPoleBlock;
import nightclubcraftmod.block.light_pole.FuturisticLightPoleBlock;
import nightclubcraftmod.block.light_pole.ModernLightPoleBlock;

/**
 * Registro de bloques del mod.
 */
public class ModBlocks {
    
    // Bloques
    public static final Block PLOT_CONTROL_BLOCK = new PlotControlBlock(
        FabricBlockSettings.copyOf(Blocks.COMMAND_BLOCK)
            .strength(3.0f, 6.0f)
            .sounds(BlockSoundGroup.METAL)
            .luminance(state -> 5) // Brillo de nivel 5
    );
    
    // Valla de plot
    public static final Block PLOT_FENCE = new PlotFenceBlock(
        FabricBlockSettings.copyOf(Blocks.IRON_BARS)
            .strength(2.5f, 5.0f)
            .sounds(BlockSoundGroup.METAL)
            .nonOpaque() // Para que no bloquee la visión
    );
    
    // Postes de luz
    public static final Block MODERN_LIGHT_POLE = new ModernLightPoleBlock(
        FabricBlockSettings.copyOf(Blocks.IRON_BLOCK) // Usando textura de bloque de hierro
            .strength(2.0f, 4.0f)
            .sounds(BlockSoundGroup.METAL)
            .luminance(state -> 15) // Luz brillante
            .nonOpaque() // Para que no bloquee la luz
    );
    
    public static final Block CLASSIC_LIGHT_POLE = new ClassicLightPoleBlock(
        FabricBlockSettings.copyOf(Blocks.DARK_OAK_FENCE) // Usando textura de valla de roble oscuro
            .strength(2.0f, 4.0f)
            .sounds(BlockSoundGroup.WOOD)
            .luminance(state -> 12) // Luz cálida
            .nonOpaque() // Para que no bloquee la luz
    );
    
    public static final Block FUTURISTIC_LIGHT_POLE = new FuturisticLightPoleBlock(
        FabricBlockSettings.copyOf(Blocks.QUARTZ_PILLAR) // Usando textura de pilar de cuarzo
            .strength(2.5f, 5.0f)
            .sounds(BlockSoundGroup.STONE)
            .luminance(state -> 14) // Luz azulada
            .nonOpaque() // Para que no bloquee la luz
    );
    
    /**
     * Registra todos los bloques del mod.
     */
    public static void register() {
        // Registrar bloques
        Registry.register(Registries.BLOCK, new Identifier(NightClubCraftMod.MOD_ID, "plot_control_block"), PLOT_CONTROL_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(NightClubCraftMod.MOD_ID, "plot_fence"), PLOT_FENCE);
        Registry.register(Registries.BLOCK, new Identifier(NightClubCraftMod.MOD_ID, "modern_light_pole"), MODERN_LIGHT_POLE);
        Registry.register(Registries.BLOCK, new Identifier(NightClubCraftMod.MOD_ID, "classic_light_pole"), CLASSIC_LIGHT_POLE);
        Registry.register(Registries.BLOCK, new Identifier(NightClubCraftMod.MOD_ID, "futuristic_light_pole"), FUTURISTIC_LIGHT_POLE);
        
        // Registrar items de bloque
        Registry.register(Registries.ITEM, new Identifier(NightClubCraftMod.MOD_ID, "plot_control_block"), 
                new BlockItem(PLOT_CONTROL_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(NightClubCraftMod.MOD_ID, "plot_fence"), 
                new BlockItem(PLOT_FENCE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(NightClubCraftMod.MOD_ID, "modern_light_pole"), 
                new BlockItem(MODERN_LIGHT_POLE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(NightClubCraftMod.MOD_ID, "classic_light_pole"), 
                new BlockItem(CLASSIC_LIGHT_POLE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(NightClubCraftMod.MOD_ID, "futuristic_light_pole"), 
                new BlockItem(FUTURISTIC_LIGHT_POLE, new FabricItemSettings()));
        
        NightClubCraftMod.LOGGER.info("Registrados los bloques de " + NightClubCraftMod.MOD_NAME);
    }
} 