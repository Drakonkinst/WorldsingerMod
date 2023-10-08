package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.registry.ModDamageTypes;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class TallCrimsonSpinesBlock extends Block implements Waterloggable, SporeGrowthBlock {

    private static final double WIDTH = 3.0;
    private static final VoxelShape SHAPE_LOWER = Block.createCuboidShape(
            WIDTH, 0.0, WIDTH,
            16.0 - WIDTH, 16.0, 16.0 - WIDTH);
    private static final VoxelShape SHAPE_UPPER = Block.createCuboidShape(
            WIDTH, 0.0, WIDTH,
            16.0 - WIDTH, 15.0, 16.0 - WIDTH);
    private static final VoxelShape DAMAGE_SHAPE = Block.createCuboidShape(WIDTH, 15.0, WIDTH,
            16.0 - WIDTH, 16.0, 16.0 - WIDTH);
    private static final VoxelShape SAFE_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0,
            16.0);

    public static void placeAt(WorldAccess world, BlockState state, BlockPos pos, int flags) {
        BlockPos abovePos = pos.up();
        world.setBlockState(pos, TallPlantBlock.withWaterloggedState(world, pos,
                state.with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)), flags);
        world.setBlockState(abovePos, TallPlantBlock.withWaterloggedState(world, abovePos,
                state.with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)), flags);
    }

    private static BlockState withFluidState(WorldView world, BlockPos pos, BlockState state) {
        state = state.with(Properties.WATERLOGGED, world.isWater(pos));
        int fluidIndex = Fluidlogged.getFluidIndex(world.getFluidState(pos).getFluid());
        if (fluidIndex > -1) {
            state = state.with(ModProperties.FLUIDLOGGED, fluidIndex);
        }
        return state;
    }

    public TallCrimsonSpinesBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.PERSISTENT, false)
                .with(Properties.WATERLOGGED, false)
                .with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        DoubleBlockHalf half = state.get(Properties.DOUBLE_BLOCK_HALF);
        if (!(direction.getAxis() != Direction.Axis.Y
                || half == DoubleBlockHalf.LOWER != (direction == Direction.UP)
                || neighborState.isIn(ModBlockTags.TALL_CRIMSON_SPINES)
                && neighborState.get(Properties.DOUBLE_BLOCK_HALF) != half)) {
            return Blocks.AIR.getDefaultState();
        }
        if (half == DoubleBlockHalf.LOWER
                && direction == Direction.DOWN
                && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return switch(state.get(Properties.DOUBLE_BLOCK_HALF)) {
            case UPPER -> SHAPE_UPPER;
            case LOWER -> SHAPE_LOWER;
        };
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // Spikes do not destroy items
        if (entity instanceof ItemEntity) {
            return;
        }

        // Only spike tips can damage entities
        if (state.get(Properties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        if (!CrimsonSpikeBlock.isMoving(entity)) {
            return;
        }

        VoxelShape entityShape = VoxelShapes.cuboid(
                entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()));

        // Only damage if entity is at the tip of the spike, not the sides
        if (VoxelShapes.matchesAnywhere(entityShape, SAFE_SHAPE, BooleanBiFunction.AND)
                || !VoxelShapes.matchesAnywhere(entityShape, DAMAGE_SHAPE, BooleanBiFunction.AND)) {
            return;
        }
        entity.damage(ModDamageTypes.of(world, ModDamageTypes.SPIKE), 2.0f);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (pos.getY() >= world.getTopY() - 1 || !world.getBlockState(pos.up())
                .canReplace(ctx)) {
            return null;
        }
        return this.getDefaultState()
                .with(Properties.PERSISTENT, true)
                .with(Properties.WATERLOGGED, world.isWater(pos));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        BlockPos abovePos = pos.up();
        BlockState aboveState = this.getStateWithProperties(state)
                .with(Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
        world.setBlockState(abovePos,
                TallCrimsonSpinesBlock.withFluidState(world, abovePos, aboveState));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);
        if (state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            Worldsinger.LOGGER.info("A " + (belowState.isIn(ModBlockTags.TALL_CRIMSON_SPINES)
                    && belowState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER));
            return belowState.isIn(ModBlockTags.TALL_CRIMSON_SPINES)
                    && belowState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        }
        return belowState.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            if (player.isCreative()) {
                // Destroy bottom half without dropping an item
                BlockPos belowPos = pos.down();
                BlockState belowState = world.getBlockState(belowPos);
                DoubleBlockHalf half = state.get(Properties.DOUBLE_BLOCK_HALF);
                if (half == DoubleBlockHalf.UPPER && belowState.isIn(
                        ModBlockTags.TALL_CRIMSON_SPINES)
                        && belowState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                    Fluid fluidAtPos = Fluidlogged.getFluid(belowState);
                    BlockState nextBlockState;
                    if (fluidAtPos == null) {
                        nextBlockState = Blocks.AIR.getDefaultState();
                    } else {
                        nextBlockState = fluidAtPos.getDefaultState().getBlockState();
                    }
                    world.setBlockState(belowPos, nextBlockState,
                            Block.NOTIFY_ALL | Block.SKIP_DROPS);
                    world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, belowPos,
                            Block.getRawIdFromState(belowState));
                }
            } else {
                Block.dropStacks(state, world, pos, null, player, player.getMainHandStack());
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
            @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, tool);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity,
            float fallDistance) {
        if (state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            entity.handleFallDamage(fallDistance + 1.0f, 1.5f,
                    ModDamageTypes.of(world, ModDamageTypes.SPIKE_FALL));
        } else {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(Properties.PERSISTENT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        // Decay over time
        if (SporeGrowthBlock.canDecay(world, pos, state, random)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Override
    public float getMaxHorizontalModelOffset() {
        return 0.125f;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.PERSISTENT, Properties.WATERLOGGED,
                Properties.DOUBLE_BLOCK_HALF);
    }
}
