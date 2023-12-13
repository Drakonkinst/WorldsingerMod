package io.github.drakonkinst.worldsinger.client.entity.model;

import com.google.common.collect.ImmutableList;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class MidnightCreatureEntityModel extends EntityModel<MidnightCreatureEntity> {

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.CUBE,
                ModelPartBuilder.create().uv(0, 0).cuboid(-6F, 12F, -6F, 12F, 12F, 12F),
                ModelTransform.pivot(0F, 0F, 0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    private final ModelPart base;

    public MidnightCreatureEntityModel(ModelPart modelPart) {
        this.base = modelPart.getChild(EntityModelPartNames.CUBE);
    }

    @Override
    public void setAngles(MidnightCreatureEntity entity, float limbAngle, float limbDistance,
            float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
            float red, float green, float blue, float alpha) {
        ImmutableList.of(this.base).forEach((modelRenderer) -> {
            modelRenderer.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        });
    }
}
