package io.github.drakonkinst.worldsinger.api;

import io.github.drakonkinst.worldsinger.item.SilverLinedBoatItemData;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.world.SilverLined;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ModApi {

    public static final ItemApiLookup<SilverLined, Void> SILVER_LINED_ITEM = ItemApiLookup.get(
            new Identifier(ModConstants.MOD_ID, "silver_lined"), SilverLined.class, Void.class);

    public static void initialize() {
        ModApi.SILVER_LINED_ITEM.registerForItems(
                (stack, context) -> new SilverLinedBoatItemData(stack),
                Items.ACACIA_BOAT,
                Items.BIRCH_BOAT,
                Items.CHERRY_BOAT,
                Items.DARK_OAK_BOAT,
                Items.JUNGLE_BOAT,
                Items.MANGROVE_BOAT,
                Items.OAK_BOAT,
                Items.SPRUCE_BOAT,
                Items.BAMBOO_RAFT,
                Items.ACACIA_CHEST_BOAT,
                Items.BIRCH_CHEST_BOAT,
                Items.CHERRY_CHEST_BOAT,
                Items.DARK_OAK_CHEST_BOAT,
                Items.JUNGLE_CHEST_BOAT,
                Items.MANGROVE_CHEST_BOAT,
                Items.OAK_CHEST_BOAT,
                Items.SPRUCE_CHEST_BOAT,
                Items.BAMBOO_CHEST_RAFT
        );
    }
}
