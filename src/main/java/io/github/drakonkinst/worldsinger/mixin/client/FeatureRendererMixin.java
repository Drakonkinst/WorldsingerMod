package io.github.drakonkinst.worldsinger.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.entity.MidnightOverlayAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(FeatureRenderer.class)
public class FeatureRendererMixin {

    @WrapOperation(method = "render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V"))
    private static <T extends LivingEntity> void renderFeaturesWithMidnightOverlay(
            EntityModel<T> model, Identifier texture, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, T entity, float red, float green,
            float blue, Operation<Void> original) {
        boolean hasMidnightOverlay = ((MidnightOverlayAccess) entity).worldsinger$hasMidnightOverlay();
        if (hasMidnightOverlay) {
            original.call(model, texture, matrices, vertexConsumers, light, entity,
                    MidnightOverlayAccess.DARKNESS_MULTIPLIER,
                    MidnightOverlayAccess.DARKNESS_MULTIPLIER,
                    MidnightOverlayAccess.DARKNESS_MULTIPLIER);
        } else {
            original.call(model, texture, matrices, vertexConsumers, light, entity, red, green,
                    blue);
        }
    }
}
