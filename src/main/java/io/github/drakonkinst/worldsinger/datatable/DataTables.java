package io.github.drakonkinst.worldsinger.datatable;

import io.github.drakonkinst.worldsinger.Worldsinger;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public final class DataTables {

    private static final Identifier DATA_TABLE_PACKET_ID = Worldsinger.id("data_table");

    private static void initialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new DataTableRegistry());
    }

    public static void initializeServer() {
        DataTables.initialize();

        CommonLifecycleEvents.TAGS_LOADED.register(((registries, client) -> {
            if (DataTableRegistry.INSTANCE != null) {
                DataTableRegistry.INSTANCE.resolveTags();
            } else {
                Worldsinger.LOGGER.error(
                        "Failed to resolve tags for data tables: Data tables not initialized for isClient: "
                                + client);
            }
        }));

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(((player, joined) -> {
            if (DataTableRegistry.INSTANCE == null) {
                Worldsinger.LOGGER.error("DataTableRegistry is null on server side");
                return;
            }
            if (!DataTableRegistry.INSTANCE.areTagsResolved()) {
                Worldsinger.LOGGER.error("Tags are not resolved on server side");
                return;
            }

            PacketByteBuf buf = PacketByteBufs.create();
            DataTableRegistry.INSTANCE.writePacket(buf);
            ServerPlayNetworking.send(player, DATA_TABLE_PACKET_ID, buf);
        }));
    }

    @Environment(EnvType.CLIENT)
    public static void initializeClient() {
        DataTables.initialize();

        ClientPlayNetworking.registerGlobalReceiver(DATA_TABLE_PACKET_ID,
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

    // Warning: Do NOT cache DataTable instances! They are overwritten each reload
    // and this will cause data leakage.
    public static DataTable get(Identifier id) {
        if (DataTableRegistry.INSTANCE == null) {
            Worldsinger.LOGGER.error(
                    "Error: Attempted to access data tables but data tables have not been initialized");
        }
        return DataTableRegistry.INSTANCE.get(id);
    }

    public static Optional<DataTable> getOptional(Identifier id) {
        if (DataTableRegistry.INSTANCE == null) {
            Worldsinger.LOGGER.error(
                    "Error: Attempted to access data tables but data tables have not been initialized");
        }
        return DataTableRegistry.INSTANCE.getOptional(id);
    }

    public static boolean contains(Identifier id) {
        if (DataTableRegistry.INSTANCE == null) {
            Worldsinger.LOGGER.error(
                    "Error: Attempted to access data tables but data tables have not been initialized");
        }
        return DataTableRegistry.INSTANCE.contains(id);
    }

    public static Identifier of(String id) {
        return Worldsinger.id(id);
    }

    private DataTables() {}
}
