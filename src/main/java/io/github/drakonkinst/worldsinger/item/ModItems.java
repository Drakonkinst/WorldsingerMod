package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.material.ModArmorMaterials;
import io.github.drakonkinst.worldsinger.material.ModToolMaterials;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItems {

    // Items

    // Spore Buckets
    public static final Item DEAD_SPORES_BUCKET = ModItems.register("dead_spores_bucket",
            new AetherSporeBucketItem(ModBlocks.DEAD_SPORE_BLOCK,
                    SoundEvents.BLOCK_POWDER_SNOW_PLACE,
                    new FabricItemSettings()
                            .recipeRemainder(Items.BUCKET)
                            .maxCount(1)));
    public static final Item VERDANT_SPORES_BUCKET = ModItems.register(
            "verdant_spores_bucket", new AetherSporeBucketItem(ModBlocks.VERDANT_SPORE_BLOCK,
                    SoundEvents.BLOCK_POWDER_SNOW_PLACE,
                    new FabricItemSettings()
                            .recipeRemainder(Items.BUCKET)
                            .maxCount(1)));
    public static final Item CRIMSON_SPORES_BUCKET = ModItems.register(
            "crimson_spores_bucket", new AetherSporeBucketItem(ModBlocks.CRIMSON_SPORE_BLOCK,
                    SoundEvents.BLOCK_POWDER_SNOW_PLACE,
                    new FabricItemSettings()
                            .recipeRemainder(Items.BUCKET)
                            .maxCount(1)));

    // Spore Bottles
    public static final Item DEAD_SPORES_BOTTLE = ModItems.register("dead_spores_bottle",
            new SporeBottleItem(
                    AetherSporeType.DEAD, new FabricItemSettings()
                    .maxCount(Items.POTION.getMaxCount())));
    public static final Item VERDANT_SPORES_BOTTLE = ModItems.register("verdant_spores_bottle",
            new SporeBottleItem(
                    AetherSporeType.VERDANT, new FabricItemSettings()
                    .maxCount(Items.POTION.getMaxCount())));
    public static final Item CRIMSON_SPORES_BOTTLE = ModItems.register("crimson_spores_bottle",
            new SporeBottleItem(
                    AetherSporeType.CRIMSON, new FabricItemSettings()
                    .maxCount(Items.POTION.getMaxCount())));
    public static final Item ZEPHYR_SPORES_BOTTLE = ModItems.register("zephyr_spores_bottle",
            new SporeBottleItem(
                    AetherSporeType.ZEPHYR, new FabricItemSettings()
                    .maxCount(Items.POTION.getMaxCount())));
    public static final Item SUNLIGHT_SPORES_BOTTLE = ModItems.register("sunlight_spores_bottle",
            new SporeBottleItem(
                    AetherSporeType.SUNLIGHT, new FabricItemSettings()
                    .maxCount(Items.POTION.getMaxCount())));
    public static final Item ROSEITE_SPORES_BOTTLE = ModItems.register("roseite_spores_bottle",
            new SporeBottleItem(
                    AetherSporeType.ROSEITE, new FabricItemSettings()
                    .maxCount(Items.POTION.getMaxCount())));
    public static final Item MIDNIGHT_SPORES_BOTTLE = ModItems.register("midnight_spores_bottle",
            new SporeBottleItem(
                    AetherSporeType.MIDNIGHT, new FabricItemSettings()
                    .maxCount(Items.POTION.getMaxCount())));

    // Spore Splash Bottle
    public static final Item DEAD_SPORES_SPLASH_BOTTLE = ModItems.register(
            "dead_spores_splash_bottle",
            new SplashSporeBottleItem(
                    AetherSporeType.DEAD, new FabricItemSettings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .maxCount(Items.SPLASH_POTION.getMaxCount())));
    public static final Item VERDANT_SPORES_SPLASH_BOTTLE = ModItems.register(
            "verdant_spores_splash_bottle",
            new SplashSporeBottleItem(
                    AetherSporeType.VERDANT, new FabricItemSettings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .maxCount(Items.SPLASH_POTION.getMaxCount())));
    public static final Item CRIMSON_SPORES_SPLASH_BOTTLE = ModItems.register(
            "crimson_spores_splash_bottle",
            new SplashSporeBottleItem(
                    AetherSporeType.CRIMSON, new FabricItemSettings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .maxCount(Items.SPLASH_POTION.getMaxCount())));
    public static final Item ZEPHYR_SPORES_SPLASH_BOTTLE = ModItems.register(
            "zephyr_spores_splash_bottle",
            new SplashSporeBottleItem(
                    AetherSporeType.ZEPHYR, new FabricItemSettings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .maxCount(Items.SPLASH_POTION.getMaxCount())));
    public static final Item SUNLIGHT_SPORES_SPLASH_BOTTLE = ModItems.register(
            "sunlight_spores_splash_bottle",
            new SplashSporeBottleItem(
                    AetherSporeType.SUNLIGHT, new FabricItemSettings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .maxCount(Items.SPLASH_POTION.getMaxCount())));
    public static final Item ROSEITE_SPORES_SPLASH_BOTTLE = ModItems.register(
            "roseite_spores_splash_bottle",
            new SplashSporeBottleItem(
                    AetherSporeType.ROSEITE, new FabricItemSettings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .maxCount(Items.SPLASH_POTION.getMaxCount())));
    public static final Item MIDNIGHT_SPORES_SPLASH_BOTTLE = ModItems.register(
            "midnight_spores_splash_bottle",
            new SplashSporeBottleItem(
                    AetherSporeType.MIDNIGHT, new FabricItemSettings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .maxCount(Items.SPLASH_POTION.getMaxCount())));


    public static final Item VERDANT_VINE = ModItems.register("verdant_vine",
            new Item(new FabricItemSettings().food(
                    new FoodComponent.Builder()
                            // Same as Dried Kelp
                            .hunger(1)
                            .saturationModifier(0.3f)
                            .snack()
                            .build())));
    public static final Item SALT = ModItems.register("salt",
            new SaltItem(new FabricItemSettings()));
    public static final Item RAW_SILVER = ModItems.register("raw_silver",
            new SaltItem(new FabricItemSettings()));
    public static final Item SILVER_INGOT = ModItems.register("silver_ingot",
            new Item(new FabricItemSettings()));
    public static final Item SILVER_NUGGET = ModItems.register("silver_nugget",
            new Item(new FabricItemSettings()));

    // Steel
    public static final Item CRUDE_IRON = ModItems.register("crude_iron",
            new Item(new FabricItemSettings()));
    public static final Item STEEL_INGOT = ModItems.register("steel_ingot",
            new Item(new FabricItemSettings()));
    public static final Item STEEL_NUGGET = ModItems.register("steel_nugget",
            new Item(new FabricItemSettings()));
    public static final Item STEEL_HELMET = ModItems.register("steel_helmet", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item STEEL_CHESTPLATE = ModItems.register("steel_chestplate", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item STEEL_LEGGINGS = ModItems.register("steel_leggings", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item STEEL_BOOTS = ModItems.register("steel_boots", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.BOOTS, new FabricItemSettings()));
    public static final Item STEEL_SWORD = ModItems.register("steel_sword", new SwordItem(
            ModToolMaterials.STEEL, 3, -2.4f, new FabricItemSettings()));
    public static final Item STEEL_PICKAXE = ModItems.register("steel_pickaxe", new PickaxeItem(
            ModToolMaterials.STEEL, 3, -2.4f, new FabricItemSettings()));
    public static final Item STEEL_AXE = ModItems.register("steel_axe", new AxeItem(
            ModToolMaterials.STEEL, 3, -2.4f, new FabricItemSettings()));
    public static final Item STEEL_SHOVEL = ModItems.register("steel_shovel", new ShovelItem(
            ModToolMaterials.STEEL, 3, -2.4f, new FabricItemSettings()));
    public static final Item STEEL_HOE = ModItems.register("steel_hoe", new HoeItem(
            ModToolMaterials.STEEL, 3, -2.4f, new FabricItemSettings()));
    public static final Item QUARTZ_AND_STEEL = ModItems.register("quartz_and_steel",
            new FlintAndSteelItem(new FabricItemSettings().maxDamage(88)));
    public static final Item FLINT_AND_IRON = ModItems.register("flint_and_iron",
            new FaultyFirestarterItem(0.33f, new FabricItemSettings().maxDamage(64)));
    public static final Item QUARTZ_AND_IRON = ModItems.register("quartz_and_iron",
            new FaultyFirestarterItem(0.33f, new FabricItemSettings().maxDamage(88)));

    // Item Groups
    private static final ItemGroup WORLDSINGER_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.VERDANT_VINE_SNARE))
            .displayName(Text.translatable("itemGroup.worldsinger.worldsinger"))
            .build();

    public static <T extends Item> T register(String id, T item) {
        Identifier itemId = Worldsinger.id(id);
        return Registry.register(Registries.ITEM, itemId, item);
    }

    public static void initialize() {
        // Custom item group
        Identifier moddedItemsIdentifier = Worldsinger.id("worldsinger");
        Registry.register(Registries.ITEM_GROUP, moddedItemsIdentifier, WORLDSINGER_ITEM_GROUP);
        RegistryKey<ItemGroup> moddedItemsItemGroupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP,
                moddedItemsIdentifier);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> {
            itemGroup.addAfter(Items.IRON_SWORD,
                    ModItems.STEEL_SWORD);
            itemGroup.addAfter(Items.IRON_AXE,
                    ModItems.STEEL_AXE);
            itemGroup.addAfter(Items.IRON_BOOTS,
                    ModItems.STEEL_HELMET,
                    ModItems.STEEL_CHESTPLATE,
                    ModItems.STEEL_LEGGINGS,
                    ModItems.STEEL_BOOTS);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.addAfter(Items.IRON_HOE,
                    ModItems.STEEL_SHOVEL,
                    ModItems.STEEL_PICKAXE,
                    ModItems.STEEL_AXE,
                    ModItems.STEEL_HOE);
            itemGroup.addAfter(Items.FLINT_AND_STEEL,
                    ModItems.QUARTZ_AND_STEEL,
                    ModItems.FLINT_AND_IRON,
                    ModItems.QUARTZ_AND_IRON);
            itemGroup.addAfter(Items.MILK_BUCKET,
                    ModItems.VERDANT_SPORES_BUCKET,
                    ModItems.DEAD_SPORES_BUCKET);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.addAfter(Items.DAMAGED_ANVIL,
                    ModBlocks.STEEL_ANVIL,
                    ModBlocks.CHIPPED_STEEL_ANVIL,
                    ModBlocks.DAMAGED_STEEL_ANVIL);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register((itemGroup) -> {
            itemGroup.addAfter(Items.DEEPSLATE,
                    ModBlocks.SALTSTONE);
            itemGroup.addAfter(Items.DEEPSLATE_COAL_ORE,
                    ModBlocks.SALTSTONE_SALT_ORE
            );
            itemGroup.addAfter(Items.DEEPSLATE_GOLD_ORE,
                    ModBlocks.SILVER_ORE,
                    ModBlocks.DEEPSLATE_SILVER_ORE
            );
            itemGroup.addAfter(Items.WET_SPONGE,
                    ModBlocks.VERDANT_SPORE_BLOCK,
                    ModBlocks.DEAD_SPORE_BLOCK,
                    ModBlocks.VERDANT_VINE_BLOCK,
                    ModBlocks.VERDANT_VINE_BRANCH,
                    ModBlocks.VERDANT_VINE_SNARE,
                    ModBlocks.TWISTING_VERDANT_VINES,
                    ModBlocks.DEAD_VERDANT_VINE_BLOCK,
                    ModBlocks.DEAD_VERDANT_VINE_BRANCH,
                    ModBlocks.DEAD_VERDANT_VINE_SNARE,
                    ModBlocks.DEAD_TWISTING_VERDANT_VINES
            );
            itemGroup.addAfter(Items.RAW_GOLD_BLOCK,
                    ModBlocks.RAW_SILVER_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            itemGroup.addAfter(Items.RAW_GOLD,
                    ModItems.RAW_SILVER);
            itemGroup.addAfter(Items.IRON_NUGGET,
                    ModItems.STEEL_NUGGET);
            itemGroup.addAfter(Items.IRON_INGOT,
                    ModItems.STEEL_INGOT);
            itemGroup.addAfter(Items.GOLD_NUGGET,
                    ModItems.SILVER_NUGGET);
            itemGroup.addAfter(Items.GOLD_INGOT,
                    ModItems.SILVER_INGOT);
            itemGroup.addBefore(Items.NETHERITE_SCRAP,
                    ModItems.CRUDE_IRON);
            itemGroup.addAfter(Items.SUGAR,
                    ModItems.SALT);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((itemGroup) -> {
            itemGroup.addBefore(Items.GOLD_BLOCK,
                    ModBlocks.STEEL_BLOCK);
            itemGroup.addBefore(Items.REDSTONE_BLOCK,
                    ModBlocks.SILVER_BLOCK);
            itemGroup.addAfter(Items.NETHERITE_BLOCK,
                    ModBlocks.SALT_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((itemGroup) -> {
            itemGroup.addAfter(Items.DRIED_KELP,
                    ModItems.VERDANT_VINE);
        });

        ItemGroupEvents.modifyEntriesEvent(moddedItemsItemGroupKey).register((itemGroup) -> {
            // Only include the highlight items/blocks here
            itemGroup.add(ModItems.DEAD_SPORES_BUCKET);
            itemGroup.add(ModItems.VERDANT_SPORES_BUCKET);
            itemGroup.add(ModItems.CRIMSON_SPORES_BUCKET);
            
            itemGroup.add(ModBlocks.VERDANT_VINE_BLOCK);
            itemGroup.add(ModBlocks.VERDANT_VINE_BRANCH);
            itemGroup.add(ModBlocks.VERDANT_VINE_SNARE);
            itemGroup.add(ModBlocks.TWISTING_VERDANT_VINES);
            itemGroup.add(ModItems.SALT);
            itemGroup.add(ModItems.SILVER_INGOT);
            itemGroup.add(ModItems.STEEL_INGOT);
            itemGroup.add(ModBlocks.SALTSTONE);
            itemGroup.add(ModBlocks.SILVER_BLOCK);
            itemGroup.add(ModBlocks.STEEL_BLOCK);

            itemGroup.add(ModItems.VERDANT_SPORES_BOTTLE);
            itemGroup.add(ModItems.VERDANT_SPORES_SPLASH_BOTTLE);
            itemGroup.add(ModItems.DEAD_SPORES_BOTTLE);
            itemGroup.add(ModItems.DEAD_SPORES_SPLASH_BOTTLE);
            itemGroup.add(ModItems.CRIMSON_SPORES_BOTTLE);
            itemGroup.add(ModItems.CRIMSON_SPORES_SPLASH_BOTTLE);
            itemGroup.add(ModItems.ZEPHYR_SPORES_BOTTLE);
            itemGroup.add(ModItems.ZEPHYR_SPORES_SPLASH_BOTTLE);
            itemGroup.add(ModItems.SUNLIGHT_SPORES_BOTTLE);
            itemGroup.add(ModItems.SUNLIGHT_SPORES_SPLASH_BOTTLE);
            itemGroup.add(ModItems.ROSEITE_SPORES_BOTTLE);
            itemGroup.add(ModItems.ROSEITE_SPORES_SPLASH_BOTTLE);
            itemGroup.add(ModItems.MIDNIGHT_SPORES_BOTTLE);
            itemGroup.add(ModItems.MIDNIGHT_SPORES_SPLASH_BOTTLE);
        });
    }

    private ModItems() {}
}
