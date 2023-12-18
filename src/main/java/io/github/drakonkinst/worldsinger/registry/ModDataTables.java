package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.util.Identifier;

public final class ModDataTables {

    public static final Identifier ARMOR_METAL_CONTENT = Worldsinger.id("armor/metal_content");
    public static final Identifier BLOCK_METAL_CONTENT = Worldsinger.id("block/metal_content");
    public static final Identifier SPORE_KILLING_RADIUS = Worldsinger.id(
            "block/spore_killing_radius");
    public static final Identifier ENTITY_METAL_CONTENT = Worldsinger.id("entity/metal_content");
    public static final Identifier CONSUMABLE_HYDRATION = Worldsinger.id(
            "item/consumable_hydration");

    private ModDataTables() {}
}
