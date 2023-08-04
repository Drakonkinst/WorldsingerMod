package io.github.drakonkinst.examplemod;

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