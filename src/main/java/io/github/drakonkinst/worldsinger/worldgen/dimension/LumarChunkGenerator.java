package io.github.drakonkinst.worldsinger.worldgen.dimension;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.AquiferSampler.FluidLevelSampler;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseConfig;

public class LumarChunkGenerator extends CustomNoiseChunkGenerator {

    public static final int SEA_LEVEL = 80;

    private static final Block PLACEHOLDER_BLOCK = ModBlocks.DEAD_SPORE_SEA_BLOCK;
    private static final Supplier<FluidLevelSampler> SPORE_SEA_PLACEHOLDER = Suppliers.memoize(
            LumarChunkGenerator::createFluidLevelSampler);
    private static final BlockState WATER = Blocks.WATER.getDefaultState();
    private static final BlockState EMERALD_SEA = ModBlocks.VERDANT_SPORE_SEA_BLOCK.getDefaultState();

    public static final Codec<LumarChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .forGetter(LumarChunkGenerator::getBiomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings")
                            .forGetter(LumarChunkGenerator::getSettings)
            ).apply(instance, instance.stable(LumarChunkGenerator::new)));

    private static AquiferSampler.FluidLevelSampler createFluidLevelSampler() {
        AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54,
                Blocks.LAVA.getDefaultState());
        AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(SEA_LEVEL,
                PLACEHOLDER_BLOCK.getDefaultState());
        return (x, y, z) -> {
            if (y < Math.min(-54, SEA_LEVEL)) {
                return fluidLevel;
            }
            return fluidLevel2;
        };
    }

    public LumarChunkGenerator(BiomeSource biomeSource,
            RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings, SPORE_SEA_PLACEHOLDER);
    }

    @Override
    public BlockState modifyBlockState(BlockState state, NoiseConfig noiseConfig, int x, int y,
            int z) {
        if (!state.isOf(PLACEHOLDER_BLOCK)) {
            return state;
        }

        DensityFunction.UnblendedNoisePos noisePos = new DensityFunction.UnblendedNoisePos(x, y, z);
        double temperature = noiseConfig.getNoiseRouter().temperature().sample(noisePos);
        if (temperature > 0.0) {
            return EMERALD_SEA;
        } else {
            return WATER;
        }
    }

    @Override
    protected boolean shouldSkipPostProcessing(AquiferSampler aquiferSampler, FluidState fluidState,
            int y) {
        return super.shouldSkipPostProcessing(aquiferSampler, fluidState, y) &&
                !(!fluidState.isEmpty() && y == this.getSeaLevel() - 1 && fluidState.isIn(
                        ModFluidTags.AETHER_SPORES));
    }

    @Override
    public int getSeaLevel() {
        return SEA_LEVEL;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}
