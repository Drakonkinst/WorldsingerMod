package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.SporeKillable;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import io.github.drakonkinst.worldsinger.datatable.DataTable;
import io.github.drakonkinst.worldsinger.datatable.DataTables;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.util.BlockPosUtil;
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

public final class SporeKillingManager {

    public static final int MAX_BLOCK_RADIUS = 5;
    public static final double BOAT_RADIUS = 2.0;

    private SporeKillingManager() {}

    public static int killNearbySpores(World world, BlockPos pos, int radius) {
        int numKilled = 0;
        radius = Math.min(radius, MAX_BLOCK_RADIUS);
        int deadSporeFluidIndex = Fluidlogged.getFluidIndex(ModFluids.DEAD_SPORES);

        // Not sure if this is the right iteration method, but it works
        for (BlockPos currentPos : BlockPos.iterateOutwards(pos, radius, radius, radius)) {
            if (currentPos.equals(pos)) {
                continue;
            }

            BlockState blockState = world.getBlockState(currentPos);
            boolean wasChanged = false;

            // Kill SporeKillable block
            if (blockState.getBlock() instanceof SporeKillable sporeKillable) {
                if (BlockPosUtil.isInvestitureBlocked(world, pos, currentPos)) {
                    continue;
                }
                blockState = convertToDeadVariant(sporeKillable, blockState);
                wasChanged = true;
            }

            // Turn living spore fluids into dead spore fluid
            if (blockState.getBlock() instanceof Waterloggable) {
                FluidState fluidState = blockState.getFluidState();
                if (fluidState.isIn(ModFluidTags.AETHER_SPORES) && !fluidState.isIn(
                        ModFluidTags.DEAD_SPORES)) {
                    if (BlockPosUtil.isInvestitureBlocked(world, pos, currentPos)) {
                        continue;
                    }
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

    public static boolean isSporeKillingBlockNearby(World world, BlockPos pos) {
        DataTable dataTable = DataTables.get(DataTables.SPORE_KILLING_RADIUS);
        for (BlockPos currentPos : BlockPos.iterateOutwards(pos, MAX_BLOCK_RADIUS,
                MAX_BLOCK_RADIUS, MAX_BLOCK_RADIUS)) {
            BlockState blockState = world.getBlockState(currentPos);
            if (!blockState.isIn(ModBlockTags.KILLS_SPORES)) {
                continue;
            }

            int distance = BlockPosUtil.getDistance(pos, currentPos);
            if (dataTable.getIntForBlock(blockState) < distance) {
                continue;
            }

            if (BlockPosUtil.isInvestitureBlocked(world, currentPos, pos)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static boolean checkNearbyEntities(World world, Vec3d pos) {
        Box box = Box.of(pos, BOAT_RADIUS, BOAT_RADIUS, BOAT_RADIUS);
        return checkNearbyEntitiesInBox(world, box);
    }

    public static boolean checkNearbyEntitiesForRange(World world, double minX, double minY,
            double minZ, double maxX, double maxY, double maxZ) {
        Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        box = box.expand(BOAT_RADIUS);
        return checkNearbyEntitiesInBox(world, box);
    }

    private static boolean checkNearbyEntitiesInBox(World world, Box box) {
        List<BoatEntity> entitiesInRange = world.getEntitiesByClass(BoatEntity.class, box,
                boatEntity -> {
                    SilverLinedComponent silverData = ModComponents.SILVER_LINED.get(
                            boatEntity);
                    boolean hasSilver = silverData.getSilverDurability() > 0;
                    if (hasSilver) {
                        silverData.setSilverDurability(silverData.getSilverDurability() - 1);
                        ModComponents.SILVER_LINED.sync(boatEntity);
                    }
                    return hasSilver;
                });
        return !entitiesInRange.isEmpty();
    }

    public static boolean isSporeKillingBlockNearbyForRange(World world, double minX, double minY,
            double minZ, double maxX, double maxY, double maxZ) {
        return isSporeKillingBlockNearbyForRange(world,
                MathHelper.floor(minX), MathHelper.floor(minY), MathHelper.floor(minZ),
                MathHelper.ceil(maxX), MathHelper.ceil(maxY), MathHelper.ceil(maxZ));
    }

    public static boolean isSporeKillingBlockNearbyForRange(World world, int minX, int minY,
            int minZ, int maxX, int maxY, int maxZ) {
        DataTable dataTable = DataTables.get(DataTables.SPORE_KILLING_RADIUS);

        int searchMinX = minX - MAX_BLOCK_RADIUS;
        int searchMinY = minY - MAX_BLOCK_RADIUS;
        int searchMinZ = minZ - MAX_BLOCK_RADIUS;
        int searchMaxX = maxX + MAX_BLOCK_RADIUS;
        int searchMaxY = maxY + MAX_BLOCK_RADIUS;
        int searchMaxZ = maxZ + MAX_BLOCK_RADIUS;

        BlockPos.Mutable closestPos = new BlockPos.Mutable();
        for (BlockPos searchPos : BlockPos.iterate(searchMinX, searchMinY, searchMinZ, searchMaxX,
                searchMaxY, searchMaxZ)) {
            BlockState blockState = world.getBlockState(searchPos);
            if (!blockState.isIn(ModBlockTags.KILLS_SPORES)) {
                continue;
            }

            SporeKillingManager.calcClosestPointOnCuboid(searchPos.getX(),
                    searchPos.getY(), searchPos.getZ(), minX, minY, minZ, maxX, maxY, maxZ,
                    closestPos);
            int distance = BlockPosUtil.getDistance(searchPos, closestPos);
            if (dataTable.getIntForBlock(blockState) < distance) {
                continue;
            }

            if (BlockPosUtil.isInvestitureBlocked(world, searchPos, closestPos)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private static void calcClosestPointOnCuboid(int x, int y, int z, int minX, int minY,
            int minZ, int maxX, int maxY, int maxZ, BlockPos.Mutable mutable) {
        int closestX = clamp(x, minX, maxX);
        int closestY = clamp(y, minY, maxY);
        int closestZ = clamp(z, minZ, maxZ);
        mutable.set(closestX, closestY, closestZ);
    }

    private static int getDistanceBetweenPointAndCube(int x, int y, int z, int minX, int minY,
            int minZ, int maxX, int maxY, int maxZ) {
        int closestX = clamp(x, minX, maxX);
        int closestY = clamp(y, minY, maxY);
        int closestZ = clamp(z, minZ, maxZ);
        return BlockPosUtil.getDistance(x, y, z, closestX, closestY, closestZ);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    public static BlockState convertToDeadVariant(SporeKillable sporeKillable,
            BlockState blockState) {
        return sporeKillable.getDeadSporeBlock().getStateWithProperties(blockState);
    }
}
