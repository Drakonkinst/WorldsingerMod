package io.github.drakonkinst.worldsinger.mixin.client.world;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.PossessionComponent;
import io.github.drakonkinst.worldsinger.entity.CameraPossessable;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererPossessionMixin {

    // For as little latency as possible, movement commands while possessing are sent on the
    // render thread rather than only 20 times per second. This makes the client view smooth,
    // and the server can catch up.
    @Inject(method = "render", at = @At("HEAD"))
    private void sendCameraPossessableCommands(MatrixStack matrices, float tickDelta,
            long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix,
            CallbackInfo ci) {
        Entity cameraEntity = MinecraftClient.getInstance().getCameraEntity();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (cameraEntity == null || cameraEntity.isRemoved()) {
            Worldsinger.PROXY.resetRenderViewEntity();
        } else if (player != null && cameraEntity instanceof CameraPossessable cameraPossessable) {
            PossessionComponent possessionData = ModComponents.POSSESSION.get(player);
            if (possessionData.isPossessing()) {
                float headYaw = player.getHeadYaw();
                float bodyYaw = player.getBodyYaw();
                float pitch = player.getPitch();
                float forwardSpeed = player.input.movementForward;
                float sidewaysSpeed = player.input.movementSideways;
                boolean jumping = player.input.jumping;
                cameraPossessable.commandMovement(headYaw, bodyYaw, pitch, forwardSpeed,
                        sidewaysSpeed, jumping);

                // Rotation should be wrapped between [-180, 180] on server-side
                ClientPlayNetworking.send(CameraPossessable.POSSESS_UPDATE_PACKET_ID,
                        CameraPossessable.createSyncPacket(MathHelper.wrapDegrees(headYaw),
                                MathHelper.wrapDegrees(bodyYaw), MathHelper.wrapDegrees(pitch),
                                forwardSpeed, sidewaysSpeed, jumping));
            } else {
                Worldsinger.PROXY.resetRenderViewEntity();
            }
        }
    }

    // Allow the player model to still be rendered while possessing another mob
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyConstant(method = "render", constant = @Constant(classValue = ClientPlayerEntity.class))
    private static boolean allowRenderPlayerModel(Object obj, Class<?> clazz, MatrixStack matrices,
            float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera,
            GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
            Matrix4f projectionMatrix) {
        Entity cameraEntity = MinecraftClient.getInstance().getCameraEntity();
        if (cameraEntity instanceof CameraPossessable) {

            // If a Midnight Creature Entity, the entity used for rendering is not actually the
            // Midnight Creature itself. Therefore, we need to exclude the rendered entity from
            // being rendered while possessing it.
            if (cameraEntity instanceof MidnightCreatureEntity midnightCreature
                    && midnightCreature.getMorph() != null && midnightCreature.equals(obj)
                    && !camera.isThirdPerson()) {
                return true;
            }

            // Allow ClientPlayerEntity to be rendered when using a CameraPossessable
            return false;
        }
        return clazz.isAssignableFrom(obj.getClass());
    }
}
