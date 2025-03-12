package nightclubcraftmod.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nightclubcraftmod.NightClubCraftMod;
import nightclubcraftmod.plot.PlotManager;
import nightclubcraftmod.plot.PlotSelection;
import nightclubcraftmod.util.PlotRenderer;

import java.util.List;

/**
 * Item utilizado para seleccionar plots en el mundo.
 * Click derecho para seleccionar el primer y segundo punto.
 * Scroll para ajustar la altura del plot.
 */
public class DevelopmentStickItem extends Item {
    
    // Altura máxima y mínima para el ajuste con scroll
    private static final int MAX_HEIGHT_ADJUSTMENT = 10;
    private static final int MIN_HEIGHT_ADJUSTMENT = -10;

    public DevelopmentStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }
        
        // Obtener la posición del bloque al que se está apuntando
        BlockHitResult hitResult = (BlockHitResult) player.raycast(5.0, 0.0f, false);
        BlockPos pos = hitResult.getBlockPos();
        
        // Comprobar si ya tenemos el primer punto seleccionado
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains("firstPosX")) {
            // Ya tenemos el primer punto, así que este es el segundo punto
            int firstX = nbt.getInt("firstPosX");
            int firstY = nbt.getInt("firstPosY");
            int firstZ = nbt.getInt("firstPosZ");
            BlockPos firstPos = new BlockPos(firstX, firstY, firstZ);
            
            // Crear la selección de plot
            PlotSelection selection = new PlotSelection(firstPos, pos);
            
            // Verificar el tamaño del plot
            if (selection.getVolume() > 10000) { // Máximo 10,000 bloques (ajustar según necesidades)
                player.sendMessage(Text.literal("§cEl plot es demasiado grande. Máximo 10,000 bloques."), true);
                return TypedActionResult.fail(stack);
            }
            
            // Registrar el plot
            boolean success = PlotManager.getInstance().createPlot(selection, player);
            
            if (success) {
                // Limpiar la selección
                stack.removeSubNbt("firstPosX");
                stack.removeSubNbt("firstPosY");
                stack.removeSubNbt("firstPosZ");
                stack.removeSubNbt("heightAdjustment");
                
                player.sendMessage(Text.literal("§a¡Plot creado con éxito!"), false);
            } else {
                player.sendMessage(Text.literal("§cNo se pudo crear el plot. Posiblemente se superpone con otro existente."), false);
            }
        } else {
            // Este es el primer punto
            nbt.putInt("firstPosX", pos.getX());
            nbt.putInt("firstPosY", pos.getY());
            nbt.putInt("firstPosZ", pos.getZ());
            nbt.putInt("heightAdjustment", 0); // Inicializar el ajuste de altura
            
            // Notificar al jugador
            player.sendMessage(Text.literal("§a¡Primer punto seleccionado en " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "!"), true);
            player.sendMessage(Text.literal("§aUsa el scroll para ajustar la altura y click derecho para seleccionar el segundo punto."), true);
        }
        
        return TypedActionResult.success(stack);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // Redirigir al método use para manejar ambos puntos con click derecho
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        
        if (player != null) {
            use(world, player, context.getHand());
        }
        
        return ActionResult.SUCCESS;
    }
    
    /**
     * Maneja el evento de scroll para ajustar la altura del plot.
     * Este método debe ser llamado desde un mixin que capture el evento de scroll.
     * 
     * @param player El jugador
     * @param amount La cantidad de scroll (positivo para arriba, negativo para abajo)
     * @return true si se procesó el scroll, false en caso contrario
     */
    public static boolean handleScroll(PlayerEntity player, double amount) {
        // Comprobar si el jugador tiene el Development Stick en la mano
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        
        ItemStack stack = null;
        if (mainHand.getItem() instanceof DevelopmentStickItem) {
            stack = mainHand;
        } else if (offHand.getItem() instanceof DevelopmentStickItem) {
            stack = offHand;
        }
        
        if (stack == null) {
            return false;
        }
        
        // Comprobar si ya tenemos el primer punto seleccionado
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("firstPosX")) {
            return false;
        }
        
        // Ajustar la altura
        int heightAdjustment = nbt.getInt("heightAdjustment");
        
        if (amount > 0) {
            // Scroll hacia arriba
            heightAdjustment = Math.min(heightAdjustment + 1, MAX_HEIGHT_ADJUSTMENT);
        } else {
            // Scroll hacia abajo
            heightAdjustment = Math.max(heightAdjustment - 1, MIN_HEIGHT_ADJUSTMENT);
        }
        
        nbt.putInt("heightAdjustment", heightAdjustment);
        
        // Notificar al jugador
        player.sendMessage(Text.literal("§aAjuste de altura: " + heightAdjustment), true);
        
        return true;
    }
    
    /**
     * Obtiene la selección actual basada en el primer punto y la posición actual del jugador.
     * 
     * @param stack El ItemStack del Development Stick
     * @param player El jugador
     * @return La selección actual, o null si no hay primer punto seleccionado
     */
    public static PlotSelection getCurrentSelection(ItemStack stack, PlayerEntity player) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("firstPosX")) {
            return null;
        }
        
        int firstX = nbt.getInt("firstPosX");
        int firstY = nbt.getInt("firstPosY");
        int firstZ = nbt.getInt("firstPosZ");
        int heightAdjustment = nbt.getInt("heightAdjustment");
        
        BlockPos firstPos = new BlockPos(firstX, firstY, firstZ);
        
        // Obtener la posición a la que está mirando el jugador
        BlockHitResult hitResult = (BlockHitResult) player.raycast(5.0, 0.0f, false);
        BlockPos secondPos = hitResult.getBlockPos();
        
        // Aplicar el ajuste de altura al segundo punto
        secondPos = secondPos.up(heightAdjustment);
        
        return new PlotSelection(firstPos, secondPos);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        
        tooltip.add(Text.literal("Herramienta para seleccionar plots").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Click derecho: Seleccionar puntos").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Scroll: Ajustar altura").formatted(Formatting.AQUA));
        
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("firstPosX")) {
            int x = nbt.getInt("firstPosX");
            int y = nbt.getInt("firstPosY");
            int z = nbt.getInt("firstPosZ");
            int heightAdjustment = nbt.getInt("heightAdjustment");
            
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("Primer punto: " + x + ", " + y + ", " + z).formatted(Formatting.GREEN));
            tooltip.add(Text.literal("Ajuste de altura: " + heightAdjustment).formatted(Formatting.YELLOW));
        }
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        // Hacer que el item brille si tiene una posición seleccionada
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains("firstPosX");
    }
} 