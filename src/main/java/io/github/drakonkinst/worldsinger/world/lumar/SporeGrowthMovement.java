package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.registry.datatable.DataTable;
import io.github.drakonkinst.worldsinger.registry.datatable.DataTables;
import io.github.drakonkinst.worldsinger.util.BlockPosUtil;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.world.MetalQueryManager;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3d;

public final class SporeGrowthMovement {

    private static final int MAX_SEARCH_RADIUS = 5;
    private static final int BLOCK_FORCE_MULTIPLIER = 10;

    public static Vector3d calcExternalForce(World world, BlockPos pos) {
        Vector3d force = new Vector3d();
        SporeGrowthMovement.calcBlockExternalForce(world, pos, force);
        SporeGrowthMovement.calcEntityExternalForce(world, pos, force);
        return SporeGrowthMovement.normalizeIfNonZero(force);
    }

    private static void calcBlockExternalForce(World world, BlockPos pos, Vector3d force) {
        DataTable metalContentTable = DataTables.getOrElse(world, DataTables.BLOCK_METAL_CONTENT,
                0);

        int minX = pos.getX() - MAX_SEARCH_RADIUS;
        int minY = pos.getY() - MAX_SEARCH_RADIUS;
        int minZ = pos.getZ() - MAX_SEARCH_RADIUS;
        int maxX = pos.getX() + MAX_SEARCH_RADIUS;
        int maxY = pos.getY() + MAX_SEARCH_RADIUS;
        int maxZ = pos.getZ() + MAX_SEARCH_RADIUS;

        double forceX = 0;
        double forceY = 0;
        double forceZ = 0;

        for (BlockPos currentPos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (currentPos.equals(pos)) {
                continue;
            }

            BlockState blockState = world.getBlockState(currentPos);
            boolean hasIron = blockState.isIn(ModBlockTags.HAS_IRON);
            boolean hasSteel = blockState.isIn(ModBlockTags.HAS_STEEL);

            if (!hasIron && !hasSteel) {
                continue;
            }

            int range = metalContentTable.getIntForBlock(blockState);
            if (range <= 0) {
                ModConstants.LOGGER.warn("Block " + blockState.getBlock().getName()
                        + " is defined as having iron or steel, but no metal content value is given");
                continue;
            }

            if (BlockPosUtil.isInvestitureBlocked(world, currentPos, pos)) {
                continue;
            }

            Vec3d dir = getNormalizedVectorBetween(currentPos, pos, hasSteel);
            int distance = BlockPosUtil.getDistance(pos, currentPos);

            if (range < distance) {
                continue;
            }

            int power = (range - distance + 1) * BLOCK_FORCE_MULTIPLIER;
            forceX += dir.getX() * power;
            forceY += dir.getY() * power;
            forceZ += dir.getZ() * power;
        }
        force.add(forceX, forceY, forceZ);
    }

    // This is so spaghetti I'm so sorry
    private static void calcEntityExternalForce(World world, BlockPos pos, Vector3d force) {
        DataTable metalContentTable = DataTables.getOrElse(world, DataTables.ENTITY_METAL_CONTENT,
                0);
        DataTable armorMetalContentTable = DataTables.getOrElse(world,
                DataTables.ARMOR_METAL_CONTENT, 0);
        Box box = new Box(pos).expand(MAX_SEARCH_RADIUS);
        double forceX = 0;
        double forceY = 0;
        double forceZ = 0;

        List<LivingEntity> nearbyLivingEntities = world.getEntitiesByClass(LivingEntity.class, box,
                LivingEntity::isAlive);
        for (LivingEntity entity : nearbyLivingEntities) {
            BlockPos entityPos = entity.getBlockPos();
            if (BlockPosUtil.isInvestitureBlocked(world, entityPos, pos)) {
                continue;
            }

            int ironContent = MetalQueryManager.getIronContent(entity, metalContentTable,
                    armorMetalContentTable);
            int steelContent = MetalQueryManager.getSteelContent(entity, metalContentTable,
                    armorMetalContentTable);

            if ((ironContent <= 0 && steelContent <= 0) || ironContent == steelContent) {
                return;
            }

            boolean isIron;
            int metalContent;
            if (ironContent > steelContent) {
                metalContent = ironContent - steelContent;
                isIron = true;
            } else {
                metalContent = steelContent - ironContent;
                isIron = false;
            }

            Vec3d forceDir = getNormalizedVectorBetween(pos, entityPos, isIron);
            int distance = BlockPosUtil.getDistance(entityPos, pos);
            int power = Math.max(0, metalContent - distance);

            if (power == 0) {
                continue;
            }

            forceX += forceDir.getX() * power;
            forceY += forceDir.getY() * power;
            forceZ += forceDir.getZ() * power;
        }

        List<AbstractMinecartEntity> nearbyMinecarts = world.getEntitiesByClass(
                AbstractMinecartEntity.class, box, Entity::isAlive);
        for (AbstractMinecartEntity entity : nearbyMinecarts) {
            BlockPos entityPos = entity.getBlockPos();
            if (BlockPosUtil.isInvestitureBlocked(world, entityPos, pos)) {
                continue;
            }

            // Assume this entity will only have iron content
            int ironContent = MetalQueryManager.getIronContent(entity, metalContentTable,
                    armorMetalContentTable);
            int distance = BlockPosUtil.getDistance(entityPos, pos);
            int power = Math.max(0, ironContent - distance);
            if (power == 0) {
                continue;
            }

            Vec3d forceDir = getNormalizedVectorBetween(entityPos, pos, false);
            forceX += forceDir.getX() * power;
            forceY += forceDir.getY() * power;
            forceZ += forceDir.getZ() * power;
        }

        force.add(forceX, forceY, forceZ);
    }

    private static Vec3d getNormalizedVectorBetween(BlockPos from, BlockPos to, boolean negate) {
        Vec3d dir = from.toCenterPos().subtract(to.toCenterPos()).normalize();
        if (negate) {
            return dir.negate();
        }
        return dir;
    }

    private static Vector3d normalizeIfNonZero(Vector3d vector) {
        if (vector.x() != 0.0f || vector.y() != 0.0f || vector.z() != 0.0f) {
            return vector.normalize();
        }
        return vector;
    }

    private SporeGrowthMovement() {}
}
