package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModFluids {

    public static final FlowableFluid DEAD_SPORES = register("dead_spores",
            new DeadSporeFluid.Still()
    );
    public static final FlowableFluid FLOWING_DEAD_SPORES = register("flowing_dead_spores",
            new DeadSporeFluid.Flowing()
    );
    public static final FlowableFluid VERDANT_SPORES = register("verdant_spores",
            new VerdantSporeFluid.Still()
    );
    public static final FlowableFluid FLOWING_VERDANT_SPORES = register("flowing_verdant_spores",
            new VerdantSporeFluid.Flowing()
    );
    public static final FlowableFluid CRIMSON_SPORES = register("crimson_spores",
            new CrimsonSporeFluid.Still()
    );
    public static final FlowableFluid FLOWING_CRIMSON_SPORES = register("flowing_crimson_spores",
            new CrimsonSporeFluid.Flowing()
    );

    public static <T extends Fluid> T register(String id, T fluid) {
        return Registry.register(Registries.FLUID, Worldsinger.id(id), fluid);
    }

    public static void initialize() {}

    private ModFluids() {}
}
