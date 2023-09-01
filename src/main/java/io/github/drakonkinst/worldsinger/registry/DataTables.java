package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class DataTables {

    public static final RegistryKey<DataTable> SPORE_KILLING_RADIUS = DataTables.of(
            "spore_killing_radius");
    public static final RegistryKey<DataTable> ENTITY_WEIGHT = DataTables.of(
            "entity_weight");

    private static final Int2ObjectMap<DataTable> dummyDataTables = new Int2ObjectArrayMap<>();

    private DataTables() {}


    public static void register() {
        DynamicRegistries.registerSynced(ModRegistries.DATA_TABLES, DataTable.CODEC);
    }

    private static RegistryKey<DataTable> of(String id) {
        return RegistryKey.of(ModRegistries.DATA_TABLES, new Identifier(ModConstants.MOD_ID, id));
    }

    private static DataTable createOrGetDummyDataTable(int defaultValue) {
        return DataTables.dummyDataTables.computeIfAbsent(defaultValue, DataTable::new);
    }

    @NotNull
    public static DataTable getOrElse(World world, RegistryKey<DataTable> key, int defaultValue) {
        DataTable dataTable = world.getRegistryManager().get(ModRegistries.DATA_TABLES).get(key);
        if (dataTable == null) {
            return DataTables.createOrGetDummyDataTable(defaultValue);
        }
        return dataTable;
    }

    public static Optional<DataTable> getOrEmpty(World world, Identifier id) {
        return world.getRegistryManager().get(ModRegistries.DATA_TABLES).getOrEmpty(id);
    }

    public static Set<Identifier> getDataTableIds(World world) {
        return world.getRegistryManager().get(ModRegistries.DATA_TABLES).getIds();
    }
}
