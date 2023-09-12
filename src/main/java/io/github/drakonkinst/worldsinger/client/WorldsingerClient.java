package io.github.drakonkinst.worldsinger.client;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.worldgen.dimension.ModDimensionEffects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WorldsingerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Register fluids
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.VERDANT_SPORES,
                ModFluids.FLOWING_VERDANT_SPORES,
                new SimpleFluidRenderHandler(
                        new Identifier(ModConstants.MOD_ID, "block/verdant_spore_sea_block_still"),
                        new Identifier(ModConstants.MOD_ID, "block/verdant_spore_sea_block_flow")
                ));
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.DEAD_SPORES,
                ModFluids.FLOWING_DEAD_SPORES,
                new SimpleFluidRenderHandler(
                        new Identifier(ModConstants.MOD_ID, "block/dead_spore_sea_block_still"),
                        new Identifier(ModConstants.MOD_ID, "block/dead_spore_sea_block_flow")
                ));

        // Register fluid render layer as translucent (needed to make boats cull properly)
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.VERDANT_SPORES,
                ModFluids.FLOWING_VERDANT_SPORES,
                ModFluids.DEAD_SPORES,
                ModFluids.FLOWING_DEAD_SPORES);

        // Register block render layer
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.VERDANT_VINE_SNARE,
                ModBlocks.TWISTING_VERDANT_VINES,
                ModBlocks.TWISTING_VERDANT_VINES_PLANT,
                ModBlocks.DEAD_VERDANT_VINE_SNARE,
                ModBlocks.DEAD_TWISTING_VERDANT_VINES,
                ModBlocks.DEAD_TWISTING_VERDANT_VINES_PLANT
        );

        // Register particles
        ParticleFactoryRegistry.getInstance()
                .register(ModParticleTypes.SPORE_DUST, SporeDustParticle.Factory::new);

        // Register entity renderers
        EntityRendererRegistry.register(ModEntityTypes.THROWN_SPORE_BOTTLE,
                FlyingItemEntityRenderer::new);

        ModModelPredicates.register();

        ModDimensionEffects.initialize();
    }
}