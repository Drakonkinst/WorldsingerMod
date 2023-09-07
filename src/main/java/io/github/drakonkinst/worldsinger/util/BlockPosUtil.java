package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;

public final class BlockPosUtil {

    private BlockPosUtil() {}

    public static BlockPos toRoundedBlockPos(Vec3d pos) {
        int x = MathHelper.floor(pos.getX());
        int y = (int) Math.round(pos.getY());
        int z = MathHelper.floor(pos.getZ());
        return new BlockPos(x, y, z);
    }

    public static boolean isInvestitureBlocked(World world, BlockPos emitterPos,
            BlockPos listenerPos) {
        return BlockPosUtil.isOccluded(world, emitterPos, listenerPos,
                ModBlockTags.BLOCKS_INVESTITURE);
    }

    // Note: This only works for BlockPos, we may want something different for entities to account
    // for their hitboxes? Maybe raycast from all corners of their bounding box
    public static boolean isOccluded(World world, BlockPos emitterPos, BlockPos listenerPos,
            TagKey<Block> blockTag) {
        Vec3d fromPos = emitterPos.toCenterPos();
        Vec3d toPos = listenerPos.toCenterPos();
        for (Direction direction : Direction.values()) {
            Vec3d vec3d3 = fromPos.offset(direction, 1.0E-5f);
            if (world.raycast(new BlockStateRaycastContext(vec3d3, toPos, state -> state.isIn(
                    blockTag))).getType() == HitResult.Type.BLOCK) {
                continue;
            }
            return false;
        }
        return true;
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
