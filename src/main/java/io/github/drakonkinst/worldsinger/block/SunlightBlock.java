package io.github.drakonkinst.worldsinger.block;

import com.mojang.serialization.MapCodec;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import java.util.function.ToIntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SunlightBlock extends Block {

    public static final MapCodec<SunlightBlock> CODEC = createCodec(SunlightBlock::new);
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = (state) -> {
        int level = state.get(ModProperties.SUNLIGHT_LEVEL);
        if (level == 1) {
            // Equal to Magma Block
            return 3;
        }
        if (level == 2) {
            // Half luminance
            return 8;
        }
        // Full luminance
        return 15;
    };

    private static final float DAMAGE_PER_TICK = 1.0f;

    public SunlightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(ModProperties.SUNLIGHT_LEVEL, 3));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(ModProperties.SUNLIGHT_LEVEL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return context.isHolding(ModBlocks.SUNLIGHT.asItem()) ? VoxelShapes.fullCube()
                : VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(this)) {
            return true;
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return this.asItem().getDefaultStack();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int level = state.get(ModProperties.SUNLIGHT_LEVEL);
        if (level > 1) {
            world.setBlockState(pos, state.with(ModProperties.SUNLIGHT_LEVEL, level - 1));
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.isFireImmune()) {
            entity.setFireTicks(entity.getFireTicks() + 1);
            if (entity.getFireTicks() == 0) {
                entity.setOnFireFor(8);
            }
        }
        entity.damage(world.getDamageSources().inFire(), DAMAGE_PER_TICK);
        entity.slowMovement(state, new Vec3d(0.5, 0.8, 0.5));
        super.onEntityCollision(state, world, pos, entity);
    }

    public MapCodec<? extends SunlightBlock> getCodec() {
        return CODEC;
    }
}
