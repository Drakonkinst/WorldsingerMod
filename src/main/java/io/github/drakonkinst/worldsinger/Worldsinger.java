package io.github.drakonkinst.worldsinger;

import io.github.drakonkinst.worldsinger.api.ModApi;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.block.ModCauldronBehaviors;
import io.github.drakonkinst.worldsinger.command.ModCommands;
import io.github.drakonkinst.worldsinger.datatable.DataTables;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.registry.ModDispenserBehaviors;
import io.github.drakonkinst.worldsinger.registry.ModPotions;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.worldgen.dimension.ModDimensionTypes;
import io.github.drakonkinst.worldsinger.worldgen.structure.ModStructurePieceTypes;
import io.github.drakonkinst.worldsinger.worldgen.structure.ModStructureTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worldsinger implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModConstants.MOD_ID);

    public static Identifier id(String id) {
        return new Identifier(ModConstants.MOD_ID, id);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Worldsinger...");

        DataTables.initialize();
        Fluidlogged.initialize();

        ModProperties.initialize();
        ModParticleTypes.initialize();
        ModSoundEvents.initialize();
        ModFluids.initialize();
        ModBlocks.initialize();
        ModItems.initialize();
        ModEntityTypes.initialize();
        ModCommands.initialize();
        ModPotions.register();
        ModCauldronBehaviors.register();
        ModDispenserBehaviors.register();
        ModDimensionTypes.initialize();
        ModStructurePieceTypes.initialize();
        ModStructureTypes.initialize();

        ModApi.initialize();
    }
}