package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.fluid.FluidShapes;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class AetherSporeFluidBlock extends FluidBlock implements SporeEmitting {

    public static void update(WorldAccess world, BlockPos pos, BlockState currentBlockState,
            BlockState fluidizedSource) {
        if (!hasAnyAetherSporesSource(currentBlockState)) {
            return;
        }

        BlockPos.Mutable mutable = pos.mutableCopy();
        boolean fluidized = AetherSporeFluidBlock.shouldFluidize(fluidizedSource);
        AetherSporeFluidBlock.updateFluidization(world, mutable, currentBlockState, fluidized);
        while (true) {
            mutable.move(Direction.UP);
            BlockState blockStateAtPos = world.getBlockState(mutable);
            if (!updateFluidization(world, mutable, blockStateAtPos, fluidized)) {
                break;
            }
        }
    }

    // Returns true if it should keep going
    public static boolean updateFluidization(WorldAccess world, BlockPos pos,
            BlockState blockState,
            boolean fluidized) {
        if (AetherSporeFluidBlock.isAetherSporesSolid(blockState) &&
                blockState.getBlock() instanceof AetherSporeBlock aetherSporeBlock) {
            if (fluidized) {
                if (!world.setBlockState(pos, aetherSporeBlock.getFluidizedState(),
                        Block.NOTIFY_LISTENERS)) {
                    return false;
                }
            }
            return true;
        } else if (AetherSporeFluidBlock.isAetherSporesFluidSource(blockState) &&
                blockState.getBlock() instanceof AetherSporeFluidBlock aetherSporeFluidBlock) {
            if (!fluidized) {
                if (!world.setBlockState(pos, aetherSporeFluidBlock.getSolidState(),
                        Block.NOTIFY_LISTENERS)) {
                    return false;
                }
            }
            return true;
        } else if (AetherSporeFluidBlock.isAetherSporesFluidlogged(blockState)
                && blockState.getBlock() instanceof Waterloggable waterloggable) {
            if (!fluidized) {
                // TODO: Add particle event
                // TODO: Maybe place the block above?
                waterloggable.tryDrainFluid(world, pos, blockState);
            }
            return true;
        }
        return false;
    }

    private static boolean hasAnyAetherSporesSource(BlockState state) {
        return isAetherSporesSolid(state) || isAetherSporesFluidSource(state)
                || isAetherSporesFluidlogged(state);
    }

    private static boolean isAetherSporesSolid(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_BLOCKS);
    }

    private static boolean isAetherSporesFluidSource(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                && state.getFluidState().getLevel() >= 8
                &&
                state.getFluidState().isStill();
    }

    private static boolean isAetherSporesFluidlogged(BlockState state) {
        return state.getBlock() instanceof Waterloggable && state.getFluidState()
                .isIn(ModFluidTags.AETHER_SPORES);
    }

    public static boolean shouldFluidize(BlockState fluidizedSource) {
        return fluidizedSource.isOf(Blocks.MAGMA_BLOCK) || isAetherSporesFluidSource(
                fluidizedSource) || isAetherSporesFluidlogged(fluidizedSource);
    }

    private BlockState solidBlockState = null;
    private final AetherSporeType aetherSporeType;

    public AetherSporeFluidBlock(FlowableFluid fluid, AetherSporeType aetherSporeType,
            Settings settings) {
        super(fluid, settings);
        this.aetherSporeType = aetherSporeType;
    }

    public void setSolidBlockState(BlockState blockState) {
        this.solidBlockState = blockState;
    }

    public BlockState getSolidState() {
        return solidBlockState;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleFluidTick(pos, fluid, fluid.getTickRate(world));
        if (!state.canPlaceAt(world, pos) || direction == Direction.DOWN ||
                direction == Direction.UP && !neighborState.isIn(
                        ModBlockTags.AETHER_SPORE_SEA_BLOCKS) &&
                        AetherSporeFluidBlock.isAetherSporesSolid(neighborState)) {
            world.scheduleBlockTick(pos, this, 5);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        AetherSporeFluidBlock.update(world, pos, state, world.getBlockState(pos.down()));
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity,
            float fallDistance) {
        if (fallDistance > 0.25f && !world.isClient() && world instanceof ServerWorld serverWorld) {
            SporeParticleManager.spawnLandingParticles(serverWorld, aetherSporeType, entity,
                    fallDistance);
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    public AetherSporeType getSporeType() {
        return aetherSporeType;
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit,
            ProjectileEntity projectile) {
        if (world instanceof ServerWorld serverWorld) {
            SporeParticleManager.spawnProjectileParticles(serverWorld, aetherSporeType,
                    projectile.getPos());
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        FluidState fluidState = state.getFluidState();
        if (fluidState.isStill() && context instanceof WorldShapeContext shapeContext) {
            World realWorld = shapeContext.worldsinger$getWorld();
            if (realWorld != null && !LumarSeetheManager.areSporesFluidized(
                    realWorld)) {
                return FluidShapes.VOXEL_SHAPES[fluidState.getLevel()];
            }
        }

        return super.getCollisionShape(state, world, pos, context);
    }
}
