package io.github.drakonkinst.worldsinger.api;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.SilverLined;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.util.Identifier;

public class ModApi {

    public static final ItemApiLookup<SilverLined, Void> SILVER_LINED_ITEM = ItemApiLookup.get(
            new Identifier(ModConstants.MOD_ID, "silver_lined"), SilverLined.class, Void.class);

    public static void initialize() {
        
    }
}
