package io.github.drakonkinst.worldsinger.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class BlockPosUtil {

    private BlockPosUtil() {}

    public static BlockPos toRoundedBlockPos(Vec3d pos) {
        int x = MathHelper.floor(pos.getX());
        int y = (int) Math.round(pos.getY());
        int z = MathHelper.floor(pos.getZ());
        return new BlockPos(x, y, z);
    }

    // Iterates over all blocks in an entity's bounding box. Number of blocks iterated is consistent
    // per entity type, regardless of position. Starts from the minimum y (entity's position).
    public static Iterable<BlockPos> iterateBoundingBoxForEntity(Entity entity) {
        return BlockPosUtil.iterateBoundingBoxForEntity(entity,
                BlockPosUtil.toRoundedBlockPos(entity.getPos()));
    }

    public static Iterable<BlockPos> iterateBoundingBoxForEntity(Entity entity, BlockPos blockPos) {
        return BlockPosUtil.iterateBoundingBoxForEntity(entity, blockPos, 0, 0, 0);
    }

    public static Iterable<BlockPos> iterateBoundingBoxForEntity(Entity entity, BlockPos blockPos,
            int offsetX, int offsetY, int offsetZ) {
        int width = MathHelper.ceil(entity.getWidth());
        int height = MathHelper.ceil(entity.getHeight());

        int minX = blockPos.getX() - (width / 2) + offsetX;
        int maxX = blockPos.getX() + ((width - 1) / 2) + offsetX;
        int minY = blockPos.getY() + offsetY;
        int maxY = blockPos.getY() + height - 1 + offsetY;
        int minZ = blockPos.getZ() - (width / 2) + offsetZ;
        int maxZ = blockPos.getZ() + ((width - 1) / 2) + offsetZ;
        return BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
