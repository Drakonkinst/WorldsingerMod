package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {

    // Dead Spores
    public static final Block DEAD_SPORE_SEA_BLOCK = register(
            "dead_spore_sea_block",
            new AetherSporeFluidBlock(ModFluids.DEAD_SPORES, AetherSporeType.DEAD,
                    FabricBlockSettings.create()
                            .strength(100.0f)
                            .mapColor(MapColor.GRAY)
                            .replaceable()
                            .noCollision()
                            .pistonBehavior(PistonBehavior.DESTROY)
                            .dropsNothing()
                            .liquid()
                            .sounds(BlockSoundGroup.SAND)
            ), false);
    public static final Block DEAD_SPORE_BLOCK = register("dead_spore_block",
            new AetherSporeBlock(AetherSporeType.DEAD, ModBlocks.DEAD_SPORE_SEA_BLOCK,
                    FabricBlockSettings.create()
                            // 0.5x strength of living version
                            .strength(0.5f)
                            .mapColor(MapColor.GRAY)
                            .sounds(BlockSoundGroup.SAND)
            ), true);
    public static final Block DEAD_VERDANT_VINE_BLOCK = register("dead_verdant_vine_block",
            new VerdantVineBlock(
                    FabricBlockSettings.create()
                            // 0.5x strength of living version
                            .strength(1.0f)
                            .sounds(BlockSoundGroup.WOOD)
                            .ticksRandomly()
            ), true);
    public static final Block DEAD_VERDANT_VINE_BRANCH = register("dead_verdant_vine_branch",
            new VerdantVineBranchBlock(
                    FabricBlockSettings.create()
                            // 0.5x strength of living version
                            .strength(0.4f)
                            .nonOpaque()
                            .ticksRandomly()
            ), true);
    public static final Block DEAD_VERDANT_VINE_SNARE = register("dead_verdant_vine_snare",
            new VerdantVineSnareBlock(
                    FabricBlockSettings.create()
                            .solid()
                            .noCollision()
                            .requiresTool()
                            // 0.5x strength of living version
                            .strength(0.4f)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), true);
    public static final Block DEAD_TWISTING_VERDANT_VINES = register("dead_twisting_verdant_vines",
            new TwistingVerdantVineBlock(
                    FabricBlockSettings.create()
                            // 0.5x strength of living version
                            .strength(0.2f)
                            .noCollision()
                            .sounds(BlockSoundGroup.WEEPING_VINES)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), true);
    public static final Block DEAD_TWISTING_VERDANT_VINES_PLANT = register(
            "dead_twisting_verdant_vines_plant",
            new TwistingVerdantVineStemBlock(
                    FabricBlockSettings.create()
                            // 0.5x strength of living version
                            .strength(0.2f)
                            .noCollision()
                            .sounds(BlockSoundGroup.WEEPING_VINES)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), false);

    // Verdant Spores
    public static final Block VERDANT_SPORE_SEA_BLOCK = register("verdant_spore_sea_block",
            new LivingAetherSporeFluidBlock(ModFluids.VERDANT_SPORES, AetherSporeType.VERDANT,
                    FabricBlockSettings.create()
                            .strength(100.0f)
                            .mapColor(MapColor.DARK_GREEN)
                            .replaceable()
                            .noCollision()
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
                            .dropsNothing()
                            .liquid()
                            .sounds(BlockSoundGroup.SAND)
            ), false);
    public static final Block VERDANT_SPORE_BLOCK =
            register("verdant_spore_block",
                    new LivingAetherSporeBlock(AetherSporeType.VERDANT,
                            ModBlocks.VERDANT_SPORE_SEA_BLOCK,
                            FabricBlockSettings.create()
                                    .strength(0.5f)
                                    .mapColor(MapColor.DARK_GREEN)
                                    .ticksRandomly()
                                    .sounds(BlockSoundGroup.SAND)
                    ), true);
    public static final Block VERDANT_VINE_BLOCK = register("verdant_vine_block",
            new LivingVerdantVineBlock(
                    FabricBlockSettings.create()
                            // Same strength as log
                            .strength(2.0f)
                            .sounds(BlockSoundGroup.WOOD)
                            .requiresTool()
                            .ticksRandomly()
            ), true);
    public static final Block VERDANT_VINE_BRANCH = register(
            "verdant_vine_branch",
            new LivingVerdantVineBranchBlock(
                    FabricBlockSettings.create()
                            // 2x the strength of Chorus Plant
                            .strength(0.8f)
                            .requiresTool()
                            .nonOpaque()
                            .ticksRandomly()
            ), true);
    public static final Block VERDANT_VINE_SNARE = register("verdant_vine_snare",
            new LivingVerdantVineSnareBlock(
                    FabricBlockSettings.create()
                            .solid()
                            .noCollision()
                            .requiresTool()
                            // Same strength as branch
                            .strength(0.8f)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), true);
    public static final Block TWISTING_VERDANT_VINES = register("twisting_verdant_vines",
            new LivingTwistingVerdantVineBlock(
                    FabricBlockSettings.create()
                            // Same strength as Ladder
                            .strength(0.4f)
                            .noCollision()
                            .sounds(BlockSoundGroup.WEEPING_VINES)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)),
            true);
    public static final Block TWISTING_VERDANT_VINES_PLANT = register(
            "twisting_verdant_vines_plant",
            new LivingTwistingVerdantVineStemBlock(
                    FabricBlockSettings.create()
                            // Same strength as Twisting Verdant Vines
                            .strength(0.4f)
                            .noCollision()
                            .sounds(BlockSoundGroup.WEEPING_VINES)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), false);
    public static final Block VERDANT_SPORE_CAULDRON = register("verdant_spore_cauldron",
            new LivingSporeCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON)
                    .ticksRandomly(),
                    ModCauldronBehaviors.VERDANT_SPORE_CAULDRON_BEHAVIOR, AetherSporeType.VERDANT
            ), false);
    public static final Block DEAD_SPORE_CAULDRON = register("dead_spore_cauldron",
            new SporeCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON),
                    ModCauldronBehaviors.DEAD_SPORE_CAULDRON_BEHAVIOR, AetherSporeType.DEAD
            ), false);

    // Other
    public static final Block SALTSTONE = register("saltstone",
            new Block(FabricBlockSettings.create()
                    .requiresTool()
                    // Slightly harder to break than Netherrack
                    .strength(0.5f, 6.0f)
                    .sounds(BlockSoundGroup.NETHERRACK)
            ), true);
    public static final Block SALTSTONE_SALT_ORE = register("saltstone_salt_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.create()
                    .requiresTool()
                    // Slightly easier to break than Nether Gold Ore
                    .strength(2.5f, 3.0f)
            ), true);
    public static final Block SALT_BLOCK = register("salt_block", new Block(
            FabricBlockSettings.create()
                    .requiresTool()
                    // Same strength as Calcite
                    .strength(0.75f)
                    .sounds(BlockSoundGroup.CALCITE)
    ), true);
    public static final Block SILVER_ORE = register("silver_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.create()
                    .requiresTool()
                    // Equal to Gold Ore
                    .strength(3.0f, 3.0f)
            ), true);
    public static final Block DEEPSLATE_SILVER_ORE = register("deepslate_silver_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.create()
                    .requiresTool()
                    // Equal to Deepslate Gold Ore
                    .strength(4.5f, 3.0f)
            ), true);
    public static final Block SILVER_BLOCK = register("silver_block", new Block(
            FabricBlockSettings.create()
                    .requiresTool()
                    // Same strength as Gold Block
                    .strength(3.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
    ), true);
    public static final Block RAW_SILVER_BLOCK = register("raw_silver_block", new Block(
            FabricBlockSettings.create()
                    .requiresTool()
                    // Same strength as Raw Gold Block
                    .strength(5.0f, 6.0f)
    ), true);

    // Steel
    public static final Block STEEL_BLOCK = register("steel_block", new Block(
            FabricBlockSettings.create()
                    .requiresTool()
                    // +1 strength of Iron Block, same blast resistance as End Stone
                    .strength(6.0f, 9.0f)
    ), true);
    public static final Block STEEL_ANVIL = register("steel_anvil", new SteelAnvilBlock(
            FabricBlockSettings.create()
                    .requiresTool()
                    // +1 strength of Iron Anvil, same blast resistance as Anvil
                    .strength(6.0f, 1200.0f)
                    .sounds(BlockSoundGroup.ANVIL)
                    .pistonBehavior(PistonBehavior.BLOCK)
    ), true);
    public static final Block CHIPPED_STEEL_ANVIL = register("chipped_steel_anvil",
            new SteelAnvilBlock(
                    FabricBlockSettings.create()
                            .requiresTool()
                            // Same settings as Steel Anvil
                            .strength(6.0f, 1200.0f)
                            .sounds(BlockSoundGroup.ANVIL)
                            .pistonBehavior(PistonBehavior.BLOCK)
            ), true);
    public static final Block DAMAGED_STEEL_ANVIL = register("damaged_steel_anvil",
            new SteelAnvilBlock(
                    FabricBlockSettings.create()
                            .requiresTool()
                            // Same settings as Steel Anvil
                            .strength(6.0f, 1200.0f)
                            .sounds(BlockSoundGroup.ANVIL)
                            .pistonBehavior(PistonBehavior.BLOCK)
            ), true);

    public static <T extends Block> T register(String id, T block, boolean shouldRegisterItem) {
        Identifier blockId = Worldsinger.id(id);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
            Registry.register(Registries.ITEM, blockId, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockId, block);
    }

    public static void initialize() {}

    private ModBlocks() {}
}
