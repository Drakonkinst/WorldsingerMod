package io.github.drakonkinst.worldsinger.entity.render;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import io.github.drakonkinst.worldsinger.entity.model.MidnightCreatureEntityModel;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import io.github.drakonkinst.worldsinger.mixin.accessor.LimbAnimatorAccessor;
import io.github.drakonkinst.worldsinger.mixin.accessor.LivingEntityAccessor;
import io.github.drakonkinst.worldsinger.registry.ModEntityRenderers;
import io.github.drakonkinst.worldsinger.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class MidnightCreatureEntityRenderer extends
        MobEntityRenderer<MidnightCreatureEntity, MidnightCreatureEntityModel> {

    public static final int MIDNIGHT_OVERLAY_COLOR = ColorUtil.colorToInt(0, 0, 0, 251);
    public static final int MIDNIGHT_OVERLAY_UV = OverlayTexture.packUv(0, 0);
    public static final int MIDNIGHT_OVERLAY_HURT_COLOR = ColorUtil.colorToInt(176, 0, 0, 251);
    public static final int MIDNIGHT_OVERLAY_HURT_UV = OverlayTexture.packUv(0, 1);

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
        ((EntityAccessor) morph).worldsinger$setTouchingWater(entity.isTouchingWater());

        // Pitch for Phantoms is inverted
        if (morph instanceof PhantomEntity) {
            morph.setPitch(-entity.getPitch());
            morph.prevPitch = -entity.prevPitch;
        } else {
            morph.setPitch(entity.getPitch());
            morph.prevPitch = entity.prevPitch;
        }

        // Equip held items and armor
        morph.equipStack(EquipmentSlot.MAINHAND, entity.getEquippedStack(EquipmentSlot.MAINHAND));
        morph.equipStack(EquipmentSlot.OFFHAND, entity.getEquippedStack(EquipmentSlot.OFFHAND));
        morph.equipStack(EquipmentSlot.HEAD, entity.getEquippedStack(EquipmentSlot.HEAD));
        morph.equipStack(EquipmentSlot.CHEST, entity.getEquippedStack(EquipmentSlot.CHEST));
        morph.equipStack(EquipmentSlot.LEGS, entity.getEquippedStack(EquipmentSlot.LEGS));
        morph.equipStack(EquipmentSlot.FEET, entity.getEquippedStack(EquipmentSlot.FEET));

        if (morph instanceof MobEntity) {
            ((MobEntity) morph).setAttacking(entity.isUsingItem());
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
        morphRenderer.render(morph, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
