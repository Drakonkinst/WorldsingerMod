package io.github.drakonkinst.worldsinger.block;

import com.mojang.serialization.MapCodec;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.registry.ModDamageTypes;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import java.util.function.ToIntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SunlightBlock extends StillFluidBlock {

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

    private static final float DAMAGE_PER_TICK = 4.0f;

    private static boolean isTouchingAnyWater(World world, BlockPos pos) {
        BlockPos.Mutable neighborPos = new BlockPos.Mutable();
        for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
            neighborPos.set(pos.add(direction.getOffsetX(), direction.getOffsetY(),
                    direction.getOffsetZ()));
            if (world.isWater(neighborPos)) {
                return true;
            }
        }
        return false;
    }

    public SunlightBlock(Settings settings) {
        super(ModFluids.SUNLIGHT, settings);
        this.setDefaultState(this.getDefaultState()
                .with(ModProperties.SUNLIGHT_LEVEL, 3));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ModProperties.SUNLIGHT_LEVEL);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos,
            NavigationType type) {
        return false;
    }

    // @Override
    // public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
    //         ShapeContext context) {
    //     return context.isHolding(ModBlocks.SUNLIGHT.asItem()) ? VoxelShapes.fullCube()
    //             : VoxelShapes.empty();
    // }
    //
    // @Override
    // public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos,
    //         ShapeContext context) {
    //     return VoxelShapes.empty();
    // }
    //
    // @Override
    // public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
    //     return VoxelShapes.empty();
    // }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        state.getFluidState().onRandomTick(world, pos, random);

        int level = state.get(ModProperties.SUNLIGHT_LEVEL);
        if (level > 1) {
            world.setBlockState(pos, state.with(ModProperties.SUNLIGHT_LEVEL, level - 1));
        } else if (!SunlightBlock.isTouchingAnyWater(world, pos)) {
            // Will not decay fully if touching water, to halt unnecessary re-catalyzation
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // Same damage and SFX as lava
        if (!entity.isFireImmune()) {
            entity.setOnFireFor(15);
        }

        if (entity.damage(ModDamageTypes.createSource(world, ModDamageTypes.SUNLIGHT),
                DAMAGE_PER_TICK)) {
            entity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f,
                    2.0f + world.getRandom().nextFloat() * 0.4f);
        }

        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        int sunlightLevel = state.get(ModProperties.SUNLIGHT_LEVEL);
        return super.getFluidState(state).with(ModProperties.SUNLIGHT_LEVEL, sunlightLevel);
    }

    public MapCodec<? extends SunlightBlock> getCodec() {
        return CODEC;
    }
}
