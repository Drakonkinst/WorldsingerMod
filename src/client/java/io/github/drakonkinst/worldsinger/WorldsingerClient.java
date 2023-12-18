package io.github.drakonkinst.worldsinger;

import io.github.drakonkinst.worldsinger.cosmere.ShapeshiftingBridge;
import io.github.drakonkinst.worldsinger.dimension.ModDimensionEffects;
import io.github.drakonkinst.worldsinger.fluid.ModFluidRenderers;
import io.github.drakonkinst.worldsinger.network.ClientNetworkHandler;
import io.github.drakonkinst.worldsinger.network.ShapeshiftingClientBridge;
import io.github.drakonkinst.worldsinger.particle.ModParticleManager;
import io.github.drakonkinst.worldsinger.registry.ModBlockRenderers;
import io.github.drakonkinst.worldsinger.registry.ModEntityRenderers;
import io.github.drakonkinst.worldsinger.registry.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;

public class WorldsingerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ShapeshiftingBridge bridge = new ShapeshiftingClientBridge();

        ModFluidRenderers.register();
        ModBlockRenderers.register();
        ModEntityRenderers.register();

        // Register particles
        ModParticleManager.register();

        ModModelPredicates.register();
        ModDimensionEffects.initialize();

        ClientNetworkHandler.registerPacketHandlers();
    }
}