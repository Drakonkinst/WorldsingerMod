package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.util.Identifier;

public final class ModLootTables {

    public static final Identifier LUMAR_SHIPWRECK_SPROUTER_CHEST = ModLootTables.of(
            "chests/lumar_shipwreck_sprouter");
    public static final Identifier LUMAR_SHIPWRECK_SUPPLY_CHEST = ModLootTables.of(
            "chests/lumar_shipwreck_supply");
    public static final Identifier LUMAR_SHIPWRECK_CAPTAIN_CHEST = ModLootTables.of(
            "chests/lumar_shipwreck_captain");
    public static final Identifier LUMAR_SALTSTONE_MINESHAFT_CHEST = ModLootTables.of(
            "chests/lumar_saltstone_mineshaft");

    private static Identifier of(String id) {
        return Worldsinger.id(id);
    }

    private ModLootTables() {}
}
