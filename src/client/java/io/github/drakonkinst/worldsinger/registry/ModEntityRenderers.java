package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.render.MidnightCreatureEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public final class ModEntityRenderers {

    public static void register() {
        EntityRendererRegistry.register(ModEntityTypes.THROWN_SPORE_BOTTLE,
                FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.MIDNIGHT_CREATURE,
                MidnightCreatureEntityRenderer::new);
    }

    private ModEntityRenderers() {}
}