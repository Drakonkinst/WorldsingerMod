package io.github.drakonkinst.worldsinger.client;

import io.github.drakonkinst.worldsinger.client.dimension.ModDimensionEffects;
import io.github.drakonkinst.worldsinger.client.fluid.ModFluidRenderers;
import io.github.drakonkinst.worldsinger.client.registry.ClientNetworkHandler;
import io.github.drakonkinst.worldsinger.client.registry.ModBlockRenderers;
import io.github.drakonkinst.worldsinger.client.registry.ModEntityRenderers;
import io.github.drakonkinst.worldsinger.client.registry.ModModelPredicates;
import io.github.drakonkinst.worldsinger.datatable.DataTables;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

@Environment(EnvType.CLIENT)
public class WorldsingerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DataTables.initializeClient();

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