package nightclubcraftmod.block.light_pole;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

/**
 * Clase base para los postes de luz.
 * Implementa la funcionalidad común a todos los modelos de postes de luz.
 */
public abstract class LightPoleBlock extends Block {
    
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    
    // Formas de colisión para las diferentes partes del poste
    protected static final VoxelShape LOWER_SHAPE_NORTH_SOUTH = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    protected static final VoxelShape LOWER_SHAPE_EAST_WEST = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    
    protected static final VoxelShape UPPER_SHAPE_NORTH = VoxelShapes.union(
        Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),  // Poste vertical
        Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 12.0, 16.0) // Brazo horizontal
    );
    
    protected static final VoxelShape UPPER_SHAPE_SOUTH = VoxelShapes.union(
        Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),  // Poste vertical
        Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 12.0, 16.0) // Brazo horizontal
    );
    
    protected static final VoxelShape UPPER_SHAPE_EAST = VoxelShapes.union(
        Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),  // Poste vertical
        Block.createCuboidShape(0.0, 8.0, 6.0, 16.0, 12.0, 10.0) // Brazo horizontal
    );
    
    protected static final VoxelShape UPPER_SHAPE_WEST = VoxelShapes.union(
        Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),  // Poste vertical
        Block.createCuboidShape(0.0, 8.0, 6.0, 16.0, 12.0, 10.0) // Brazo horizontal
    );
    
    public LightPoleBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(HALF, DoubleBlockHalf.LOWER));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return direction.getAxis() == Direction.Axis.Z ? LOWER_SHAPE_NORTH_SOUTH : LOWER_SHAPE_EAST_WEST;
        } else {
            return switch (direction) {
                case NORTH -> UPPER_SHAPE_NORTH;
                case SOUTH -> UPPER_SHAPE_SOUTH;
                case EAST -> UPPER_SHAPE_EAST;
                case WEST -> UPPER_SHAPE_WEST;
                default -> LOWER_SHAPE_NORTH_SOUTH; // No debería ocurrir
            };
        }
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        
        // Verificar si hay espacio para la parte superior
        if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
        
        return null;
    }
    
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
    }
    
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState lowerState = world.getBlockState(pos.down());
            return lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return super.canPlaceAt(state, world, pos);
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);
        
        if (direction.getAxis() == Direction.Axis.Y && 
            ((half == DoubleBlockHalf.LOWER && direction == Direction.UP) || 
             (half == DoubleBlockHalf.UPPER && direction == Direction.DOWN))) {
            
            if (!neighborState.isOf(this) || neighborState.get(HALF) == half) {
                return Blocks.AIR.getDefaultState();
            }
        }
        
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            DoubleBlockHalf half = state.get(HALF);
            
            if (half == DoubleBlockHalf.UPPER) {
                BlockPos lowerPos = pos.down();
                BlockState lowerState = world.getBlockState(lowerPos);
                
                if (lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER) {
                    world.setBlockState(lowerPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                }
            }
        }
        
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Verificar si el jugador está presionando Shift (sneaking)
        if (player.isSneaking()) {
            // Cancelar la selección (eliminar el bloque)
            if (!world.isClient) {
                // Determinar qué parte del poste se está interactuando
                DoubleBlockHalf half = state.get(HALF);
                
                if (half == DoubleBlockHalf.LOWER) {
                    // Si es la parte inferior, eliminar ambas partes
                    BlockPos upperPos = pos.up();
                    BlockState upperState = world.getBlockState(upperPos);
                    
                    if (upperState.isOf(this) && upperState.get(HALF) == DoubleBlockHalf.UPPER) {
                        world.removeBlock(upperPos, false);
                    }
                    world.removeBlock(pos, false);
                } else {
                    // Si es la parte superior, eliminar ambas partes
                    BlockPos lowerPos = pos.down();
                    BlockState lowerState = world.getBlockState(lowerPos);
                    
                    if (lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER) {
                        world.removeBlock(lowerPos, false);
                    }
                    world.removeBlock(pos, false);
                }
                
                // Devolver el ítem al jugador si no está en modo creativo
                if (!player.isCreative()) {
                    ItemStack itemStack = new ItemStack(this);
                    player.getInventory().insertStack(itemStack);
                }
            }
            
            return ActionResult.success(world.isClient);
        }
        
        return ActionResult.PASS;
    }
    
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    
    /**
     * Obtiene el nivel de luminosidad del poste de luz.
     * Cada modelo puede tener un nivel diferente.
     * 
     * @return El nivel de luminosidad (0-15)
     */
    public abstract int getLightLevel();
} 