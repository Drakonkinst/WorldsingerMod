package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.util.Constants;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class DataTables {

    public static final RegistryKey<DataTable> SPORE_KILLING_RADIUS = DataTables.of(
            "spore_killing_radius");

    public static void register() {
        DynamicRegistries.register(ModRegistries.DATA_TABLES, DataTable.CODEC);
    }

    private static RegistryKey<DataTable> of(String id) {
        return RegistryKey.of(ModRegistries.DATA_TABLES, new Identifier(Constants.MOD_ID, id));
    }

    public static DataTable get(World world, RegistryKey<DataTable> key) {
        if (world.isClient()) {
            // Data tables are not synced on client
            return null;
        }
        return world.getRegistryManager().get(ModRegistries.DATA_TABLES).get(key);
    }

    public static Optional<DataTable> getOptional(World world, Identifier id) {
        if (world.isClient()) {
            // Data tables are not synced on client
            return Optional.empty();
        }
        return Optional.ofNullable(
                world.getRegistryManager().get(ModRegistries.DATA_TABLES).get(id));
    }

    public static Set<Identifier> getDataTableIds(World world) {
        return world.getRegistryManager().get(ModRegistries.DATA_TABLES).getIds();
    }

    private DataTables() {}
}
