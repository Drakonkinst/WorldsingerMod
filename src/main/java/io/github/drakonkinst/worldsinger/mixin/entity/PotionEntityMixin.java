package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.cosmere.WaterReactionManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity {

    @Unique
    private static final int HORIZONTAL_RADIUS = 4;
    @Unique
    private static final int VERTICAL_RADIUS = 2;
    @Unique
    private static final int WATER_AMOUNT = 75;

    public PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "applyWater", at = @At("TAIL"))
    private void addSporeReaction(CallbackInfo ci) {
        WaterReactionManager.catalyzeAroundWaterEffect(this.getWorld(), this.getBlockPos(),
                HORIZONTAL_RADIUS, VERTICAL_RADIUS, WATER_AMOUNT);
    }
}
