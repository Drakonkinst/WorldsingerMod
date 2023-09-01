package io.github.drakonkinst.worldsinger.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import io.github.drakonkinst.worldsinger.util.Constants;
import io.github.drakonkinst.worldsinger.world.lumar.SilverLiningLevel;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.BoatEntity.Type;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BoatSilverLiningFeatureRenderer extends FeatureRenderer<BoatEntity, BoatEntityModel> {

    private final Map<Type, Pair<Identifier, CompositeEntityModel<BoatEntity>>> texturesToModels;

    private static final Map<SilverLiningLevel, Identifier> BOAT_LEVEL_TO_TEXTURE = ImmutableMap.of(
            SilverLiningLevel.LOW,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/boat_silver_lining_low.png"),
            SilverLiningLevel.MEDIUM,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/boat_silver_lining_medium.png"),
            SilverLiningLevel.HIGH,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/boat_silver_lining_high.png"),
            SilverLiningLevel.PERFECT,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/boat_silver_lining_perfect.png")
    );

    private static final Map<SilverLiningLevel, Identifier> RAFT_LEVEL_TO_TEXTURE = ImmutableMap.of(
            SilverLiningLevel.LOW,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/raft_silver_lining_low.png"),
            SilverLiningLevel.MEDIUM,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/raft_silver_lining_medium.png"),
            SilverLiningLevel.HIGH,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/raft_silver_lining_high.png"),
            SilverLiningLevel.PERFECT,
            new Identifier(Constants.MOD_ID, "textures/entity/boat/raft_silver_lining_perfect.png")
    );

    protected static <T extends Entity> void renderModel(EntityModel<T> model, Identifier texture,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                RenderLayer.getEntityCutoutNoCull(texture));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f,
                1.0f);
    }

    public BoatSilverLiningFeatureRenderer(
            FeatureRendererContext<BoatEntity, BoatEntityModel> context,
            Map<Type, Pair<Identifier, CompositeEntityModel<BoatEntity>>> texturesToModels) {
        super(context);
        this.texturesToModels = texturesToModels;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            BoatEntity entity, float limbAngle, float limbDistance, float tickDelta,
            float animationProgress, float headYaw, float headPitch) {
        SilverLinedComponent silverData = ModComponents.SILVER_LINED_ENTITY.get(entity);
        float durabilityFraction =
                (float) silverData.getSilverDurability() / silverData.getMaxSilverDurability();
        SilverLiningLevel level = SilverLiningLevel.fromDurability(durabilityFraction);
        if (level == SilverLiningLevel.NONE) {
            return;
        }
        Identifier identifier;
        boolean isRaft = entity.getVariant() == Type.BAMBOO;
        if (isRaft) {
            identifier = RAFT_LEVEL_TO_TEXTURE.get(level);
        } else {
            identifier = BOAT_LEVEL_TO_TEXTURE.get(level);
        }
        BoatSilverLiningFeatureRenderer.renderModel(this.getModelForBoat(entity), identifier,
                matrices,
                vertexConsumers, light);
    }

    private EntityModel<BoatEntity> getModelForBoat(BoatEntity entity) {
        return texturesToModels.get(entity.getVariant()).getSecond();
    }
}
