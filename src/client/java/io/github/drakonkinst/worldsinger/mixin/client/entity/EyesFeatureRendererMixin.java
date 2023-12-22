package io.github.drakonkinst.worldsinger.mixin.client.entity;

import io.github.drakonkinst.worldsinger.entity.data.MidnightOverlayAccess;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyesFeatureRenderer.class)
public abstract class EyesFeatureRendererMixin<T extends Entity, M extends EntityModel<T>> extends
        FeatureRenderer<T, M> {

    public EyesFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelRenderIfMidnightOverlay(MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle,
            float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch, CallbackInfo ci) {

        // Midnight entities should not have glowing eyes!
        if (entity instanceof LivingEntity livingEntity) {
            if (((MidnightOverlayAccess) livingEntity).worldsinger$hasMidnightOverlay()) {
                ci.cancel();
            }
        }
    }
}
