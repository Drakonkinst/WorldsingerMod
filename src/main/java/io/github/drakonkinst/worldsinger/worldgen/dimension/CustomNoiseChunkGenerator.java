package io.github.drakonkinst.worldsinger.worldgen.dimension;

import com.google.common.collect.Sets;
import io.github.drakonkinst.worldsinger.mixin.accessor.ChunkNoiseSamplerInvoker;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.BelowZeroRetrogen;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

// Custom noise-based chunk generator that allows for a custom fluid level sampler to be used
public abstract class CustomNoiseChunkGenerator extends NoiseChunkGenerator {

    private static final BlockState AIR = Blocks.AIR.getDefaultState();

    private final Supplier<AquiferSampler.FluidLevelSampler> customFluidLevelSampler;

    public CustomNoiseChunkGenerator(BiomeSource biomeSource,
            RegistryEntry<ChunkGeneratorSettings> settings,
            Supplier<AquiferSampler.FluidLevelSampler> customFluidLevelSampler) {
        super(biomeSource, settings);
        this.customFluidLevelSampler = customFluidLevelSampler;
    }

    public abstract BlockState modifyBlockState(BlockState state, NoiseConfig noiseConfig, int x,
            int y, int z);

    protected ChunkNoiseSampler createChunkNoiseSampler(Chunk chunk, StructureAccessor world,
            Blender blender, NoiseConfig noiseConfig) {
        return ChunkNoiseSampler.create(chunk, noiseConfig,
                StructureWeightSampler.createStructureWeightSampler(world, chunk.getPos()),
                this.getSettings().value(), customFluidLevelSampler.get(), blender);
    }

    private OptionalInt sampleHeightmap(HeightLimitView world, NoiseConfig noiseConfig, int x,
            int z, @Nullable MutableObject<VerticalBlockSample> columnSample,
            @Nullable Predicate<BlockState> stopPredicate) {
        BlockState[] blockStates;
        GenerationShapeConfig generationShapeConfig = this.getSettings().value()
                .generationShapeConfig()
                .trimHeight(world);
        int i = generationShapeConfig.verticalCellBlockCount();
        int j = generationShapeConfig.minimumY();
        int k = MathHelper.floorDiv(j, i);
        int l = MathHelper.floorDiv(generationShapeConfig.height(), i);
        if (l <= 0) {
            return OptionalInt.empty();
        }
        if (columnSample == null) {
            blockStates = null;
        } else {
            blockStates = new BlockState[generationShapeConfig.height()];
            columnSample.setValue(new VerticalBlockSample(j, blockStates));
        }
        int m = generationShapeConfig.horizontalCellBlockCount();
        int n = Math.floorDiv(x, m);
        int o = Math.floorDiv(z, m);
        int p = Math.floorMod(x, m);
        int q = Math.floorMod(z, m);
        int r = n * m;
        int s = o * m;
        double d = (double) p / (double) m;
        double e = (double) q / (double) m;

        ChunkNoiseSampler chunkNoiseSampler = new ChunkNoiseSampler(1, noiseConfig, r, s,
                generationShapeConfig, DensityFunctionTypes.Beardifier.INSTANCE,
                this.getSettings().value(), customFluidLevelSampler.get(),
                Blender.getNoBlending());
        chunkNoiseSampler.sampleStartDensity();
        chunkNoiseSampler.sampleEndDensity(0);
        for (int t = l - 1; t >= 0; --t) {
            chunkNoiseSampler.onSampledCellCorners(t, 0);
            for (int u = i - 1; u >= 0; --u) {

                int v = (k + t) * i + u;
                double f = (double) u / (double) i;
                chunkNoiseSampler.interpolateY(v, f);
                chunkNoiseSampler.interpolateX(x, d);
                chunkNoiseSampler.interpolateZ(z, e);
                BlockState blockState = ((ChunkNoiseSamplerInvoker) chunkNoiseSampler).worldsinger$sampleBlockState();
                BlockState blockState2 =
                        blockState == null ? this.getSettings().value().defaultBlock() : blockState;
                if (blockStates != null) {
                    int w = t * i + u;
                    blockStates[w] = blockState2;
                }
                if (stopPredicate == null || !stopPredicate.test(blockState2)) {
                    continue;
                }
                chunkNoiseSampler.stopInterpolation();
                return OptionalInt.of(v + 1);
            }
        }
        chunkNoiseSampler.stopInterpolation();
        return OptionalInt.empty();
    }

    @Override
    public int getHeight(int x, int z, Type heightmap, HeightLimitView world,
            NoiseConfig noiseConfig) {
        return this.sampleHeightmap(world, noiseConfig, x, z, null, heightmap.getBlockPredicate())
                .orElse(world.getBottomY());
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world,
            NoiseConfig noiseConfig) {
        MutableObject<VerticalBlockSample> mutableObject = new MutableObject<>();
        this.sampleHeightmap(world, noiseConfig, x, z, mutableObject, null);
        return mutableObject.getValue();
    }

    @Override
    public CompletableFuture<Chunk> populateBiomes(Executor executor, NoiseConfig noiseConfig,
            Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
            this.populateBiomes(blender, noiseConfig, structureAccessor, chunk);
            return chunk;
        }), Util.getMainWorkerExecutor());
    }

    private void populateBiomes(Blender blender, NoiseConfig noiseConfig,
            StructureAccessor structureAccessor, Chunk chunk) {
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
                thisChunk -> this.createChunkNoiseSampler(thisChunk, structureAccessor, blender,
                        noiseConfig));
        BiomeSupplier biomeSupplier = BelowZeroRetrogen.getBiomeSupplier(
                blender.getBiomeSupplier(this.biomeSource), chunk);
        chunk.populateBiomes(biomeSupplier,
                ((ChunkNoiseSamplerInvoker) chunkNoiseSampler).worldsinger$createMultiNoiseSampler(
                        noiseConfig.getNoiseRouter(),
                        this.getSettings().value().spawnTarget()));
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
            NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        GenerationShapeConfig generationShapeConfig = this.getSettings().value()
                .generationShapeConfig()
                .trimHeight(chunk.getHeightLimitView());
        int i = generationShapeConfig.minimumY();
        int j = MathHelper.floorDiv(i, generationShapeConfig.verticalCellBlockCount());
        int k = MathHelper.floorDiv(generationShapeConfig.height(),
                generationShapeConfig.verticalCellBlockCount());
        if (k <= 0) {
            return CompletableFuture.completedFuture(chunk);
        }
        int l = chunk.getSectionIndex(k * generationShapeConfig.verticalCellBlockCount() - 1 + i);
        int m = chunk.getSectionIndex(i);
        HashSet<ChunkSection> set = Sets.newHashSet();
        for (int n = l; n >= m; --n) {
            ChunkSection chunkSection = chunk.getSection(n);
            chunkSection.lock();
            set.add(chunkSection);
        }
        return CompletableFuture.supplyAsync(Util.debugSupplier("wgen_fill_noise",
                        () -> this.populateNoise(blender, structureAccessor, noiseConfig, chunk, j, k)),
                Util.getMainWorkerExecutor()).whenCompleteAsync((chunk2, throwable) -> {
            for (ChunkSection chunkSection : set) {
                chunkSection.unlock();
            }
        }, executor);
    }

    protected Chunk populateNoise(Blender blender, StructureAccessor structureAccessor,
            NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight) {
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
                thisChunk -> this.createChunkNoiseSampler(thisChunk, structureAccessor, blender,
                        noiseConfig));
        Heightmap oceanFloorHeightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap worldSurfaceHeightmap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
        chunkNoiseSampler.sampleStartDensity();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int horizontalCellCount = ((ChunkNoiseSamplerInvoker) chunkNoiseSampler).worldsinger$getHorizontalCellBlockCount();
        int verticalCellCount = ((ChunkNoiseSamplerInvoker) chunkNoiseSampler).worldsinger$getVerticalCellBlockCount();
        int cellCountX = 16 / horizontalCellCount;
        int cellCountZ = 16 / horizontalCellCount;
        for (int cellX = 0; cellX < cellCountX; ++cellX) {
            chunkNoiseSampler.sampleEndDensity(cellX);
            for (int cellZ = 0; cellZ < cellCountZ; ++cellZ) {
                int lastSectionIndex = chunk.countVerticalSections() - 1;
                ChunkSection chunkSection = chunk.getSection(lastSectionIndex);
                for (int cellY = cellHeight - 1; cellY >= 0; --cellY) {
                    chunkNoiseSampler.onSampledCellCorners(cellY, cellZ);
                    for (int posY = verticalCellCount - 1; posY >= 0; --posY) {
                        int y = (minimumCellY + cellY) * verticalCellCount + posY;
                        int chunkY = y & 0xF;
                        int sectionIndex = chunk.getSectionIndex(y);
                        if (lastSectionIndex != sectionIndex) {
                            lastSectionIndex = sectionIndex;
                            chunkSection = chunk.getSection(sectionIndex);
                        }
                        double d = (double) posY / (double) verticalCellCount;
                        chunkNoiseSampler.interpolateY(y, d);
                        for (int posX = 0; posX < horizontalCellCount; ++posX) {
                            int x = startX + cellX * horizontalCellCount + posX;
                            int chunkX = x & 0xF;
                            double tX = (double) posX / (double) horizontalCellCount;
                            chunkNoiseSampler.interpolateX(x, tX);
                            for (int posZ = 0; posZ < horizontalCellCount; ++posZ) {
                                int z = startZ + cellZ * horizontalCellCount + posZ;
                                int chunkZ = z & 0xF;
                                double tZ = (double) posZ / (double) horizontalCellCount;
                                chunkNoiseSampler.interpolateZ(z, tZ);
                                BlockState blockState = ((ChunkNoiseSamplerInvoker) chunkNoiseSampler).worldsinger$sampleBlockState();
                                if (blockState == null) {
                                    blockState = this.getSettings().value().defaultBlock();
                                }
                                if (blockState == AIR || SharedConstants.isOutsideGenerationArea(
                                        chunk.getPos())) {
                                    continue;
                                }

                                // Allow block state to be modified before placement
                                blockState = this.modifyBlockState(blockState, noiseConfig, x,
                                        y, z);
                                chunkSection.setBlockState(chunkX, chunkY, chunkZ, blockState,
                                        false);
                                oceanFloorHeightmap.trackUpdate(chunkX, y, chunkZ, blockState);
                                worldSurfaceHeightmap.trackUpdate(chunkX, y, chunkZ, blockState);

                                // Allow multiple conditions to skip post-processing
                                if ((this.shouldSkipPostProcessing(aquiferSampler,
                                        blockState.getFluidState(), y))) {
                                    continue;
                                }
                                mutable.set(x, y, z);
                                chunk.markBlockForPostProcessing(mutable);
                            }
                        }
                    }
                }
            }
            chunkNoiseSampler.swapBuffers();
        }
        chunkNoiseSampler.stopInterpolation();
        return chunk;
    }

    protected boolean shouldSkipPostProcessing(AquiferSampler aquiferSampler, FluidState fluidState,
            int y) {
        return fluidState.isEmpty() || !aquiferSampler.needsFluidTick();
    }


}
