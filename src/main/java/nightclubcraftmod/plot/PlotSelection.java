package nightclubcraftmod.plot;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/**
 * Representa la selección de un plot en el mundo.
 * Contiene las coordenadas de las esquinas del plot.
 */
public class PlotSelection {
    private final BlockPos minPos;
    private final BlockPos maxPos;
    
    /**
     * Crea una nueva selección de plot a partir de dos posiciones.
     * Las posiciones se ordenan automáticamente para que minPos tenga las coordenadas mínimas
     * y maxPos tenga las coordenadas máximas.
     * 
     * @param pos1 Primera posición de la selección
     * @param pos2 Segunda posición de la selección
     */
    public PlotSelection(BlockPos pos1, BlockPos pos2) {
        // Ordenar las coordenadas para que minPos tenga las coordenadas mínimas
        // y maxPos tenga las coordenadas máximas
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        this.minPos = new BlockPos(minX, minY, minZ);
        this.maxPos = new BlockPos(maxX, maxY, maxZ);
    }
    
    /**
     * Crea una selección de plot a partir de un NbtCompound.
     * 
     * @param nbt El NbtCompound que contiene los datos de la selección
     */
    public PlotSelection(NbtCompound nbt) {
        int minX = nbt.getInt("minX");
        int minY = nbt.getInt("minY");
        int minZ = nbt.getInt("minZ");
        
        int maxX = nbt.getInt("maxX");
        int maxY = nbt.getInt("maxY");
        int maxZ = nbt.getInt("maxZ");
        
        this.minPos = new BlockPos(minX, minY, minZ);
        this.maxPos = new BlockPos(maxX, maxY, maxZ);
    }
    
    /**
     * Guarda la selección en un NbtCompound.
     * 
     * @return Un NbtCompound con los datos de la selección
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        
        nbt.putInt("minX", minPos.getX());
        nbt.putInt("minY", minPos.getY());
        nbt.putInt("minZ", minPos.getZ());
        
        nbt.putInt("maxX", maxPos.getX());
        nbt.putInt("maxY", maxPos.getY());
        nbt.putInt("maxZ", maxPos.getZ());
        
        return nbt;
    }
    
    /**
     * Obtiene la posición mínima de la selección.
     * 
     * @return La posición mínima
     */
    public BlockPos getMinPos() {
        return minPos;
    }
    
    /**
     * Obtiene la posición máxima de la selección.
     * 
     * @return La posición máxima
     */
    public BlockPos getMaxPos() {
        return maxPos;
    }
    
    /**
     * Comprueba si la selección contiene una posición.
     * 
     * @param pos La posición a comprobar
     * @return true si la posición está dentro de la selección, false en caso contrario
     */
    public boolean contains(BlockPos pos) {
        return pos.getX() >= minPos.getX() && pos.getX() <= maxPos.getX() &&
               pos.getY() >= minPos.getY() && pos.getY() <= maxPos.getY() &&
               pos.getZ() >= minPos.getZ() && pos.getZ() <= maxPos.getZ();
    }
    
    /**
     * Comprueba si esta selección se superpone con otra.
     * 
     * @param other La otra selección
     * @return true si las selecciones se superponen, false en caso contrario
     */
    public boolean overlaps(PlotSelection other) {
        return !(maxPos.getX() < other.minPos.getX() || minPos.getX() > other.maxPos.getX() ||
                 maxPos.getY() < other.minPos.getY() || minPos.getY() > other.maxPos.getY() ||
                 maxPos.getZ() < other.minPos.getZ() || minPos.getZ() > other.maxPos.getZ());
    }
    
    /**
     * Obtiene el volumen de la selección en bloques.
     * 
     * @return El volumen de la selección
     */
    public int getVolume() {
        return (maxPos.getX() - minPos.getX() + 1) *
               (maxPos.getY() - minPos.getY() + 1) *
               (maxPos.getZ() - minPos.getZ() + 1);
    }
    
    /**
     * Obtiene el centro de la selección.
     * 
     * @return El centro de la selección
     */
    public BlockPos getCenter() {
        return new BlockPos(
            minPos.getX() + (maxPos.getX() - minPos.getX()) / 2,
            minPos.getY() + (maxPos.getY() - minPos.getY()) / 2,
            minPos.getZ() + (maxPos.getZ() - minPos.getZ()) / 2
        );
    }
    
    /**
     * Convierte la selección a un Box para renderizado o colisiones.
     * 
     * @return Un Box que representa la selección
     */
    public Box toBox() {
        // Asegurarnos de que la caja tenga una altura mínima para ser visible
        BlockPos minPosAdjusted = new BlockPos(minPos.getX(), minPos.getY(), minPos.getZ());
        BlockPos maxPosAdjusted = new BlockPos(maxPos.getX(), maxPos.getY() + 3, maxPos.getZ());
        
        // Crear la caja con un poco de expansión para mejor visibilidad
        return new Box(
            minPosAdjusted.getX() - 0.01, 
            minPosAdjusted.getY() - 0.01, 
            minPosAdjusted.getZ() - 0.01,
            maxPosAdjusted.getX() + 1.01, 
            maxPosAdjusted.getY() + 1.01, 
            maxPosAdjusted.getZ() + 1.01
        );
    }
    
    @Override
    public String toString() {
        return "PlotSelection{" +
                "minPos=" + minPos +
                ", maxPos=" + maxPos +
                '}';
    }
} 