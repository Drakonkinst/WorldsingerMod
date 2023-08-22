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
            // Use square radius instead of Manhattan distance
            int distance = SporeKillable.getLongestDistanceAxis(pos, currentPos);
            if (blockState.isIn(ModBlockTags.KILLS_SPORES)
                    && dataTable.getIntForBlock(blockState) >= distance) {
                return true;
            }
        }
        return false;
    }

    static int getLongestDistanceAxis(BlockPos pos1, BlockPos pos2) {
        int deltaX = Math.abs(pos1.getX() - pos2.getX());
        int deltaY = Math.abs(pos1.getY() - pos2.getY());
        int deltaZ = Math.abs(pos1.getZ() - pos2.getZ());
        return Math.max(deltaX, Math.max(deltaY, deltaZ));
    }

    static BlockState convertToDeadVariant(SporeKillable sporeKillable, BlockState blockState) {
        return sporeKillable.getDeadSporeBlock()
                .getStateWithProperties(blockState);
    }

    Block getDeadSporeBlock();
}
