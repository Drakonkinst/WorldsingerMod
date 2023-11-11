package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ModFluidTags {

    public static final TagKey<Fluid> AETHER_SPORES = ModFluidTags.of("aether_spores");
    public static final TagKey<Fluid> STILL_AETHER_SPORES = ModFluidTags.of("still_aether_spores");
    public static final TagKey<Fluid> VERDANT_SPORES = ModFluidTags.of("verdant_spores");
    public static final TagKey<Fluid> CRIMSON_SPORES = ModFluidTags.of("crimson_spores");
    public static final TagKey<Fluid> ZEPHYR_SPORES = ModFluidTags.of("zephyr_spores");
    public static final TagKey<Fluid> SUNLIGHT_SPORES = ModFluidTags.of("sunlight_spores");
    public static final TagKey<Fluid> ROSEITE_SPORES = ModFluidTags.of("roseite_spores");
    public static final TagKey<Fluid> MIDNIGHT_SPORES = ModFluidTags.of("midnight_spores");
    public static final TagKey<Fluid> DEAD_SPORES = ModFluidTags.of("dead_spores");
    public static final TagKey<Fluid> SUNLIGHT = ModFluidTags.of("sunlight");
    public static final TagKey<Fluid> AETHER_SPORES_OR_SUNLIGHT = ModFluidTags.of(
            "aether_spores_or_sunlight");

    private ModFluidTags() {}

    private static TagKey<Fluid> of(String id) {
        return TagKey.of(RegistryKeys.FLUID, Worldsinger.id(id));
    }
}
