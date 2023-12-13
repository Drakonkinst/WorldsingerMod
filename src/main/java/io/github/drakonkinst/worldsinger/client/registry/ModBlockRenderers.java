package io.github.drakonkinst.worldsinger.client.registry;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public final class ModBlockRenderers {

    public static void register() {
        final Block[] cutoutBlocks = {
                ModBlocks.VERDANT_VINE_SNARE,
                ModBlocks.DEAD_VERDANT_VINE_SNARE,
                ModBlocks.TWISTING_VERDANT_VINES,
                ModBlocks.DEAD_TWISTING_VERDANT_VINES,
                ModBlocks.TWISTING_VERDANT_VINES_PLANT,
                ModBlocks.DEAD_TWISTING_VERDANT_VINES_PLANT,
                ModBlocks.CRIMSON_SPIKE,
                ModBlocks.DEAD_CRIMSON_SPIKE,
                ModBlocks.CRIMSON_SNARE,
                ModBlocks.DEAD_CRIMSON_SNARE,
                ModBlocks.CRIMSON_SPINES,
                ModBlocks.DEAD_CRIMSON_SPINES,
                ModBlocks.TALL_CRIMSON_SPINES,
                ModBlocks.DEAD_TALL_CRIMSON_SPINES,
                ModBlocks.ROSEITE_CLUSTER,
                ModBlocks.LARGE_ROSEITE_BUD,
                ModBlocks.MEDIUM_ROSEITE_BUD,
                ModBlocks.SMALL_ROSEITE_BUD
        };
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), cutoutBlocks);

        final Block[] translucentBlocks = {
                ModBlocks.ROSEITE_BLOCK, ModBlocks.ROSEITE_STAIRS, ModBlocks.ROSEITE_SLAB
        };
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), translucentBlocks);
    }

    private ModBlockRenderers() {}
}
