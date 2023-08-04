package io.github.drakonkinst.examplemod;

import io.github.drakonkinst.examplemod.block.ModBlocks;
import io.github.drakonkinst.examplemod.fluid.Fluidlogged;
import io.github.drakonkinst.examplemod.fluid.ModFluids;
import io.github.drakonkinst.examplemod.item.ModItems;
import io.github.drakonkinst.examplemod.util.fog.FogModifier;
import io.github.drakonkinst.examplemod.util.fog.FogModifiers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.FogShape;
import net.minecraft.world.biome.BiomeKeys;

public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOGGER.info("Hello Fabric world!");

        ModFluids.initialize();
        ModBlocks.initialize();
        ModItems.initialize();

        Fluidlogged.initialize();

        FogModifiers.register(FogModifier.InjectionPoint.WATER,
                new FogModifier.Builder()
                        .predicate(fogFunction -> fogFunction.biomeEntry() != null && fogFunction.biomeEntry().matchesKey(BiomeKeys.OCEAN))
                        .fogStart(fogFunction -> fogFunction.viewDistance() * .5f)
                        .fogEnd(fogFunction -> Math.min(fogFunction.viewDistance(), 192f) * .5f)
                        .densitySpeedTicks(0.001f)
                        .fogShape(FogShape.SPHERE)
                        .color(0x00ff00)
                        .build()
        );
    }
}