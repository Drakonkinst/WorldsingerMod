package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.util.Constants;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class AetherSporeBlock extends FallingBlock implements FluidDrainable {

    private final BlockState fluidizedState;
    private final int color;

    public AetherSporeBlock(Block fluidized, int color, Settings settings) {
        super(settings);
        this.color = color;
        this.fluidizedState = fluidized.getDefaultState();
        if (fluidized instanceof AetherSporeFluidBlock aetherSporeFluidBlock) {
            aetherSporeFluidBlock.setSolidBlockState(this.getDefaultState());
        } else {
            Constants.LOGGER.error("Expected fluidized block for " + this.getClass().getName() +
                    " to be an instance of AetherSporeFluidBlock");
        }
    }

    public BlockState getFluidizedState() {
        return fluidizedState;
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        // TODO: May want to tag or make a new entity so that it produces a spore explosion upon landing in this manner
        entity.dropItem = false;
    }

    // @Override
    // public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    //     if (!AetherSporeBlock.canFallThrough(world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) {
    //         return;
    //     }
    //     FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
    //     this.configureFallingBlockEntity(fallingBlockEntity);
    // }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState,
            BlockState currentStateInPos,
            FallingBlockEntity fallingBlockEntity) {
        if (AetherSporeFluidBlock.shouldFluidize(world.getBlockState(pos.down()))) {
            world.setBlockState(pos, this.fluidizedState, Block.NOTIFY_ALL);
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

    // @Override
    // public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
    //         ItemStack itemStack) {
    //     update(world, pos, state, world.getBlockState(pos.down()));
    //     super.onPlaced(world, pos, state, placer, itemStack);
    // }

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
    public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(),
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        if (!world.isClient()) {
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
        }
        return new ItemStack(ModItems.VERDANT_SPORES_BUCKET);
    }

    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        // TODO: Change to unique sound
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW);
    }

    @Override
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return color;
    }
}
