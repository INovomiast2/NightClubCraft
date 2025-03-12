package nightclubcraftmod.block.light_pole;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

/**
 * Poste de luz con diseño futurista y avanzado.
 * Utiliza textura de pilar de cuarzo para un aspecto elegante y moderno.
 * Emite una luz azulada de alta tecnología.
 */
public class FuturisticLightPoleBlock extends LightPoleBlock {
    
    // Formas personalizadas para este modelo específico - Adaptadas para textura de cuarzo
    private static final VoxelShape UPPER_LIGHT_NORTH = VoxelShapes.union(
        UPPER_SHAPE_NORTH,
        Block.createCuboidShape(5.0, 7.0, 0.0, 11.0, 13.0, 6.0),  // Panel de luz futurista
        Block.createCuboidShape(6.0, 8.0, 6.0, 10.0, 12.0, 8.0),  // Conector tecnológico
        Block.createCuboidShape(7.0, 9.0, 0.0, 9.0, 11.0, 8.0)    // Rayo de luz
    );
    
    private static final VoxelShape UPPER_LIGHT_SOUTH = VoxelShapes.union(
        UPPER_SHAPE_SOUTH,
        Block.createCuboidShape(5.0, 7.0, 10.0, 11.0, 13.0, 16.0), // Panel de luz futurista
        Block.createCuboidShape(6.0, 8.0, 8.0, 10.0, 12.0, 10.0),  // Conector tecnológico
        Block.createCuboidShape(7.0, 9.0, 8.0, 9.0, 11.0, 16.0)    // Rayo de luz
    );
    
    private static final VoxelShape UPPER_LIGHT_EAST = VoxelShapes.union(
        UPPER_SHAPE_EAST,
        Block.createCuboidShape(10.0, 7.0, 5.0, 16.0, 13.0, 11.0), // Panel de luz futurista
        Block.createCuboidShape(8.0, 8.0, 6.0, 10.0, 12.0, 10.0),  // Conector tecnológico
        Block.createCuboidShape(8.0, 9.0, 7.0, 16.0, 11.0, 9.0)    // Rayo de luz
    );
    
    private static final VoxelShape UPPER_LIGHT_WEST = VoxelShapes.union(
        UPPER_SHAPE_WEST,
        Block.createCuboidShape(0.0, 7.0, 5.0, 6.0, 13.0, 11.0),  // Panel de luz futurista
        Block.createCuboidShape(6.0, 8.0, 6.0, 8.0, 12.0, 10.0),  // Conector tecnológico
        Block.createCuboidShape(0.0, 9.0, 7.0, 8.0, 11.0, 9.0)    // Rayo de luz
    );
    
    // Forma para el poste inferior con detalles de cuarzo
    private static final VoxelShape LOWER_QUARTZ_POLE = VoxelShapes.union(
        Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),   // Poste central
        Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 2.0, 10.0),  // Base elegante
        Block.createCuboidShape(6.5, 4.0, 6.5, 9.5, 6.0, 9.5),    // Anillo decorativo 1
        Block.createCuboidShape(6.5, 10.0, 6.5, 9.5, 12.0, 9.5)   // Anillo decorativo 2
    );
    
    public FuturisticLightPoleBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            return switch (state.get(FACING)) {
                case NORTH -> UPPER_LIGHT_NORTH;
                case SOUTH -> UPPER_LIGHT_SOUTH;
                case EAST -> UPPER_LIGHT_EAST;
                case WEST -> UPPER_LIGHT_WEST;
                default -> UPPER_LIGHT_NORTH;
            };
        } else {
            return LOWER_QUARTZ_POLE; // Poste de cuarzo para la parte inferior
        }
    }
    
    @Override
    public int getLightLevel() {
        return 14; // Luz intensa azulada
    }
} 