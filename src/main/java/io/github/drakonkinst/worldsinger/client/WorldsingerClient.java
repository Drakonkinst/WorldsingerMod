package io.github.drakonkinst.worldsinger.client;

import io.github.drakonkinst.worldsinger.Constants;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.world.LumarSeetheAccess;
import io.github.drakonkinst.worldsinger.world.LumarSeetheData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WorldsingerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.VERDANT_SPORES,
                ModFluids.FLOWING_VERDANT_SPORES,
                new SimpleFluidRenderHandler(
                        new Identifier(Constants.MOD_ID, "block/verdant_spore_sea_block_still"),
                        new Identifier(Constants.MOD_ID, "block/verdant_spore_sea_block_flow")
                ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.VERDANT_SPORES, ModFluids.FLOWING_VERDANT_SPORES);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                ModBlocks.VERDANT_VINE_SNARE, ModBlocks.TWISTING_VERDANT_VINES,
                ModBlocks.TWISTING_VERDANT_VINES_PLANT);

        ClientPlayNetworking.registerGlobalReceiver(LumarSeetheData.LUMAR_SEETHE_UPDATE_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    LumarSeetheData lumarSeetheData = LumarSeetheData.fromBuf(buf);
                    client.execute(() -> {
                        ((LumarSeetheAccess) (client.world)).worldsinger$getLumarSeetheData()
                                .copy(lumarSeetheData);
                    });
                });
    }
}