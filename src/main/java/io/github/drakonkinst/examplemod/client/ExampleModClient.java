package io.github.drakonkinst.examplemod.client;

import io.github.drakonkinst.examplemod.Constants;
import io.github.drakonkinst.examplemod.fluid.ModFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.VERDANT_SPORES,
                ModFluids.FLOWING_VERDANT_SPORES,
                new SimpleFluidRenderHandler(
                        new Identifier(Constants.MOD_ID, "block/verdant_spore_sea_block_still"),
                        new Identifier(Constants.MOD_ID, "block/verdant_spore_sea_block_flow")
                ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.VERDANT_SPORES, ModFluids.FLOWING_VERDANT_SPORES);
    }
}