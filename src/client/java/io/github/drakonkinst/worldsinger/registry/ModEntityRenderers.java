package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.model.MidnightCreatureEntityModel;
import io.github.drakonkinst.worldsinger.entity.render.MidnightCreatureEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public final class ModEntityRenderers {

    public static final EntityModelLayer MODEL_MIDNIGHT_CREATURE_LAYER = new EntityModelLayer(
            Worldsinger.id("midnight_creature"), "main");

    public static void register() {
        EntityRendererRegistry.register(ModEntityTypes.THROWN_SPORE_BOTTLE,
                FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.MIDNIGHT_CREATURE,
                MidnightCreatureEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_MIDNIGHT_CREATURE_LAYER,
                MidnightCreatureEntityModel::getTexturedModelData);
    }

    private ModEntityRenderers() {}
}