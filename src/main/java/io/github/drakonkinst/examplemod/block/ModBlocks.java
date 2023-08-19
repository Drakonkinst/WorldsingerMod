package io.github.drakonkinst.examplemod.block;

import io.github.drakonkinst.examplemod.Constants;
import io.github.drakonkinst.examplemod.fluid.ModFluids;
import io.github.drakonkinst.examplemod.fluid.VerdantSporeFluid;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {

    private ModBlocks() {
    }

    public static final Block DISCORD_BLOCK =
            register("discord_block", new Block(FabricBlockSettings.create().strength(4.0f)), true);
    public static final Block VERDANT_SPORE_SEA_BLOCK =
            register(
                    "verdant_spore_sea_block",
                    new AetherSporeFluidBlock(ModFluids.VERDANT_SPORES, FabricBlockSettings.create()
                            .strength(100.0f)
                            .mapColor(MapColor.DARK_GREEN)
                            .replaceable()
                            .noCollision()
                            .pistonBehavior(PistonBehavior.DESTROY)
                            .dropsNothing()
                            .liquid()
                            .sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
                    ), false);
    public static final Block VERDANT_SPORE_BLOCK =
            register("verdant_spore_block", new AetherSporeBlock(ModBlocks.VERDANT_SPORE_SEA_BLOCK,
                    VerdantSporeFluid.PARTICLE_COLOR,
                    FabricBlockSettings.create()
                            .strength(0.5f)
                            .mapColor(MapColor.DARK_GREEN)
                            .sounds(BlockSoundGroup.SAND)
            ), true);
    public static final Block VERDANT_VINE_BLOCK = register("verdant_vine_block",
            new VerdantVineBlock(
                    FabricBlockSettings.create().strength(2.0f).sounds(BlockSoundGroup.WOOD)),
            true);
    public static final Block VERDANT_VINE_BRANCH = register(
            "verdant_vine_branch",
            new VerdantVineBranchBlock(FabricBlockSettings.create().strength(0.4f).nonOpaque()),
            true);
    public static final Block VERDANT_VINE_SNARE = register("verdant_vine_snare",
            new VerdantVineSnareBlock(
                    FabricBlockSettings.create().solid().noCollision().requiresTool().strength(4.0f)
                            .pistonBehavior(PistonBehavior.DESTROY)), true);
    public static final Block TWISTING_VERDANT_VINES = register("twisting_verdant_vines",
            new TwistingVerdantVineBlockPlant(
                    FabricBlockSettings.create().strength(0.2f).noCollision()
                            .sounds(BlockSoundGroup.WEEPING_VINES)
                            .pistonBehavior(PistonBehavior.DESTROY)),
            true);
    public static final Block TWISTING_VERDANT_VINES_PLANT = register(
            "twisting_verdant_vines_plant",
            new TwistingVerdantVinesPlantBlock(
                    FabricBlockSettings.create().strength(0.2f).noCollision()
                            .sounds(BlockSoundGroup.WEEPING_VINES)
                            .pistonBehavior(PistonBehavior.DESTROY)),
            false);

    public static <T extends Block> T register(String id, T block, boolean shouldRegisterItem) {
        Identifier blockId = new Identifier(Constants.MOD_ID, id);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
            Registry.register(Registries.ITEM, blockId, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockId, block);
    }

    public static void initialize() {
    }

}
