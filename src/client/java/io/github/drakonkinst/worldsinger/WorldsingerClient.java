package io.github.drakonkinst.worldsinger;

import io.github.drakonkinst.worldsinger.datatable.DataTableRegistry;
import io.github.drakonkinst.worldsinger.datatable.DataTables;
import io.github.drakonkinst.worldsinger.dimension.ModDimensionEffects;
import io.github.drakonkinst.worldsinger.fluid.ModFluidRenderers;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.registry.ClientNetworkHandler;
import io.github.drakonkinst.worldsinger.registry.ModBlockRenderers;
import io.github.drakonkinst.worldsinger.registry.ModEntityRenderers;
import io.github.drakonkinst.worldsinger.registry.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class WorldsingerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldsingerClient.initializeDataTablesClient();

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

    public static void initializeDataTablesClient() {
        ClientPlayNetworking.registerGlobalReceiver(DataTables.DATA_TABLE_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    if (DataTableRegistry.INSTANCE == null) {
                        Worldsinger.LOGGER.error("DataTableRegistry is null on client side");
                    }
                    DataTableRegistry.INSTANCE.readPacket(buf);
                    Worldsinger.LOGGER.info(
                            "Loaded " + DataTableRegistry.INSTANCE.getDataTableIds().size()
                                    + " data tables");
                });
    }

}