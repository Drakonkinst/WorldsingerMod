package io.github.drakonkinst.worldsinger.block;

import com.mojang.serialization.MapCodec;
import io.github.drakonkinst.worldsinger.cosmere.WaterReactionManager;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

// Roseite blocks do not have a dead state, and are alive by default
public class RoseiteBlock extends Block implements SporeGrowthBlock, WaterReactiveBlock {

    public static final MapCodec<RoseiteBlock> CODEC = createCodec(RoseiteBlock::new);
    public static final int DECAY_CHANCE = 5;
    public static final int RECATALYZE_VALUE = 100;

    public RoseiteBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.PERSISTENT, false)
                .with(ModProperties.CATALYZED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.PERSISTENT, ModProperties.CATALYZED);
        super.appendProperties(builder);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState != null) {
            placementState = placementState.with(Properties.PERSISTENT, true)
                    .with(ModProperties.CATALYZED, true);
        }
        return placementState;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (this.canReactToWater(pos, state) && world instanceof World realWorld) {
            BlockPos waterNeighborPos = LivingVerdantVineBlock.getWaterNeighborPos(world, pos);
            if (waterNeighborPos != null) {
                WaterReactionManager.catalyzeAroundWater(realWorld, waterNeighborPos);
                state = state.with(ModProperties.CATALYZED, true);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(Properties.PERSISTENT) || !state.get(ModProperties.CATALYZED);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        // Decay over time
        if (SporeGrowthBlock.canDecay(world, pos, state, random)
                && random.nextInt(DECAY_CHANCE) == 0) {
            world.breakBlock(pos, true);
        }

        if (!state.get(ModProperties.CATALYZED) && world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return world.getMaxLightLevel();
    }

    @Override
    public boolean reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random) {
        // TODO
        return false;
    }

    @Override
    public boolean canReactToWater(BlockPos pos, BlockState state) {
        return !state.get(ModProperties.CATALYZED);
    }

    @Override
    public Type getReactiveType() {
        return Type.ROSEITE_SPORES;
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }
}
