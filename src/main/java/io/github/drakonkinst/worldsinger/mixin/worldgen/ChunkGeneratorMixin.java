package io.github.drakonkinst.worldsinger.mixin.worldgen;

import io.github.drakonkinst.worldsinger.worldgen.ModDimensionTypes;
import io.github.drakonkinst.worldsinger.worldgen.feature.ModConfiguredFeatures;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public final class ChunkGeneratorMixin {

    @Inject(method = "generateFeatures", at = @At("TAIL"))
    private void fillSporeSeas(StructureWorldAccess world, Chunk chunk,
            StructureAccessor structureAccessor, CallbackInfo ci) {

        Optional<RegistryKey<DimensionType>> dimensionKey = world.getRegistryManager()
                .get(RegistryKeys.DIMENSION_TYPE).getKey(world.getDimension());
        if (dimensionKey.isEmpty() || !dimensionKey.get().equals(ModDimensionTypes.LUMAR)) {
            return;
        }

        ChunkRandom chunkRandom = new ChunkRandom(
                new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));

        try {
            world.setCurrentlyGeneratingStructureName(() -> "Verdant Spore Sea Feature");
            ConfiguredFeature<?, ?> feature = world.getRegistryManager()
                    .get(RegistryKeys.CONFIGURED_FEATURE)
                    .get(ModConfiguredFeatures.VERDANT_SPORE_SEA);
            if (feature == null) {
                return;
            }
            feature.generate(world, (ChunkGenerator) (Object) this, chunkRandom,
                    chunk.getPos().getStartPos());
        } catch (Exception exception2) {
            CrashReport crashReport2 = CrashReport.create(exception2, "Feature placement");
            crashReport2.addElement("Feature").add("Description", "Verdant Spore Sea Feature");
            throw new CrashException(crashReport2);
        }
    }

}