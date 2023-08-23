package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.registry.DataTable;
import io.github.drakonkinst.worldsinger.registry.DataTables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SporeKillable {

    int MAX_RADIUS = 5;

    static int killNearbySpores(World world, BlockPos pos, int radius) {
        int numKilled = 0;
        radius = Math.min(radius, SporeKillable.MAX_RADIUS);
        // Not sure if this is the right iteration method, but it works
        // TODO: Add directional flags so that other blocks (aluminum) can block this effect)
        for (BlockPos currentPos : BlockPos.iterateOutwards(pos, radius, radius, radius)) {
            if (currentPos.equals(pos)) {
                continue;
            }
            BlockState blockState = world.getBlockState(currentPos);
            if (blockState.getBlock() instanceof SporeKillable sporeKillable) {
                world.setBlockState(currentPos,
                        SporeKillable.convertToDeadVariant(sporeKillable, blockState),
                        Block.NOTIFY_ALL);
                numKilled += 1;
            }
        }
        return numKilled;
    }

    static boolean isSporeKillingBlockNearby(World world, BlockPos pos) {
        DataTable dataTable = DataTables.get(world, DataTables.SPORE_KILLING_RADIUS);
        if (dataTable == null) {
            return false;
        }

        for (BlockPos currentPos : BlockPos.iterateOutwards(pos, SporeKillable.MAX_RADIUS,
                SporeKillable.MAX_RADIUS, SporeKillable.MAX_RADIUS)) {
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

    static boolean isSporeKillingBlockNearbyForRange(World world, int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ) {
        DataTable dataTable = DataTables.get(world, DataTables.SPORE_KILLING_RADIUS);
        if (dataTable == null) {
            return false;
        }

        int searchMinX = minX - SporeKillable.MAX_RADIUS;
        int searchMinY = minY - SporeKillable.MAX_RADIUS;
        int searchMinZ = minZ - SporeKillable.MAX_RADIUS;
        int searchMaxX = maxX + SporeKillable.MAX_RADIUS;
        int searchMaxY = maxY + SporeKillable.MAX_RADIUS;
        int searchMaxZ = maxZ + SporeKillable.MAX_RADIUS;

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

    private static int getDistanceBetweenPointAndCube(int x, int y, int z, int minX,
            int minY, int minZ,
            int maxX, int maxY, int maxZ) {
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
