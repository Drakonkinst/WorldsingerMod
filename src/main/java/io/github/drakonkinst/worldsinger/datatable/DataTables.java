package io.github.drakonkinst.worldsinger.datatable;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import java.util.Optional;
import net.minecraft.util.Identifier;

public final class DataTables {

    public static final Identifier ARMOR_METAL_CONTENT = DataTables.of("armor/metal_content");
    public static final Identifier BLOCK_METAL_CONTENT = DataTables.of("block/metal_content");
    public static final Identifier SPORE_KILLING_RADIUS = DataTables.of(
            "block/spore_killing_radius");
    public static final Identifier ENTITY_METAL_CONTENT = DataTables.of("entity/metal_content");

    public static void initialize() {}

    public static DataTable get(Identifier id) {
        if (DataTableRegistry.INSTANCE == null) {
            ModConstants.LOGGER.error(
                    "Error: Attempted to access data tables but data tables have not been initialized");
        }
        // Temp check
        // if (!DataTables.contains(id)) {
        //     ModConstants.LOGGER.warn("Data table " + id + " does not exist, returning dummy");
        // }
        return DataTableRegistry.INSTANCE.get(id);
    }

    public static Optional<DataTable> getOptional(Identifier id) {
        if (DataTableRegistry.INSTANCE == null) {
            ModConstants.LOGGER.error(
                    "Error: Attempted to access data tables but data tables have not been initialized");
        }
        return DataTableRegistry.INSTANCE.getOptional(id);
    }

    public static boolean contains(Identifier id) {
        if (DataTableRegistry.INSTANCE == null) {
            ModConstants.LOGGER.error(
                    "Error: Attempted to access data tables but data tables have not been initialized");
        }
        return DataTableRegistry.INSTANCE.contains(id);
    }

    private static Identifier of(String id) {
        return new Identifier(ModConstants.MOD_ID, id);
    }

    private DataTables() {}
}
