package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
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
                            .strength(0.5f)
                            .mapColor(MapColor.GRAY)
                            .sounds(BlockSoundGroup.SAND)
            ), true);
    public static final Block DEAD_VERDANT_VINE_BLOCK = register("dead_verdant_vine_block",
            new VerdantVineBlock(
                    FabricBlockSettings.create()
                            .strength(1.0f)
                            .sounds(BlockSoundGroup.WOOD)
                            .ticksRandomly()
            ), true);
    public static final Block DEAD_VERDANT_VINE_BRANCH = register("dead_verdant_vine_branch",
            new VerdantVineBranchBlock(
                    FabricBlockSettings.create()
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
                            .strength(0.4f)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), true);
    public static final Block DEAD_TWISTING_VERDANT_VINES = register("dead_twisting_verdant_vines",
            new TwistingVerdantVineBlock(
                    FabricBlockSettings.create()
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
                    FabricBlockSettings.create().strength(2.0f).sounds(BlockSoundGroup.WOOD)
                            .ticksRandomly()
            ), true);
    public static final Block VERDANT_VINE_BRANCH = register(
            "verdant_vine_branch",
            new LivingVerdantVineBranchBlock(
                    FabricBlockSettings.create()
                            .strength(0.8f)
                            .nonOpaque()
                            .ticksRandomly()
            ), true);
    public static final Block VERDANT_VINE_SNARE = register("verdant_vine_snare",
            new LivingVerdantVineSnareBlock(
                    FabricBlockSettings.create()
                            .solid()
                            .noCollision()
                            .requiresTool()
                            .strength(0.8f)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), true);
    public static final Block TWISTING_VERDANT_VINES = register("twisting_verdant_vines",
            new LivingTwistingVerdantVineBlock(
                    FabricBlockSettings.create()
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
                            .strength(0.4f)
                            .noCollision()
                            .sounds(BlockSoundGroup.WEEPING_VINES)
                            .ticksRandomly()
                            .pistonBehavior(PistonBehavior.DESTROY)
            ), false);

    // Other
    public static final Block SALTSTONE = register("saltstone",
            new Block(FabricBlockSettings.create()
                    .requiresTool()
                    .strength(1.5f, 6.0f)
            ), true);
    public static final Block SALTSTONE_SALT_ORE = register("saltstone_salt_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.create()
                    .requiresTool()
                    .strength(3.0f, 3.0f)
            ), true);
    public static final Block SALTSTONE_SILVER_ORE = register("saltstone_silver_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.create()
                    .requiresTool()
                    .strength(3.0f, 3.0f)
            ), true);
    public static final Block SALT_BLOCK = register("salt_block", new Block(
            FabricBlockSettings.create()
                    .requiresTool()
                    .strength(5.0f, 6.0f)
    ), true);
    public static final Block SILVER_BLOCK = register("silver_block", new Block(
            FabricBlockSettings.create()
                    .requiresTool()
                    .strength(5.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
    ), true);
    public static final Block RAW_SILVER_BLOCK = register("raw_silver_block", new Block(
            FabricBlockSettings.create()
                    .requiresTool()
                    .strength(5.0f, 6.0f)
    ), true);

    public static <T extends Block> T register(String id, T block, boolean shouldRegisterItem) {
        Identifier blockId = new Identifier(ModConstants.MOD_ID, id);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
            Registry.register(Registries.ITEM, blockId, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockId, block);
    }

    public static void initialize() {}

    private ModBlocks() {}
}
