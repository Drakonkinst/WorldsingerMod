package io.github.drakonkinst.worldsinger.client.entity.render;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.client.entity.model.MidnightCreatureEntityModel;
import io.github.drakonkinst.worldsinger.client.registry.ModEntityRenderers;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class MidnightCreatureEntityRenderer extends
        MobEntityRenderer<MidnightCreatureEntity, MidnightCreatureEntityModel> {

    public MidnightCreatureEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MidnightCreatureEntityModel(
                context.getPart(ModEntityRenderers.MODEL_MIDNIGHT_CREATURE_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(MidnightCreatureEntity entity) {
        return Worldsinger.id("textures/entity/midnight_creature/midnight_creature.png");
    }

    @Override
    public void render(MidnightCreatureEntity entity, float f, float g, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int i) {
        LivingEntity identity = entity.getIdentity();
        if (identity == null) {
            super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
            return;
        }
        EntityRenderer<? super LivingEntity> identityRenderer = MinecraftClient.getInstance()
                .getEntityRenderDispatcher()
                .getRenderer(identity);
        identityRenderer.render(identity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
