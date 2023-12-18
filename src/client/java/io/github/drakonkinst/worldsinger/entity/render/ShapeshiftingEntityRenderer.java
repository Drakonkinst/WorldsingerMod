package io.github.drakonkinst.worldsinger.entity.render;

import io.github.drakonkinst.worldsinger.entity.ShapeshiftingEntity;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import io.github.drakonkinst.worldsinger.mixin.accessor.LimbAnimatorAccessor;
import io.github.drakonkinst.worldsinger.mixin.accessor.LivingEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Hand;

public abstract class ShapeshiftingEntityRenderer<T extends ShapeshiftingEntity, M extends EntityModel<T>> extends
        MobEntityRenderer<T, M> {

    public ShapeshiftingEntityRenderer(Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    protected void configureEquipment(T entity, LivingEntity morph) {
        // Equip held items and armor
        // Note: Should not run these if using visual equipment
        morph.equipStack(EquipmentSlot.MAINHAND, entity.getEquippedStack(EquipmentSlot.MAINHAND));
        morph.equipStack(EquipmentSlot.OFFHAND, entity.getEquippedStack(EquipmentSlot.OFFHAND));
        morph.equipStack(EquipmentSlot.HEAD, entity.getEquippedStack(EquipmentSlot.HEAD));
        morph.equipStack(EquipmentSlot.CHEST, entity.getEquippedStack(EquipmentSlot.CHEST));
        morph.equipStack(EquipmentSlot.LEGS, entity.getEquippedStack(EquipmentSlot.LEGS));
        morph.equipStack(EquipmentSlot.FEET, entity.getEquippedStack(EquipmentSlot.FEET));
    }

    protected boolean shouldBeTouchingWater(T entity, LivingEntity morph) {
        return entity.isTouchingWater();
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        LivingEntity morph = entity.getMorph();
        if (morph == null) {
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            return;
        }

        // Sync limbs
        LimbAnimator target = morph.limbAnimator;
        LimbAnimator source = entity.limbAnimator;
        target.setSpeed(source.getSpeed());
        ((LimbAnimatorAccessor) target).worldsinger$setPrevSpeed(
                ((LimbAnimatorAccessor) source).worldsinger$getPrevSpeed());
        ((LimbAnimatorAccessor) target).worldsinger$setPos(source.getPos());

        // Sync data
        morph.handSwinging = entity.handSwinging;
        morph.handSwingTicks = entity.handSwingTicks;
        morph.lastHandSwingProgress = entity.lastHandSwingProgress;
        morph.handSwingProgress = entity.handSwingProgress;
        morph.bodyYaw = entity.bodyYaw;
        morph.prevBodyYaw = entity.prevBodyYaw;
        morph.headYaw = entity.headYaw;
        morph.prevHeadYaw = entity.prevHeadYaw;
        morph.age = entity.age;
        morph.preferredHand = entity.preferredHand;
        morph.deathTime = entity.deathTime;
        morph.hurtTime = entity.hurtTime;
        morph.setOnGround(entity.isOnGround());
        morph.setVelocity(entity.getVelocity());

        ((EntityAccessor) morph).worldsinger$setVehicle(entity.getVehicle());
        ((EntityAccessor) morph).worldsinger$setTouchingWater(shouldBeTouchingWater(entity, morph));

        // Pitch for Phantoms is inverted
        if (morph instanceof PhantomEntity) {
            morph.setPitch(-entity.getPitch());
            morph.prevPitch = -entity.prevPitch;
        } else {
            morph.setPitch(entity.getPitch());
            morph.prevPitch = entity.prevPitch;
        }

        configureEquipment(entity, morph);

        if (morph instanceof MobEntity) {
            ((MobEntity) morph).setAttacking(entity.isAttacking());
        }

        // Assign pose
        morph.setPose(entity.getPose());

        // Set active hand after configuring held items
        morph.setCurrentHand(
                entity.getActiveHand() == null ? Hand.MAIN_HAND : entity.getActiveHand());
        ((LivingEntityAccessor) morph).worldsinger$setLivingFlag(1, entity.isUsingItem());
        morph.getItemUseTime();
        ((LivingEntityAccessor) morph).worldsinger$tickActiveItemStack();

        // Render
        EntityRenderer<? super LivingEntity> morphRenderer = MinecraftClient.getInstance()
                .getEntityRenderDispatcher()
                .getRenderer(morph);
        morphRenderer.render(morph, yaw, tickDelta, matrices, vertexConsumers, light);

        if (entity.shouldRenderNameTag() && this.hasLabel(entity)) {
            this.renderLabelIfPresent(entity, entity.getDisplayName(), matrices, vertexConsumers,
                    light);
        }
    }
}
