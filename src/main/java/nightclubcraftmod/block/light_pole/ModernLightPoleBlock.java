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
 * Poste de luz con diseño moderno y minimalista.
 * Utiliza textura de hierro para un aspecto industrial y moderno.
 * Emite una luz brillante blanca.
 */
public class ModernLightPoleBlock extends LightPoleBlock {
    
    // Formas personalizadas para este modelo específico - Adaptadas para textura de hierro
    private static final VoxelShape UPPER_LIGHT_NORTH = VoxelShapes.union(
        UPPER_SHAPE_NORTH,
        Block.createCuboidShape(6.0, 8.0, 2.0, 10.0, 12.0, 6.0), // Lámpara cuadrada en el extremo
        Block.createCuboidShape(7.0, 7.0, 3.0, 9.0, 13.0, 5.0)   // Detalles de la lámpara
    );
    
    private static final VoxelShape UPPER_LIGHT_SOUTH = VoxelShapes.union(
        UPPER_SHAPE_SOUTH,
        Block.createCuboidShape(6.0, 8.0, 10.0, 10.0, 12.0, 14.0), // Lámpara cuadrada en el extremo
        Block.createCuboidShape(7.0, 7.0, 11.0, 9.0, 13.0, 13.0)   // Detalles de la lámpara
    );
    
    private static final VoxelShape UPPER_LIGHT_EAST = VoxelShapes.union(
        UPPER_SHAPE_EAST,
        Block.createCuboidShape(10.0, 8.0, 6.0, 14.0, 12.0, 10.0), // Lámpara cuadrada en el extremo
        Block.createCuboidShape(11.0, 7.0, 7.0, 13.0, 13.0, 9.0)   // Detalles de la lámpara
    );
    
    private static final VoxelShape UPPER_LIGHT_WEST = VoxelShapes.union(
        UPPER_SHAPE_WEST,
        Block.createCuboidShape(2.0, 8.0, 6.0, 6.0, 12.0, 10.0), // Lámpara cuadrada en el extremo
        Block.createCuboidShape(3.0, 7.0, 7.0, 5.0, 13.0, 9.0)   // Detalles de la lámpara
    );
    
    // Forma para la base metálica
    private static final VoxelShape LOWER_METAL_BASE = VoxelShapes.union(
        Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 2.0, 10.0), // Base cuadrada
        Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)   // Poste central
    );
    
    public ModernLightPoleBlock(Settings settings) {
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
            return LOWER_METAL_BASE; // Base metálica para la parte inferior
        }
    }
    
    @Override
    public int getLightLevel() {
        return 15; // Luz brillante
    }
} 