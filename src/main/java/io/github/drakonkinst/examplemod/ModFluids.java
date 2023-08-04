package io.github.drakonkinst.examplemod;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModFluids {
    public static final FlowableFluid VERDANT_SPORES = register(new VerdantSporeFluid.Still(), "verdant_spores");
    public static final FlowableFluid FLOWING_VERDANT_SPORES = register(new VerdantSporeFluid.Flowing(), "flowing_verdant_spores");

    public static void initialize() {

    }

    public static <T extends Fluid> T register(T fluid, String id) {
        Identifier fluidId = new Identifier(Constants.MOD_ID, id);
        return Registry.register(Registries.FLUID, fluidId, fluid);
    }
}
