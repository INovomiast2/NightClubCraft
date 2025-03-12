package nightclubcraftmod.plot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nightclubcraftmod.NightClubCraftMod;
import net.minecraft.block.entity.BlockEntity;

import java.util.UUID;

/**
 * Representa un plot en el juego.
 * Un plot es una región del mundo que pertenece a un jugador.
 */
public class Plot {
    private final UUID id;
    private final PlotSelection selection;
    private final UUID ownerUuid;
    private String ownerName;
    private PlotType type;
    private PlotStatus status;
    private long creationTime;
    private long lastModifiedTime;
    private boolean outlineVisible; // Indica si el contorno del plot está visible
    
    /**
     * Crea un nuevo plot.
     * 
     * @param selection La selección del plot
     * @param owner El jugador propietario del plot
     */
    public Plot(PlotSelection selection, PlayerEntity owner) {
        this.id = UUID.randomUUID();
        this.selection = selection;
        this.ownerUuid = owner.getUuid();
        this.ownerName = owner.getName().getString();
        this.type = PlotType.EMPTY;
        this.status = PlotStatus.CLAIMED;
        this.creationTime = System.currentTimeMillis();
        this.lastModifiedTime = this.creationTime;
        this.outlineVisible = true; // Por defecto, el contorno está visible
    }
    
    /**
     * Crea un plot a partir de un NbtCompound.
     * 
     * @param nbt El NbtCompound que contiene los datos del plot
     */
    public Plot(NbtCompound nbt) {
        this.id = nbt.getUuid("id");
        this.selection = new PlotSelection(nbt.getCompound("selection"));
        this.ownerUuid = nbt.getUuid("ownerUuid");
        this.ownerName = nbt.getString("ownerName");
        this.type = PlotType.valueOf(nbt.getString("type"));
        this.status = PlotStatus.valueOf(nbt.getString("status"));
        this.creationTime = nbt.getLong("creationTime");
        this.lastModifiedTime = nbt.getLong("lastModifiedTime");
        this.outlineVisible = nbt.getBoolean("outlineVisible");
    }
    
    /**
     * Guarda el plot en un NbtCompound.
     * 
     * @return Un NbtCompound con los datos del plot
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        
        nbt.putUuid("id", id);
        nbt.put("selection", selection.toNbt());
        nbt.putUuid("ownerUuid", ownerUuid);
        nbt.putString("ownerName", ownerName);
        nbt.putString("type", type.name());
        nbt.putString("status", status.name());
        nbt.putLong("creationTime", creationTime);
        nbt.putLong("lastModifiedTime", lastModifiedTime);
        nbt.putBoolean("outlineVisible", outlineVisible);
        
        return nbt;
    }
    
    /**
     * Obtiene el ID del plot.
     * 
     * @return El ID del plot
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Obtiene la selección del plot.
     * 
     * @return La selección del plot
     */
    public PlotSelection getSelection() {
        return selection;
    }
    
    /**
     * Obtiene el UUID del propietario del plot.
     * 
     * @return El UUID del propietario
     */
    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    
    /**
     * Obtiene el nombre del propietario del plot.
     * 
     * @return El nombre del propietario
     */
    public String getOwnerName() {
        return ownerName;
    }
    
    /**
     * Obtiene el tipo de plot.
     * 
     * @return El tipo de plot
     */
    public PlotType getType() {
        return type;
    }
    
    /**
     * Establece el tipo de plot.
     * 
     * @param type El nuevo tipo de plot
     */
    public void setType(PlotType type) {
        this.type = type;
        this.lastModifiedTime = System.currentTimeMillis();
    }
    
    /**
     * Obtiene el estado del plot.
     * 
     * @return El estado del plot
     */
    public PlotStatus getStatus() {
        return status;
    }
    
    /**
     * Establece el estado del plot.
     * 
     * @param status El nuevo estado del plot
     */
    public void setStatus(PlotStatus status) {
        this.status = status;
        this.lastModifiedTime = System.currentTimeMillis();
    }
    
    /**
     * Obtiene el tiempo de creación del plot.
     * 
     * @return El tiempo de creación en milisegundos
     */
    public long getCreationTime() {
        return creationTime;
    }
    
    /**
     * Obtiene el tiempo de la última modificación del plot.
     * 
     * @return El tiempo de la última modificación en milisegundos
     */
    public long getLastModifiedTime() {
        return lastModifiedTime;
    }
    
    /**
     * Comprueba si un jugador es el propietario del plot.
     * 
     * @param player El jugador a comprobar
     * @return true si el jugador es el propietario, false en caso contrario
     */
    public boolean isOwner(PlayerEntity player) {
        return player.getUuid().equals(ownerUuid);
    }
    
    /**
     * Comprueba si una posición está dentro del plot.
     * 
     * @param pos La posición a comprobar
     * @return true si la posición está dentro del plot, false en caso contrario
     */
    public boolean contains(BlockPos pos) {
        return selection.contains(pos);
    }
    
    /**
     * Muestra una cabeza flotante con el nombre del propietario en el centro del plot.
     * 
     * @param world El mundo donde mostrar la cabeza
     */
    public void showFloatingHead(ServerWorld world) {
        // Aquí se implementaría la lógica para mostrar una cabeza flotante
        // con el nombre del propietario en el centro del plot.
        // Esto podría hacerse con una entidad personalizada o con partículas.
        
        // Por ahora, simplemente registramos un mensaje de depuración
        NightClubCraftMod.LOGGER.info("Mostrando cabeza flotante para el plot " + id + " en " + selection.getCenter());
    }
    
    /**
     * Abre el panel de control del plot para un jugador.
     * 
     * @param player El jugador que abre el panel de control
     */
    public void openControlPanel(PlayerEntity player) {
        // Aquí se implementaría la lógica para abrir el panel de control del plot
        // Esto podría ser una pantalla personalizada o un inventario con ítems
        
        // Por ahora, simplemente enviamos un mensaje al jugador
        player.sendMessage(Text.literal("§aPanel de control del plot " + id), false);
    }
    
    /**
     * Crea un bloque de control para este plot.
     * 
     * @param world El mundo donde crear el bloque
     * @param pos La posición donde crear el bloque
     * @return true si se creó el bloque, false en caso contrario
     */
    public boolean createControlBlock(ServerWorld world, BlockPos pos) {
        // Comprobar si la posición está dentro del plot
        if (!contains(pos)) {
            return false;
        }
        
        // Colocar el bloque
        world.setBlockState(pos, nightclubcraftmod.registry.ModBlocks.PLOT_CONTROL_BLOCK.getDefaultState());
        
        // Obtener la entidad del bloque
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof nightclubcraftmod.block.entity.PlotControlBlockEntity plotControlEntity) {
            // Establecer el ID del plot
            plotControlEntity.setPlotId(id);
            return true;
        }
        
        return false;
    }
    
    /**
     * Establece si el contorno del plot está visible.
     * 
     * @param visible true si el contorno está visible, false en caso contrario
     */
    public void setOutlineVisible(boolean visible) {
        this.outlineVisible = visible;
    }
    
    /**
     * Comprueba si el contorno del plot está visible.
     * 
     * @return true si el contorno está visible, false en caso contrario
     */
    public boolean isOutlineVisible() {
        return outlineVisible;
    }
    
    @Override
    public String toString() {
        return "Plot{" +
                "id=" + id +
                ", owner=" + ownerName +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
    
    /**
     * Tipos de plot disponibles.
     */
    public enum PlotType {
        EMPTY,          // Plot vacío
        CUSTOM_BUILD,   // Construcción personalizada
        TEMPLATE_BUILD  // Construcción a partir de plantilla
    }
    
    /**
     * Estados posibles de un plot.
     */
    public enum PlotStatus {
        CLAIMED,        // Reclamado pero no desarrollado
        UNDER_CONSTRUCTION, // En construcción
        COMPLETED,      // Construcción completada
        ABANDONED       // Abandonado
    }
} 