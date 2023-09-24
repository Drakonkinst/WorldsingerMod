package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.util.Identifier;

public final class ModLootTables {

    public static final Identifier LUMAR_SHIPWRECK_MAP_CHEST = ModLootTables.of(
            "chests/lumar_shipwreck_map");
    public static final Identifier LUMAR_SHIPWRECK_SUPPLY_CHEST = ModLootTables.of(
            "chests/lumar_shipwreck_supply");
    public static final Identifier LUMAR_SHIPWRECK_TREASURE_CHEST = ModLootTables.of(
            "chests/lumar_shipwreck_treasure");

    private static Identifier of(String id) {
        return Worldsinger.id(id);
    }

    private ModLootTables() {}
}
