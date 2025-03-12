/**
 * @author: NightClubCraft
 * @version: 1.0.0
 * @since: 1.0.0
 * @description: Bloque de valla para delimitar los plots.
 * Utiliza un modelo de valla metálica para crear límites claros entre plots.
 * 
 * @see: Block
 * @see: BlockState
 * @see: ShapeContext
 * @see: PlayerEntity
 * @see: ItemPlacementContext
 * @see: StateManager
 * @see: DirectionProperty
 * @see: Properties
 * @see: ActionResult
 * @see: BlockMirror
 * @see: BlockRotation
 * @see: Hand
 * @see: BlockHitResult
 * 
 * # NOTAS: Esta mierda me va a matar...
 */

package nightclubcraftmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
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
import org.jetbrains.annotations.Nullable;

/**
 * Bloque de valla para delimitar los plots.
 * Utiliza un modelo de valla metálica para crear límites claros entre plots.
 */
public class PlotFenceBlock extends Block {
    
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    
    // Formas de colisión para las diferentes orientaciones
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.createCuboidShape(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
    private static final VoxelShape SHAPE_EAST_WEST = Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
    
    public PlotFenceBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(FACING, Direction.NORTH));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return direction.getAxis() == Direction.Axis.Z ? SHAPE_NORTH_SOUTH : SHAPE_EAST_WEST;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Use the same shape for collision
        return getOutlineShape(state, world, pos, context);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Orientar la valla perpendicular a la dirección del jugador
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }
    
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Si el jugador está en modo creativo y agachado, eliminar el bloque
        if (player.isSneaking() && player.isCreative()) {
            world.removeBlock(pos, false);
            return ActionResult.success(world.isClient);
        }
        
        return ActionResult.PASS;
    }
} 