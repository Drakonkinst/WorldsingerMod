package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@SuppressWarnings("UnqualifiedStaticUsage")
public final class ModFluids {

    public static final FlowableFluid DEAD_SPORES = register("dead_spores",
            new DeadSporeFluid.Still());
    public static final FlowableFluid FLOWING_DEAD_SPORES = register("flowing_dead_spores",
            new DeadSporeFluid.Flowing());
    public static final FlowableFluid VERDANT_SPORES = register("verdant_spores",
            new VerdantSporeFluid.Still());
    public static final FlowableFluid FLOWING_VERDANT_SPORES = register("flowing_verdant_spores",
            new VerdantSporeFluid.Flowing());
    public static final FlowableFluid CRIMSON_SPORES = register("crimson_spores",
            new CrimsonSporeFluid.Still());
    public static final FlowableFluid FLOWING_CRIMSON_SPORES = register("flowing_crimson_spores",
            new CrimsonSporeFluid.Flowing());
    public static final FlowableFluid ZEPHYR_SPORES = register("zephyr_spores",
            new ZephyrSporeFluid.Still());
    public static final FlowableFluid FLOWING_ZEPHYR_SPORES = register("flowing_zephyr_spores",
            new ZephyrSporeFluid.Flowing());
    public static final FlowableFluid SUNLIGHT_SPORES = register("sunlight_spores",
            new SunlightSporeFluid.Still());
    public static final FlowableFluid FLOWING_SUNLIGHT_SPORES = register("flowing_sunlight_spores",
            new SunlightSporeFluid.Flowing());
    public static final FlowableFluid ROSEITE_SPORES = register("roseite_spores",
            new RoseiteSporeFluid.Still());
    public static final FlowableFluid FLOWING_ROSEITE_SPORES = register("flowing_roseite_spores",
            new RoseiteSporeFluid.Flowing());
    public static final FlowableFluid MIDNIGHT_SPORES = register("midnight_spores",
            new MidnightSporeFluid.Still());
    public static final FlowableFluid FLOWING_MIDNIGHT_SPORES = register("flowing_midnight_spores",
            new MidnightSporeFluid.Flowing());
    public static final StillFluid SUNLIGHT = register("sunlight", new SunlightFluid());

    public static <T extends Fluid> T register(String id, T fluid) {
        return Registry.register(Registries.FLUID, Worldsinger.id(id), fluid);
    }

    public static void initialize() {}

    private ModFluids() {}
}
