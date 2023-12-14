package io.github.drakonkinst.worldsinger;

import io.github.drakonkinst.worldsinger.dimension.ModDimensionEffects;
import io.github.drakonkinst.worldsinger.fluid.ModFluidRenderers;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.registry.ClientNetworkHandler;
import io.github.drakonkinst.worldsinger.registry.ModBlockRenderers;
import io.github.drakonkinst.worldsinger.registry.ModEntityRenderers;
import io.github.drakonkinst.worldsinger.registry.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class WorldsingerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModFluidRenderers.register();
        ModBlockRenderers.register();
        ModEntityRenderers.register();

        // Register particles
        ParticleFactoryRegistry.getInstance()
                .register(ModParticleTypes.SPORE_DUST, SporeDustParticle.Factory::new);

        ModModelPredicates.register();
        ModDimensionEffects.initialize();

        ClientNetworkHandler.registerPacketHandlers();
    }
}