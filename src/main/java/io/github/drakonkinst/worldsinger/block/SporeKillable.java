package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.registry.DataTable;
import io.github.drakonkinst.worldsinger.registry.DataTables;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface SporeKillable {

    int MAX_BLOCK_RADIUS = 5;
    double BOAT_RADIUS = 2.0;

    static int killNearbySpores(World world, BlockPos pos, int radius) {
        int numKilled = 0;
        radius = Math.min(radius, SporeKillable.MAX_BLOCK_RADIUS);
        int deadSporeFluidIndex = Fluidlogged.getFluidIndex(ModFluids.DEAD_SPORES);

        // Not sure if this is the right iteration method, but it works
        // TODO: Add directional flags so that other blocks (aluminum) can block this effect)
        for (BlockPos currentPos : BlockPos.iterateOutwards(pos, radius, radius, radius)) {
            if (currentPos.equals(pos)) {
                continue;
            }

            BlockState blockState = world.getBlockState(currentPos);
            boolean wasChanged = false;

            // Kill SporeKillable block
            if (blockState.getBlock() instanceof SporeKillable sporeKillable) {
                blockState = SporeKillable.convertToDeadVariant(sporeKillable, blockState);
                wasChanged = true;
            }

            // Turn living spore fluids into dead spore fluid
            if (blockState.getBlock() instanceof Waterloggable) {
                FluidState fluidState = blockState.getFluidState();
                if (fluidState.isIn(ModFluidTags.AETHER_SPORES) && !fluidState.isIn(
                        ModFluidTags.DEAD_SPORES)) {
                    blockState = blockState.with(ModProperties.FLUIDLOGGED, deadSporeFluidIndex);
                    wasChanged = true;
                }
            }

            if (wasChanged) {
                numKilled += 1;
                world.setBlockState(currentPos, blockState, Block.NOTIFY_ALL);
            }
        }
        return numKilled;
    }

    static boolean isSporeKillingBlockNearby(World world, BlockPos pos) {
        DataTable dataTable = DataTables.getOrElse(world, DataTables.SPORE_KILLING_RADIUS, 0);
        for (BlockPos currentPos : BlockPos.iterateOutwards(pos, SporeKillable.MAX_BLOCK_RADIUS,
                SporeKillable.MAX_BLOCK_RADIUS, SporeKillable.MAX_BLOCK_RADIUS)) {
            BlockState blockState = world.getBlockState(currentPos);
            if (!blockState.isIn(ModBlockTags.KILLS_SPORES)) {
                continue;
            }

            int distance = SporeKillable.getDistance(pos, currentPos);
            if (dataTable.getIntForBlock(blockState) >= distance) {
                return true;
            }
        }
        return false;
    }

    static boolean checkNearbyEntities(World world, Vec3d pos) {
        Box box = Box.of(pos, BOAT_RADIUS, BOAT_RADIUS, BOAT_RADIUS);
        return SporeKillable.checkNearbyEntitiesInBox(world, box);
    }

    static boolean checkNearbyEntitiesForRange(World world, double minX, double minY,
            double minZ, double maxX, double maxY, double maxZ) {
        Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        box = box.expand(BOAT_RADIUS);
        return checkNearbyEntitiesInBox(world, box);
    }

    private static boolean checkNearbyEntitiesInBox(World world, Box box) {
        List<BoatEntity> entitiesInRange = world.getEntitiesByClass(BoatEntity.class, box,
                boatEntity -> {
                    SilverLinedComponent silverData = ModComponents.SILVER_LINED_ENTITY.get(
                            boatEntity);
                    boolean hasSilver = silverData.getSilverDurability() > 0;
                    if (hasSilver) {
                        silverData.setSilverDurability(silverData.getSilverDurability() - 1);
                        ModComponents.SILVER_LINED_ENTITY.sync(boatEntity);
                    }
                    return hasSilver;
                });
        return !entitiesInRange.isEmpty();
    }

    static boolean isSporeKillingBlockNearbyForRange(World world, double minX, double minY,
            double minZ, double maxX, double maxY, double maxZ) {
        return isSporeKillingBlockNearbyForRange(world, MathHelper.floor(minX),
                MathHelper.floor(minY), MathHelper.floor(minZ), MathHelper.ceil(maxX),
                MathHelper.ceil(maxY), MathHelper.ceil(maxZ));
    }

    static boolean isSporeKillingBlockNearbyForRange(World world, int minX, int minY,
            int minZ, int maxX, int maxY, int maxZ) {
        DataTable dataTable = DataTables.getOrElse(world, DataTables.SPORE_KILLING_RADIUS, 0);

        int searchMinX = minX - SporeKillable.MAX_BLOCK_RADIUS;
        int searchMinY = minY - SporeKillable.MAX_BLOCK_RADIUS;
        int searchMinZ = minZ - SporeKillable.MAX_BLOCK_RADIUS;
        int searchMaxX = maxX + SporeKillable.MAX_BLOCK_RADIUS;
        int searchMaxY = maxY + SporeKillable.MAX_BLOCK_RADIUS;
        int searchMaxZ = maxZ + SporeKillable.MAX_BLOCK_RADIUS;

        for (BlockPos searchPos : BlockPos.iterate(searchMinX, searchMinY, searchMinZ, searchMaxX,
                searchMaxY, searchMaxZ)) {
            BlockState blockState = world.getBlockState(searchPos);
            if (!blockState.isIn(ModBlockTags.KILLS_SPORES)) {
                continue;
            }

            int distance = getDistanceBetweenPointAndCube(searchPos.getX(),
                    searchPos.getY(), searchPos.getZ(), minX, minY, minZ, maxX, maxY, maxZ);
            if (dataTable.getIntForBlock(blockState) >= distance) {
                return true;
            }
        }
        return false;
    }

    private static int getDistanceBetweenPointAndCube(int x, int y, int z, int minX, int minY,
            int minZ, int maxX, int maxY, int maxZ) {
        int closestX = clamp(x, minX, maxX);
        int closestY = clamp(y, minY, maxY);
        int closestZ = clamp(z, minZ, maxZ);
        return getDistance(x, y, z, closestX, closestY, closestZ);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    private static int getDistance(BlockPos pos1, BlockPos pos2) {
        return getDistance(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(),
                pos2.getZ());
    }

    // Effective radius is a square, so use Chebyshev distance.
    // If we want a more realistic distance, consider Manhattan distance.
    private static int getDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
        int deltaX = Math.abs(x1 - x2);
        int deltaY = Math.abs(y1 - y2);
        int deltaZ = Math.abs(z1 - z2);
        return Math.max(deltaX, Math.max(deltaY, deltaZ));
    }

    static BlockState convertToDeadVariant(SporeKillable sporeKillable, BlockState blockState) {
        return sporeKillable.getDeadSporeBlock()
                .getStateWithProperties(blockState);
    }

    Block getDeadSporeBlock();
}
