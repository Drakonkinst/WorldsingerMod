package io.github.drakonkinst.worldsinger.entity.render;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import io.github.drakonkinst.worldsinger.entity.model.MidnightCreatureEntityModel;
import io.github.drakonkinst.worldsinger.registry.ModEntityRenderers;
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
        LivingEntity morph = entity.getMorph();
        if (morph == null) {
            super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
            return;
        }
        EntityRenderer<? super LivingEntity> morphRenderer = MinecraftClient.getInstance()
                .getEntityRenderDispatcher()
                .getRenderer(morph);
        morphRenderer.render(morph, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
