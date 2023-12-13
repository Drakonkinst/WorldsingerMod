package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;

@SuppressWarnings("UnqualifiedStaticUsage")
public final class ModFluidRenderers {

    public static void register() {
        registerFluidRenderer(ModFluids.DEAD_SPORES, ModFluids.FLOWING_DEAD_SPORES,
                "block/dead_spore_block", "block/dead_spore_sea_flow");
        registerFluidRenderer(ModFluids.VERDANT_SPORES, ModFluids.FLOWING_VERDANT_SPORES,
                "block/verdant_spore_block", "block/verdant_spore_sea_flow");
        registerFluidRenderer(ModFluids.CRIMSON_SPORES, ModFluids.FLOWING_CRIMSON_SPORES,
                "block/crimson_spore_block", "block/crimson_spore_sea_flow");
        registerFluidRenderer(ModFluids.ZEPHYR_SPORES, ModFluids.FLOWING_ZEPHYR_SPORES,
                "block/zephyr_spore_block", "block/zephyr_spore_sea_flow");
        registerFluidRenderer(ModFluids.SUNLIGHT_SPORES, ModFluids.FLOWING_SUNLIGHT_SPORES,
                "block/sunlight_spore_block", "block/sunlight_spore_sea_flow");
        registerFluidRenderer(ModFluids.ROSEITE_SPORES, ModFluids.FLOWING_ROSEITE_SPORES,
                "block/roseite_spore_block", "block/roseite_spore_sea_flow");
        registerFluidRenderer(ModFluids.MIDNIGHT_SPORES, ModFluids.FLOWING_MIDNIGHT_SPORES,
                "block/midnight_spore_block", "block/midnight_spore_sea_flow");
        registerFluidRenderer(ModFluids.SUNLIGHT, "block/sunlight", false);
    }

    private static void registerFluidRenderer(Fluid still, Fluid flow, String stillTexturePath,
            String flowTexturePath) {
        // Register render handler
        FluidRenderHandlerRegistry.INSTANCE.register(still, flow,
                new SimpleFluidRenderHandler(Worldsinger.id(stillTexturePath),
                        Worldsinger.id(flowTexturePath)));

        // Register fluid render layer as translucent (needed to make boats cull properly)
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), still, flow);
    }

    private static void registerFluidRenderer(Fluid fluid, String texturePath, boolean shaded) {
        // Register render handler
        FluidRenderHandlerRegistry.INSTANCE.register(fluid,
                new StillFluidRenderHandler(Worldsinger.id(texturePath), shaded));
    }

    private ModFluidRenderers() {}
}
