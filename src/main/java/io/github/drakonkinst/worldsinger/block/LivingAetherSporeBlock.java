package io.github.drakonkinst.worldsinger.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.drakonkinst.worldsinger.fluid.SunlightSporeFluid;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.world.WaterReactionManager;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LivingAetherSporeBlock extends AetherSporeBlock implements SporeKillable,
        WaterReactiveBlock {

    public static final MapCodec<LivingAetherSporeBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    AetherSporeType.CODEC.fieldOf("sporeType")
                            .forGetter(LivingAetherSporeBlock::getSporeType),
                    Block.CODEC.fieldOf("block")
                            .forGetter(LivingAetherSporeBlock::getFluidizedBlock),
                    createSettingsCodec()
            ).apply(instance, LivingAetherSporeBlock::new));

    public static final int CATALYZE_VALUE = 250;

    public LivingAetherSporeBlock(AetherSporeType aetherSporeType, Block fluidized,
            Settings settings) {
        super(aetherSporeType, settings);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos waterNeighborPos = LivingVerdantVineBlock.getWaterNeighborPos(world, pos);
        if (waterNeighborPos != null) {
            WaterReactionManager.catalyzeAroundWater(world, waterNeighborPos);
            if (aetherSporeType == AetherSporeType.VERDANT) {
                world.setBlockState(pos, ModBlocks.VERDANT_VINE_BLOCK.getDefaultState()
                        .with(ModProperties.CATALYZED, true));
            } else if (aetherSporeType == AetherSporeType.CRIMSON) {
                world.setBlockState(pos, ModBlocks.CRIMSON_GROWTH.getDefaultState()
                        .with(ModProperties.CATALYZED, true));
            } else if (aetherSporeType == AetherSporeType.SUNLIGHT) {
                world.setBlockState(pos, ModBlocks.SUNLIGHT.getDefaultState());
            }
            // TODO: Add remaining spore interactions
            return;
        }
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }
    }

    @Override
    public boolean canReactToWater(BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random) {
        world.removeBlock(pos, false);
        if (aetherSporeType == AetherSporeType.VERDANT) {
            SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos.toCenterPos(), CATALYZE_VALUE,
                    waterAmount, true, false, false);
        } else if (aetherSporeType == AetherSporeType.CRIMSON) {
            SporeGrowthSpawner.spawnCrimsonSporeGrowth(world, pos.toCenterPos(), CATALYZE_VALUE,
                    waterAmount, true, false, false);
        } else if (aetherSporeType == AetherSporeType.SUNLIGHT) {
            SunlightSporeFluid.spreadSunlightBlocks(world, pos, waterAmount, random);
        }
        // TODO: Add remaining spore logic
        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world instanceof World realWorld) {
            BlockPos waterNeighborPos = LivingVerdantVineBlock.getWaterNeighborPos(world, pos);
            if (waterNeighborPos != null) {
                WaterReactionManager.catalyzeAroundWater(realWorld, waterNeighborPos);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_SPORE_BLOCK;
    }

    @Override
    protected MapCodec<? extends LivingAetherSporeBlock> getCodec() {
        return CODEC;
    }
}
