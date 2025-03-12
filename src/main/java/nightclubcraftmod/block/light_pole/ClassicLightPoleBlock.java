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
 * Poste de luz con diseño clásico y tradicional.
 * Utiliza textura de valla de roble oscuro para un aspecto rústico y elegante.
 * Emite una luz cálida amarillenta.
 */
public class ClassicLightPoleBlock extends LightPoleBlock {
    
    // Formas personalizadas para este modelo específico - Adaptadas para textura de madera
    private static final VoxelShape UPPER_LIGHT_NORTH = VoxelShapes.union(
        UPPER_SHAPE_NORTH,
        Block.createCuboidShape(5.0, 7.0, 1.0, 11.0, 13.0, 7.0),  // Farol clásico
        Block.createCuboidShape(6.0, 6.0, 2.0, 10.0, 14.0, 6.0),  // Detalles del farol
        Block.createCuboidShape(7.5, 13.0, 3.5, 8.5, 15.0, 4.5)   // Pequeña chimenea
    );
    
    private static final VoxelShape UPPER_LIGHT_SOUTH = VoxelShapes.union(
        UPPER_SHAPE_SOUTH,
        Block.createCuboidShape(5.0, 7.0, 9.0, 11.0, 13.0, 15.0),  // Farol clásico
        Block.createCuboidShape(6.0, 6.0, 10.0, 10.0, 14.0, 14.0), // Detalles del farol
        Block.createCuboidShape(7.5, 13.0, 11.5, 8.5, 15.0, 12.5)  // Pequeña chimenea
    );
    
    private static final VoxelShape UPPER_LIGHT_EAST = VoxelShapes.union(
        UPPER_SHAPE_EAST,
        Block.createCuboidShape(9.0, 7.0, 5.0, 15.0, 13.0, 11.0),  // Farol clásico
        Block.createCuboidShape(10.0, 6.0, 6.0, 14.0, 14.0, 10.0), // Detalles del farol
        Block.createCuboidShape(11.5, 13.0, 7.5, 12.5, 15.0, 8.5)  // Pequeña chimenea
    );
    
    private static final VoxelShape UPPER_LIGHT_WEST = VoxelShapes.union(
        UPPER_SHAPE_WEST,
        Block.createCuboidShape(1.0, 7.0, 5.0, 7.0, 13.0, 11.0),  // Farol clásico
        Block.createCuboidShape(2.0, 6.0, 6.0, 6.0, 14.0, 10.0),  // Detalles del farol
        Block.createCuboidShape(3.5, 13.0, 7.5, 4.5, 15.0, 8.5)   // Pequeña chimenea
    );
    
    // Formas para la base decorativa de madera
    private static final VoxelShape LOWER_WOOD_BASE = VoxelShapes.union(
        Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 3.0, 11.0), // Base cuadrada más grande
        Block.createCuboidShape(6.0, 3.0, 6.0, 10.0, 4.0, 10.0), // Detalle decorativo
        Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)   // Poste central
    );
    
    public ClassicLightPoleBlock(Settings settings) {
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
            return LOWER_WOOD_BASE; // Base decorativa de madera para la parte inferior
        }
    }
    
    @Override
    public int getLightLevel() {
        return 12; // Luz cálida, un poco menos intensa que la moderna
    }
} 