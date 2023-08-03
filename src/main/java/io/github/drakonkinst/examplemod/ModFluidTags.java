package io.github.drakonkinst.examplemod;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModFluidTags {
    private ModFluidTags() {
    }

    public static final TagKey<Fluid> AETHER_SPORES = ModFluidTags.of("aether_spores");

    private static TagKey<Fluid> of(String id) {
        return TagKey.of(RegistryKeys.FLUID, new Identifier(ExampleMod.MOD_ID, id));
    }
}
