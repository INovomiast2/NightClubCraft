package nightclubcraftmod.plot;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import nightclubcraftmod.NightClubCraftMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Gestor de plots del juego.
 * Se encarga de crear, eliminar y gestionar todos los plots.
 */
public class PlotManager extends PersistentState {
    private static final String DATA_NAME = NightClubCraftMod.MOD_ID + "_plots";
    private static PlotManager instance;
    
    // Mapa de plots por ID
    private final Map<UUID, Plot> plots = new ConcurrentHashMap<>();
    
    /**
     * Constructor privado para el patrón Singleton.
     */
    private PlotManager() {
        super();
    }
    
    /**
     * Obtiene la instancia única del gestor de plots.
     * 
     * @return La instancia del gestor de plots
     */
    public static PlotManager getInstance() {
        if (instance == null) {
            instance = new PlotManager();
        }
        return instance;
    }
    
    /**
     * Inicializa el gestor de plots para un servidor.
     * 
     * @param server El servidor de Minecraft
     */
    public static void init(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        instance = world.getPersistentStateManager().getOrCreate(
            PlotManager::createFromNbt,
            PlotManager::new,
            DATA_NAME
        );
    }
    
    /**
     * Crea un gestor de plots a partir de un NbtCompound.
     * 
     * @param nbt El NbtCompound que contiene los datos
     * @return El gestor de plots creado
     */
    public static PlotManager createFromNbt(NbtCompound nbt) {
        PlotManager manager = new PlotManager();
        manager.readNbt(nbt);
        return manager;
    }
    
    /**
     * Lee los datos del gestor de plots desde un NbtCompound.
     * 
     * @param nbt El NbtCompound que contiene los datos
     */
    private void readNbt(NbtCompound nbt) {
        plots.clear();
        
        NbtList plotsList = nbt.getList("plots", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < plotsList.size(); i++) {
            NbtCompound plotNbt = plotsList.getCompound(i);
            Plot plot = new Plot(plotNbt);
            plots.put(plot.getId(), plot);
        }
        
        NightClubCraftMod.LOGGER.info("Cargados " + plots.size() + " plots");
    }
    
    /**
     * Guarda los datos del gestor de plots en un NbtCompound.
     * 
     * @return El NbtCompound con los datos
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList plotsList = new NbtList();
        
        for (Plot plot : plots.values()) {
            plotsList.add(plot.toNbt());
        }
        
        nbt.put("plots", plotsList);
        
        return nbt;
    }
    
    /**
     * Marca el gestor de plots como modificado para que se guarde.
     */
    @Override
    public void markDirty() {
        super.markDirty();
    }
    
    /**
     * Crea un nuevo plot.
     * 
     * @param selection La selección del plot
     * @param owner El jugador propietario del plot
     * @return true si se ha creado el plot, false si no se ha podido crear
     */
    public boolean createPlot(PlotSelection selection, PlayerEntity owner) {
        // Comprobar si la selección se superpone con algún plot existente
        for (Plot plot : plots.values()) {
            if (selection.overlaps(plot.getSelection())) {
                return false;
            }
        }
        
        // Crear el plot
        Plot plot = new Plot(selection, owner);
        plots.put(plot.getId(), plot);
        
        // Crear un bloque de control en el centro del plot
        if (owner.getWorld() instanceof ServerWorld serverWorld) {
            BlockPos center = selection.getCenter();
            // Buscar un bloque sólido debajo del centro
            BlockPos controlBlockPos = findSuitablePosition(serverWorld, center);
            if (controlBlockPos != null) {
                plot.createControlBlock(serverWorld, controlBlockPos);
            }
        }
        
        // Mostrar la cabeza flotante
        if (owner.getWorld() instanceof ServerWorld) {
            plot.showFloatingHead((ServerWorld) owner.getWorld());
        }
        
        // Marcar como modificado para guardar
        markDirty();
        
        NightClubCraftMod.LOGGER.info("Plot creado: " + plot);
        
        return true;
    }
    
    /**
     * Busca una posición adecuada para colocar el bloque de control.
     * 
     * @param world El mundo
     * @param center El centro del plot
     * @return La posición adecuada, o null si no se encuentra ninguna
     */
    private BlockPos findSuitablePosition(ServerWorld world, BlockPos center) {
        // Primero intentamos en el centro exacto
        if (isSuitablePosition(world, center)) {
            return center;
        }
        
        // Si no, buscamos en un radio de 3 bloques
        for (int y = 0; y >= -3; y--) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (isSuitablePosition(world, pos)) {
                        return pos;
                    }
                }
            }
        }
        
        // Si no encontramos ninguna posición adecuada, devolvemos el centro
        return center;
    }
    
    /**
     * Comprueba si una posición es adecuada para colocar el bloque de control.
     * 
     * @param world El mundo
     * @param pos La posición a comprobar
     * @return true si la posición es adecuada, false en caso contrario
     */
    private boolean isSuitablePosition(ServerWorld world, BlockPos pos) {
        // Comprobar si el bloque de abajo es sólido
        return world.getBlockState(pos.down()).isSolid() && 
               world.getBlockState(pos).isAir() && 
               world.getBlockState(pos.up()).isAir();
    }
    
    /**
     * Elimina un plot.
     * 
     * @param id El ID del plot a eliminar
     * @return true si se ha eliminado el plot, false si no existía
     */
    public boolean deletePlot(UUID id) {
        Plot plot = plots.remove(id);
        
        if (plot != null) {
            markDirty();
            NightClubCraftMod.LOGGER.info("Plot eliminado: " + plot);
            return true;
        }
        
        return false;
    }
    
    /**
     * Obtiene un plot por su ID.
     * 
     * @param id El ID del plot
     * @return El plot, o null si no existe
     */
    public Plot getPlot(UUID id) {
        return plots.get(id);
    }
    
    /**
     * Obtiene todos los plots.
     * 
     * @return Una colección con todos los plots
     */
    public Collection<Plot> getAllPlots() {
        return Collections.unmodifiableCollection(plots.values());
    }
    
    /**
     * Obtiene los plots de un jugador.
     * 
     * @param playerUuid El UUID del jugador
     * @return Una lista con los plots del jugador
     */
    public List<Plot> getPlayerPlots(UUID playerUuid) {
        return plots.values().stream()
            .filter(plot -> plot.getOwnerUuid().equals(playerUuid))
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el plot que contiene una posición.
     * 
     * @param pos La posición a comprobar
     * @return El plot que contiene la posición, o null si no hay ninguno
     */
    public Plot getPlotAt(BlockPos pos) {
        for (Plot plot : plots.values()) {
            if (plot.contains(pos)) {
                return plot;
            }
        }
        
        return null;
    }
    
    /**
     * Comprueba si un jugador puede construir en una posición.
     * 
     * @param player El jugador
     * @param pos La posición
     * @return true si el jugador puede construir, false en caso contrario
     */
    public boolean canBuild(PlayerEntity player, BlockPos pos) {
        Plot plot = getPlotAt(pos);
        
        // Si no hay plot, se puede construir (esto depende de la configuración del servidor)
        if (plot == null) {
            return true;
        }
        
        // Si el jugador es el propietario, puede construir
        return plot.isOwner(player);
    }
    
    /**
     * Abre el panel de control de un plot para un jugador.
     * 
     * @param plotId El ID del plot
     * @param player El jugador
     * @return true si se ha abierto el panel, false si el plot no existe
     */
    public boolean openPlotControlPanel(UUID plotId, PlayerEntity player) {
        Plot plot = getPlot(plotId);
        
        if (plot != null) {
            plot.openControlPanel(player);
            return true;
        }
        
        return false;
    }
    
    /**
     * Establece la visibilidad del contorno de todos los plots de un jugador.
     * Este método debe ser llamado desde el lado del servidor.
     * 
     * @param playerUuid El UUID del jugador
     * @param visible true para activar el contorno, false para desactivarlo
     * @return El número de plots afectados
     */
    public int setOutlineVisible(UUID playerUuid, boolean visible) {
        // Simplemente almacenamos la configuración de visibilidad para cada plot
        // La renderización se manejará en el cliente
        List<Plot> playerPlots = getPlayerPlots(playerUuid);
        int count = 0;
        
        for (Plot plot : playerPlots) {
            // Marcar el plot como visible/invisible
            // Esto se usará en el cliente para renderizar el contorno
            plot.setOutlineVisible(visible);
            count++;
            
            // Registrar información para depuración
            NightClubCraftMod.LOGGER.info("Plot " + plot.getId() + " visibilidad cambiada a: " + visible);
        }
        
        // Marcar como modificado para guardar los cambios
        markDirty();
        
        return count;
    }
    
    /**
     * Elimina todos los plots.
     * 
     * @return El número de plots eliminados
     */
    public int clearAllPlots() {
        int count = plots.size();
        plots.clear();
        markDirty();
        NightClubCraftMod.LOGGER.info("Se han eliminado todos los plots (" + count + ")");
        return count;
    }
} 