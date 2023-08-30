package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.util.Constants;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticles;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class AetherSporeBlock extends FallingBlock implements FluidDrainable, SporeEmitting {

    private final AetherSporeType aetherSporeType;
    private final BlockState fluidizedState;

    public AetherSporeBlock(AetherSporeType aetherSporeType, Block fluidized, Settings settings) {
        super(settings);
        this.aetherSporeType = aetherSporeType;
        this.fluidizedState = fluidized.getDefaultState();
        if (fluidized instanceof AetherSporeFluidBlock aetherSporeFluidBlock) {
            aetherSporeFluidBlock.setSolidBlockState(this.getDefaultState());
        } else {
            Constants.LOGGER.error("Expected fluidized block for " + this.getClass().getName() +
                    " to be an instance of AetherSporeFluidBlock");
        }
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        // TODO: May want to tag or make a new entity so that it produces a spore explosion upon landing in this manner
        entity.dropItem = false;
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState,
            BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (AetherSporeFluidBlock.shouldFluidize(world.getBlockState(pos.down()))) {
            world.setBlockState(pos, this.fluidizedState, Block.NOTIFY_ALL);
        } else if (world instanceof ServerWorld serverWorld) {
            int fallDistance = fallingBlockEntity.getFallingBlockPos().getY() - pos.getY();
            if (fallDistance >= 4) {
                SporeParticles.spawnBlockParticles(serverWorld, aetherSporeType, pos, 1.5, 0.45);
            } else {
                SporeParticles.spawnBlockParticles(serverWorld, aetherSporeType, pos, 0.75, 0.5);
            }
        }
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos,
            FallingBlockEntity fallingBlockEntity) {
        if (world instanceof ServerWorld serverWorld) {
            SporeParticles.spawnBlockParticles(serverWorld, aetherSporeType, pos, 2.5, 0.45);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        BlockState fluidizedSource = blockView.getBlockState(blockPos.down());
        if (AetherSporeFluidBlock.shouldFluidize(fluidizedSource)) {
            return this.fluidizedState;
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(),
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        if (!world.isClient()) {
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
        }
        return new ItemStack(ModItems.VERDANT_SPORES_BUCKET);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity,
            float fallDistance) {
        if (fallDistance > 0.25f && world instanceof ServerWorld serverWorld
                && !(entity instanceof FallingBlockEntity)) {
            SporeParticles.spawnSplashParticles(serverWorld, aetherSporeType, entity,
                    fallDistance, false);
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit,
            ProjectileEntity projectile) {
        if (world instanceof ServerWorld serverWorld) {
            SporeParticles.spawnProjectileParticles(serverWorld, aetherSporeType,
                    projectile.getPos());
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld serverWorld) {
            SporeParticles.spawnBlockParticles(serverWorld, aetherSporeType, pos, 0.6, 1.0);
        }
        super.onBroken(world, pos, state);
    }

    public BlockState getFluidizedState() {
        return fluidizedState;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(this)) {
            return true;
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos,
            NavigationType type) {
        return false;
    }

    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        // TODO: Change to unique sound
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW);
    }

    @Override
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return aetherSporeType.getParticleColor();
    }

    @Override
    public AetherSporeType getSporeType() {
        return aetherSporeType;
    }
}
