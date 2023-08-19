package io.github.drakonkinst.worldsinger;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.command.ModCommands;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import net.fabricmc.api.ModInitializer;

public class Worldsinger implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOGGER.info("Initializing Worldsinger...");

        ModFluids.initialize();
        ModBlocks.initialize();
        ModItems.initialize();
        ModCommands.initialize();

        Fluidlogged.initialize();
    }
}