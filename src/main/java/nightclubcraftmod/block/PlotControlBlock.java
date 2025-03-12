package nightclubcraftmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nightclubcraftmod.block.entity.PlotControlBlockEntity;
import nightclubcraftmod.plot.Plot;
import nightclubcraftmod.plot.PlotManager;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Bloque de control para plots.
 * Permite ver información del plot y activar/desactivar la visualización del contorno.
 */
public class PlotControlBlock extends BlockWithEntity {
    
    // Propiedades del bloque
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OUTLINE_VISIBLE = BooleanProperty.of("outline_visible");
    
    public PlotControlBlock(Settings settings) {
        super(settings);
        // Establecer valores por defecto para las propiedades
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, net.minecraft.util.math.Direction.NORTH)
                .with(OUTLINE_VISIBLE, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OUTLINE_VISIBLE);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Orientar el bloque hacia el jugador
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        // Obtener la entidad del bloque
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof PlotControlBlockEntity plotControlEntity)) {
            return ActionResult.PASS;
        }
        
        // Obtener el ID del plot asociado
        UUID plotId = plotControlEntity.getPlotId();
        if (plotId == null) {
            player.sendMessage(Text.literal("Este bloque de control no está asociado a ningún plot.").formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }
        
        // Obtener el plot
        Plot plot = PlotManager.getInstance().getPlot(plotId);
        if (plot == null) {
            player.sendMessage(Text.literal("El plot asociado ya no existe.").formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }
        
        // Si el jugador está agachado (shift), cambiar la visibilidad del contorno
        if (player.isSneaking()) {
            boolean newVisibility = !state.get(OUTLINE_VISIBLE);
            world.setBlockState(pos, state.with(OUTLINE_VISIBLE, newVisibility));
            
            if (newVisibility) {
                player.sendMessage(Text.literal("Contorno del plot activado.").formatted(Formatting.GREEN), false);
                plotControlEntity.setOutlineVisible(true);
            } else {
                player.sendMessage(Text.literal("Contorno del plot desactivado.").formatted(Formatting.YELLOW), false);
                plotControlEntity.setOutlineVisible(false);
            }
        } else {
            // Mostrar información del plot
            player.sendMessage(Text.literal("=== Información del Plot ===").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("ID: " + plot.getId()).formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Propietario: " + plot.getOwnerName()).formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Tipo: " + plot.getType()).formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Estado: " + plot.getStatus()).formatted(Formatting.YELLOW), false);
            
            BlockPos min = plot.getSelection().getMinPos();
            BlockPos max = plot.getSelection().getMaxPos();
            player.sendMessage(Text.literal("Área: " + min.getX() + "," + min.getY() + "," + min.getZ() + " a " + 
                                          max.getX() + "," + max.getY() + "," + max.getZ()).formatted(Formatting.YELLOW), false);
            
            player.sendMessage(Text.literal("Agáchate y haz click derecho para activar/desactivar el contorno.").formatted(Formatting.AQUA), false);
        }
        
        return ActionResult.SUCCESS;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlotControlBlockEntity(pos, state);
    }
} 