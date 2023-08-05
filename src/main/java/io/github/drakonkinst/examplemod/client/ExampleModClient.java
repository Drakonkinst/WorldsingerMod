package io.github.drakonkinst.examplemod.client;

import io.github.drakonkinst.examplemod.Constants;
import io.github.drakonkinst.examplemod.fluid.ModFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.VERDANT_SPORES,
                ModFluids.FLOWING_VERDANT_SPORES,
                new SimpleFluidRenderHandler(
                        new Identifier(Constants.MOD_ID, "block/aether_spore_sea_block_still"),
                        new Identifier(Constants.MOD_ID, "block/aether_spore_sea_block_flow"),
                        0x00ff00
                ));

        // BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
        //         ModFluids.VERDANT_SPORES, ModFluids.FLOWING_VERDANT_SPORES);
    }
}