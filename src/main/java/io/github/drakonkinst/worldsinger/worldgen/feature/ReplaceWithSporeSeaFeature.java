package io.github.drakonkinst.worldsinger.worldgen.feature;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.worldgen.ModBiomes;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ReplaceWithSporeSeaFeature extends Feature<ReplaceWithSporeSeaFeatureConfig> {

    private static final int SEA_LEVEL = 80;

    public ReplaceWithSporeSeaFeature() {
        super(ReplaceWithSporeSeaFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<ReplaceWithSporeSeaFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        ChunkPos centerChunkPos = world.getChunk(context.getOrigin()).getPos();
        RegistryEntry<Biome> biome = world.getRegistryManager().get(RegistryKeys.BIOME).getEntry(
                ModBiomes.EMERALD_SEA).orElse(null);

        int minX;
        int maxX;
        int minZ;
        int maxZ;

        if (this.isAllSameChunk(world, centerChunkPos, biome)) {
            minX = centerChunkPos.getStartX();
            maxX = centerChunkPos.getEndX();
            minZ = centerChunkPos.getStartZ();
            maxZ = centerChunkPos.getEndZ();
        } else {
            ChunkPos startChunkPos = world.getChunk(centerChunkPos.x - 1, centerChunkPos.z - 1)
                    .getPos();
            ChunkPos endChunkPos = world.getChunk(centerChunkPos.x + 1, centerChunkPos.z + 1)
                    .getPos();
            minX = startChunkPos.getStartX();
            maxX = endChunkPos.getEndX();
            minZ = startChunkPos.getStartZ();
            maxZ = endChunkPos.getEndZ();
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = SEA_LEVEL - 1; y >= world.getBottomY(); --y) {
                    mutable.set(x, y, z);
                    if (world.getBlockState(mutable).isOf(Blocks.WATER)) {
                        world.setBlockState(mutable,
                                ModBlocks.VERDANT_SPORE_SEA_BLOCK.getDefaultState(),
                                Block.NOTIFY_ALL);
                        // Turn the spore sea grey at the topmost layer, if applicable
                        // Only do this on the top layer for optimization. Many other interactions
                        // will correct this, so it isn't worth slowing down chunk generation for.
                        if (y == SEA_LEVEL - 1) {
                            world.scheduleBlockTick(mutable, ModBlocks.VERDANT_SPORE_SEA_BLOCK, 1);
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean isAllSameChunk(StructureWorldAccess world, ChunkPos centerChunkPos,
            RegistryEntry<Biome> allowedBiome) {
        ObjectArraySet<RegistryEntry<Biome>> encounteredBiomes = new ObjectArraySet<>(2);
        Iterator<ChunkPos> iterator = ChunkPos.stream(centerChunkPos, 1).iterator();
        while (iterator.hasNext()) {
            ChunkPos chunkPos = iterator.next();
            Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
            for (ChunkSection chunkSection : chunk.getSectionArray()) {
                chunkSection.getBiomeContainer().forEachValue(encounteredBiomes::add);
                if (encounteredBiomes.size() > 1) {
                    return false;
                }
            }
        }
        return encounteredBiomes.contains(allowedBiome);
    }
}
