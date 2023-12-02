package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.datatable.DataTables;
import net.minecraft.util.Identifier;

public final class ModDataTables {

    public static final Identifier ARMOR_METAL_CONTENT = DataTables.of("armor/metal_content");
    public static final Identifier BLOCK_METAL_CONTENT = DataTables.of("block/metal_content");
    public static final Identifier SPORE_KILLING_RADIUS = DataTables.of(
            "block/spore_killing_radius");
    public static final Identifier ENTITY_METAL_CONTENT = DataTables.of("entity/metal_content");

    private ModDataTables() {}
}
