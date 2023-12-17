package io.github.drakonkinst.worldsinger.entity.render;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import io.github.drakonkinst.worldsinger.entity.model.MidnightCreatureEntityModel;
import io.github.drakonkinst.worldsinger.registry.ModEntityRenderers;
import io.github.drakonkinst.worldsinger.util.ColorUtil;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.util.Identifier;

public class MidnightCreatureEntityRenderer extends
        ShapeshiftingEntityRenderer<MidnightCreatureEntity, MidnightCreatureEntityModel> {

    public static final int MIDNIGHT_OVERLAY_COLOR = ColorUtil.colorToInt(0, 0, 0, 251);
    public static final int MIDNIGHT_OVERLAY_UV = OverlayTexture.packUv(0, 0);
    public static final int MIDNIGHT_OVERLAY_HURT_COLOR = ColorUtil.colorToInt(150, 0, 0, 251);
    public static final int MIDNIGHT_OVERLAY_HURT_UV = OverlayTexture.packUv(0, 1);

    public MidnightCreatureEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MidnightCreatureEntityModel(
                context.getPart(ModEntityRenderers.MODEL_MIDNIGHT_CREATURE_LAYER)), 0.5f);
    }

    // Always render fish upright
    @Override
    protected boolean shouldBeTouchingWater(MidnightCreatureEntity entity, LivingEntity morph) {
        return super.shouldBeTouchingWater(entity, morph) || morph instanceof SchoolingFishEntity;
    }

    @Override
    public Identifier getTexture(MidnightCreatureEntity entity) {
        return Worldsinger.id("textures/entity/midnight_creature/midnight_creature.png");
    }
}
