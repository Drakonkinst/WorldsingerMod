package io.github.drakonkinst.worldsinger.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public record ReplaceWithSporeSeaFeatureConfig(Identifier blockId) implements FeatureConfig {

    public static Codec<ReplaceWithSporeSeaFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Identifier.CODEC.fieldOf("block")
                                    .forGetter(ReplaceWithSporeSeaFeatureConfig::blockId)
                    ).apply(instance, ReplaceWithSporeSeaFeatureConfig::new));
}
