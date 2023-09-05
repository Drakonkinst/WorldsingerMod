package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.FluidShapes;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
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

    protected final AetherSporeType aetherSporeType;
    private Block solidBlock = null;

    public AetherSporeFluidBlock(FlowableFluid fluid, AetherSporeType sporeType,
            Settings settings) {
        super(fluid, settings);
        this.aetherSporeType = sporeType;
    }

    // Returns whether a block should fluidize based on fluidizeSource, which is
    // generally the block underneath it
    public static boolean shouldFluidize(BlockState fluidizeSource) {
        return fluidizeSource.isOf(Blocks.MAGMA_BLOCK)
                || AetherSporeFluidBlock.isFluidSourceSpores(fluidizeSource)
                || AetherSporeFluidBlock.isFluidloggedInSpores(fluidizeSource);
    }

    // Update fluidization from a source of spores
    public static void update(WorldAccess world, BlockPos pos, BlockState blockState,
            BlockState fluidizeSource) {
        if (!AetherSporeFluidBlock.isAnySporeSource(blockState)) {
            return;
        }

        BlockPos.Mutable mutable = pos.mutableCopy();
        boolean fluidized = AetherSporeFluidBlock.shouldFluidize(fluidizeSource);

        // Iterate upwards and update fluidization
        while (AetherSporeFluidBlock.updateFluidizationForBlock(world, mutable,
                world.getBlockState(mutable), fluidized) && mutable.getY() < world.getTopY()) {
            mutable.move(Direction.UP);
        }
    }

    // Update fluidization for a single block. Returns false if obstructed
    public static boolean updateFluidizationForBlock(WorldAccess world, BlockPos pos,
            BlockState blockState,
            boolean fluidized) {
        if (fluidized) {
            return fluidizeBlock(world, pos, blockState);
        } else {
            return solidifyBlock(world, pos, blockState);
        }
    }

    // Ensure block is fluidized. Returns false if obstructed
    private static boolean fluidizeBlock(WorldAccess world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();

        // No need to modify fluid blocks
        if (block instanceof AetherSporeFluidBlock || AetherSporeFluidBlock.isFluidloggedInSpores(
                blockState)) {
            return true;
        }

        // Turn solid into fluid
        if (block instanceof AetherSporeBlock aetherSporeBlock) {
            BlockState newBlockState = aetherSporeBlock.getFluidizedBlock().getDefaultState();
            return world.setBlockState(pos, newBlockState, Block.NOTIFY_LISTENERS);
        }

        return false;
    }

    private static boolean solidifyBlock(WorldAccess world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();

        // No need to modify solid blocks
        if (block instanceof AetherSporeBlock) {
            return true;
        }

        // Turn fluid into solid
        if (block instanceof AetherSporeFluidBlock aetherSporeFluidBlock) {
            BlockState newBlockState = aetherSporeFluidBlock.getSolidBlock().getDefaultState();
            return world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
        }

        // Dissolve fluidlogged fluid into particles
        if (blockState.getBlock() instanceof Waterloggable waterloggable
                && AetherSporeFluidBlock.isFluidloggedInSpores(blockState)) {
            Fluid fluid = blockState.getFluidState().getFluid();
            // Spawn dissolving particles
            if (fluid instanceof AetherSporeFluid sporeFluid
                    && world instanceof ServerWorld serverWorld) {
                SporeParticles.spawnBlockParticles(serverWorld, sporeFluid.getSporeType(),
                        pos, 0.6, 1.0);
            }
            waterloggable.tryDrainFluid(world, pos, blockState);
            return true;
        }

        return false;
    }

    private static boolean isAnySporeSource(BlockState state) {
        return AetherSporeFluidBlock.isSolidSpores(state)
                || AetherSporeFluidBlock.isFluidSourceSpores(state)
                || AetherSporeFluidBlock.isFluidloggedInSpores(state);
    }

    private static boolean isSolidSpores(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_BLOCKS);
    }

    private static boolean isFluidSourceSpores(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                && state.getFluidState().getLevel() >= 8 && state.getFluidState().isStill();
    }

    private static boolean isFluidloggedInSpores(BlockState state) {
        return state.getBlock() instanceof Waterloggable && state.getFluidState()
                .isIn(ModFluidTags.AETHER_SPORES);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            // If the block beneath is changed, update fluidization
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
        // Spawn splash particles upon landing (during stilling)
        if (fallDistance > 0.25f && world instanceof ServerWorld serverWorld) {
            SporeParticles.spawnSplashParticles(serverWorld, aetherSporeType, entity,
                    fallDistance, false);
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit,
            ProjectileEntity projectile) {
        // Spawn projectile particles upon landing (during stilling)
        if (world instanceof ServerWorld serverWorld) {
            SporeParticles.spawnProjectileParticles(serverWorld, aetherSporeType,
                    projectile.getPos());
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        FluidState fluidState = state.getFluidState();

        // If ShapeContext implements WorldShapeContext, use it to make the block solid
        // during stillings (mainly for entities). Otherwise, assume it is transparent.
        if (fluidState.isStill() && context instanceof WorldShapeContext shapeContext) {
            World realWorld = shapeContext.worldsinger$getWorld();
            if (realWorld != null && !LumarSeethe.areSporesFluidized(realWorld)) {
                return FluidShapes.VOXEL_SHAPES[fluidState.getLevel()];
            }
        }

        return super.getCollisionShape(state, world, pos, context);
    }

    public AetherSporeType getSporeType() {
        return aetherSporeType;
    }

    public Block getSolidBlock() {
        return solidBlock;
    }

    public void setSolidBlock(Block block) {
        this.solidBlock = block;
    }
}
