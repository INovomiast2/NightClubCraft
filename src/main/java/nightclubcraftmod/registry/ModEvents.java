package nightclubcraftmod.registry;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.plot.Plot;
import nightclubcraftmod.plot.PlotManager;

/**
 * Registro de eventos del mod.
 */
public class ModEvents {
    
    /**
     * Registra todos los eventos del mod.
     */
    public static void register() {
        // Evento de inicio del servidor
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            NightClubCraftMod.LOGGER.info("Inicializando PlotManager...");
            PlotManager.init(server);
        });
        
        // Evento de protección de bloques
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            // Comprobar si el jugador puede romper bloques en esta posición
            if (!canModifyBlock(player, pos)) {
                player.sendMessage(Text.literal("§cNo puedes romper bloques en este plot."), true);
                return false;
            }
            return true;
        });
        
        // Evento de interacción con bloques
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // Comprobar si el jugador puede interactuar con bloques en esta posición
            if (!canModifyBlock(player, hitResult.getBlockPos())) {
                player.sendMessage(Text.literal("§cNo puedes interactuar con bloques en este plot."), true);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
        
        NightClubCraftMod.LOGGER.info("Registrados los eventos de " + NightClubCraftMod.MOD_NAME);
    }
    
    /**
     * Comprueba si un jugador puede modificar un bloque en una posición.
     * 
     * @param player El jugador
     * @param pos La posición del bloque
     * @return true si el jugador puede modificar el bloque, false en caso contrario
     */
    private static boolean canModifyBlock(PlayerEntity player, BlockPos pos) {
        // Si el jugador está en modo creativo, siempre puede modificar bloques
        if (player.isCreative()) {
            return true;
        }
        
        // Comprobar si el jugador puede construir en esta posición
        return PlotManager.getInstance().canBuild(player, pos);
    }
} 