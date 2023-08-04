package io.github.drakonkinst.examplemod;

import io.github.drakonkinst.examplemod.block.ModBlocks;
import io.github.drakonkinst.examplemod.fluid.Fluidlogged;
import io.github.drakonkinst.examplemod.fluid.ModFluids;
import io.github.drakonkinst.examplemod.item.ModItems;
import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOGGER.info("Hello Fabric world!");

        ModFluids.initialize();
        ModBlocks.initialize();
        ModItems.initialize();

        Fluidlogged.initialize();
    }
}