package io.github.drakonkinst.worldsinger.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.entity.MidnightOverlayAccess;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends
        EntityRenderer<T> {

    protected LivingEntityRendererMixin(Context ctx) {
        super(ctx);
    }

    @WrapOperation(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void renderModelWithMidnightOverlay(M instance, MatrixStack matrices,
            VertexConsumer vertices, int light, int overlay, float red, float green, float blue,
            float alpha, Operation<Void> original, T livingEntity, float f, float g,
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        boolean hasMidnightOverlay = ((MidnightOverlayAccess) livingEntity).worldsinger$hasMidnightOverlay();
        if (hasMidnightOverlay) {
            original.call(instance, matrices, vertices, light, overlay,
                    MidnightOverlayAccess.DARKNESS_MULTIPLIER,
                    MidnightOverlayAccess.DARKNESS_MULTIPLIER,
                    MidnightOverlayAccess.DARKNESS_MULTIPLIER, alpha);
        } else {
            original.call(instance, matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}
