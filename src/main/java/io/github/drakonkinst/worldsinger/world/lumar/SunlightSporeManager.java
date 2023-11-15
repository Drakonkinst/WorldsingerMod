package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;

public final class SunlightSporeManager {

    private static final int MAX_SUNLIGHT_SPREAD_DEPTH = 7;
    private static final int MAX_BLOCKS_AFFECTED = 65;
    private static final int WATER_PER_BLOCK = 12;
    private static final int EFFECT_RADIUS = 2;
    private static final int MAX_BLOCKS_PROCESSED = 1000;

    // Create light and heat
    public static void doSunlightSporeReaction(World world, BlockPos pos, int waterAmount,
            Random random) {
        // Number of blocks of Sunlight generated depends on amount of water
        int numBlocks = waterAmount / WATER_PER_BLOCK;
        if (numBlocks < 0) {
            return;
        }

        int maxBlocks = Math.min(numBlocks, MAX_BLOCKS_AFFECTED);
        Set<BlockPos> affectedBlocks = SunlightSporeManager.spreadSunlightBlocks(world, pos,
                maxBlocks, random);

        // Set blocks on fire, evaporate water, deal damage
        if (!affectedBlocks.isEmpty()) {
            SunlightSporeManager.doReactionEffects(world, pos, affectedBlocks, random);
        }

        // Guaranteed to set entities on fire within a certain radius of the origin
        // Maybe with some knockback?
        // TODO Also add particle effects!
        // world.getEntitiesByClass()
    }

    private static void doReactionEffects(World world, BlockPos originPos,
            Set<BlockPos> affectedBlocks, Random random) {
        LongSet blocksProcessed = new LongOpenHashSet();
        Mutable mutable = new Mutable();
        Mutable mutableDown = new Mutable();
        MutableBoolean anyEvaporated = new MutableBoolean();

        for (BlockPos pos : affectedBlocks) {
            if (blocksProcessed.size() >= MAX_BLOCKS_PROCESSED) {
                break;
            }
            SunlightSporeManager.doReactionEffectsForBlock(world, pos, blocksProcessed, mutable,
                    mutableDown, anyEvaporated, random);
        }
        if (anyEvaporated.booleanValue()) {
            world.playSound(null, originPos, ModSoundEvents.BLOCK_SUNLIGHT_EVAPORATE,
                    SoundCategory.BLOCKS, 1.0f,
                    (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f);
        }

        world.playSound(null, originPos, ModSoundEvents.BLOCK_SUNLIGHT_SPORE_BLOCK_CATALYZE,
                SoundCategory.BLOCKS,
                1.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f);
        Worldsinger.LOGGER.info(blocksProcessed.size() + " blocks processed");
    }

    private static void doReactionEffectsForBlock(World world, BlockPos centerPos,
            LongSet blocksProcessed, Mutable mutable, Mutable mutableDown,
            MutableBoolean anyEvaporated, Random random) {
        blocksProcessed.add(centerPos.asLong());
        for (int offsetX = -EFFECT_RADIUS; offsetX <= EFFECT_RADIUS; ++offsetX) {
            for (int offsetY = -EFFECT_RADIUS; offsetY <= EFFECT_RADIUS; ++offsetY) {
                for (int offsetZ = -EFFECT_RADIUS; offsetZ <= EFFECT_RADIUS; ++offsetZ) {
                    mutable.set(centerPos.getX() + offsetX, centerPos.getY() + offsetY,
                            centerPos.getZ() + offsetZ);
                    if (!blocksProcessed.add(mutable.asLong())) {
                        continue;
                    }
                    mutableDown.set(mutable).move(0, -1, 0);
                    SunlightSporeManager.processBlock(world, mutable, mutableDown, anyEvaporated,
                            random);
                }
            }
        }
    }

    private static void processBlock(World world, BlockPos mutable, BlockPos mutableDown,
            MutableBoolean anyEvaporated, Random random) {
        BlockState state = world.getBlockState(mutable);
        if (state.getBlock() instanceof FluidDrainable fluidDrainable && state.getFluidState()
                .isOf(Fluids.WATER)) {
            // Evaporate water
            fluidDrainable.tryDrainFluid(null, world, mutable, state);
            anyEvaporated.setTrue();
        }

        if (random.nextInt(3) == 0 && state.isAir() && world.getBlockState(mutableDown)
                .isOpaqueFullCube(world, mutableDown)) {
            // Set fire
            world.setBlockState(mutable, AbstractFireBlock.getState(world, mutable));
        }
    }

    // Spread Sunlight blocks using BFS, replacing Sunlight Spore Sea / Sunlight Spore Blocks
    // Places at least one Sunlight block
    // Should place a consistent amount of Sunlight Blocks (if available)
    private static Set<BlockPos> spreadSunlightBlocks(World world, BlockPos startPos, int maxBlocks,
            Random random) {
        Set<BlockPos> affectedBlocks = new HashSet<>();

        // Always generate one block of Sunlight at the point of interaction
        BlockState blockState = world.getBlockState(startPos);
        if (blockState.isOf(ModBlocks.SUNLIGHT_SPORE_SEA) || blockState.isOf(
                ModBlocks.SUNLIGHT_SPORE_BLOCK) || blockState.isOf(Blocks.AIR)) {
            // Not a waterlogged block
            world.setBlockState(startPos, ModBlocks.SUNLIGHT.getDefaultState());
            affectedBlocks.add(startPos);
        }
        if (affectedBlocks.size() >= maxBlocks) {
            return affectedBlocks;
        }

        LongSet visited = new LongOpenHashSet();
        Queue<IntObjectPair<BlockPos>> queue = new ArrayDeque<>();
        queue.add(new IntObjectImmutablePair<>(0, startPos));
        visited.add(startPos.asLong());

        while (!queue.isEmpty() && affectedBlocks.size() < maxBlocks) {
            IntObjectPair<BlockPos> nextPair = queue.remove();
            int depth = nextPair.leftInt();
            BlockPos nextPos = nextPair.right();
            BlockState state = world.getBlockState(nextPos);
            if (SunlightSporeManager.canSunlightReplace(state)) {
                // Replace with Sunlight
                world.setBlockState(nextPos, ModBlocks.SUNLIGHT.getDefaultState());
                affectedBlocks.add(nextPos);
            } else if (state.getFluidState().isOf(ModFluids.SUNLIGHT_SPORES)
                    && state.getBlock() instanceof FluidDrainable fluidDrainable) {
                fluidDrainable.tryDrainFluid(null, world, nextPos, state);
            } else if (!SunlightSporeManager.canSunlightPassThrough(state)) {
                continue;
            }

            if (depth >= MAX_SUNLIGHT_SPREAD_DEPTH) {
                continue;
            }

            // Add neighbors
            for (Direction direction : Direction.shuffle(random)) {
                BlockPos neighborPos = nextPos.add(direction.getOffsetX(), direction.getOffsetY(),
                        direction.getOffsetZ());
                long encodedNeighborPos = neighborPos.asLong();
                if (visited.add(encodedNeighborPos)) {
                    queue.add(new IntObjectImmutablePair<>(depth + 1, neighborPos));
                }
            }
        }
        return affectedBlocks;
    }

    private static boolean canSunlightReplace(BlockState state) {
        return state.isOf(ModBlocks.SUNLIGHT_SPORE_SEA) || state.isOf(
                ModBlocks.SUNLIGHT_SPORE_BLOCK);
    }

    private static boolean canSunlightPassThrough(BlockState state) {
        return state.isOf(ModBlocks.SUNLIGHT) || state.getFluidState()
                .isOf(ModFluids.SUNLIGHT_SPORES);
    }

    private SunlightSporeManager() {}
}
