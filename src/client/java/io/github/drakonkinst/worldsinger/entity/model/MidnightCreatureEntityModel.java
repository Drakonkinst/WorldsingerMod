package io.github.drakonkinst.worldsinger.entity.model;

import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Midnight Creatures render as blocks by default, so they don't make use of this class.
public class MidnightCreatureEntityModel extends EntityModel<MidnightCreatureEntity> {

    @Override
    public void setAngles(MidnightCreatureEntity entity, float limbAngle, float limbDistance,
            float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
            float red, float green, float blue, float alpha) {

    }
}
