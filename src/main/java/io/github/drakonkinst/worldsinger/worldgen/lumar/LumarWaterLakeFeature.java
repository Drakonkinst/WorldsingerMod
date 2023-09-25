package io.github.drakonkinst.worldsinger.worldgen.lumar;

import com.mojang.serialization.Codec;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LumarWaterLakeFeature extends Feature<DefaultFeatureConfig> {

    private static final int SEA_LEVEL = 80;

    public LumarWaterLakeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        ChunkPos centerChunkPos = world.getChunk(context.getOrigin()).getPos();

        int minX;
        int maxX;
        int minZ;
        int maxZ;

        ChunkPos startChunkPos = world.getChunk(centerChunkPos.x - 1, centerChunkPos.z - 1)
                .getPos();
        ChunkPos endChunkPos = world.getChunk(centerChunkPos.x + 1, centerChunkPos.z + 1)
                .getPos();
        minX = startChunkPos.getStartX();
        maxX = endChunkPos.getEndX();
        minZ = startChunkPos.getStartZ();
        maxZ = endChunkPos.getEndZ();

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = SEA_LEVEL - 1; y >= world.getBottomY(); --y) {
                    mutable.set(x, y, z);
                    if (world.getBlockState(mutable).isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)) {
                        world.setBlockState(mutable, Blocks.WATER.getDefaultState(),
                                Block.NOTIFY_ALL);
                    } else if (world.getBlockState(mutable).isOf(Blocks.MAGMA_BLOCK)) {
                        world.setBlockState(mutable, Blocks.STONE.getDefaultState(),
                                Block.NOTIFY_ALL);
                    }
                }
            }
        }

        return true;
    }
}