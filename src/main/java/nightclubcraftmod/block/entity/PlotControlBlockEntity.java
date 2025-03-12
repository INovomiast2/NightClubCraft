package nightclubcraftmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import nightclubcraftmod.registry.ModBlockEntities;

import java.util.UUID;

/**
 * Entidad de bloque para el bloque de control de plots.
 * Almacena el ID del plot asociado y si el contorno está visible.
 */
public class PlotControlBlockEntity extends BlockEntity {
    
    private UUID plotId;
    private boolean outlineVisible;
    
    public PlotControlBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLOT_CONTROL_BLOCK_ENTITY, pos, state);
        this.outlineVisible = false;
    }
    
    /**
     * Establece el ID del plot asociado.
     * 
     * @param plotId El ID del plot
     */
    public void setPlotId(UUID plotId) {
        this.plotId = plotId;
        markDirty();
    }
    
    /**
     * Obtiene el ID del plot asociado.
     * 
     * @return El ID del plot
     */
    public UUID getPlotId() {
        return plotId;
    }
    
    /**
     * Establece si el contorno del plot está visible.
     * 
     * @param visible true si el contorno está visible, false en caso contrario
     */
    public void setOutlineVisible(boolean visible) {
        this.outlineVisible = visible;
        markDirty();
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        
        if (nbt.contains("PlotId")) {
            this.plotId = nbt.getUuid("PlotId");
        }
        
        this.outlineVisible = nbt.getBoolean("OutlineVisible");
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        
        if (plotId != null) {
            nbt.putUuid("PlotId", plotId);
        }
        
        nbt.putBoolean("OutlineVisible", outlineVisible);
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
} 