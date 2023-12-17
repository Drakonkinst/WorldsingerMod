package io.github.drakonkinst.worldsinger.mixin.client.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.entity.Shapeshifter;
import io.github.drakonkinst.worldsinger.mixin.client.accessor.EntityRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"))
    private static void adjustShadowSizeForShapeshifters(MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta,
            WorldView world, float radius, Operation<Void> original) {
        if (entity instanceof Shapeshifter shapeshifter) {
            LivingEntity morph = shapeshifter.getMorph();
            if (morph != null) {
                EntityRenderer<? super LivingEntity> morphRenderer = MinecraftClient.getInstance()
                        .getEntityRenderDispatcher()
                        .getRenderer(morph);
                float morphShadowRadius = ((EntityRendererAccessor) morphRenderer).worldsinger$getShadowRadius();
                original.call(matrices, vertexConsumers, entity, opacity, tickDelta, world,
                        morphShadowRadius);
                return;
            }
        }
        original.call(matrices, vertexConsumers, entity, opacity, tickDelta, world, radius);
    }
}
