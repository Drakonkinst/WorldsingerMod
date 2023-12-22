package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.entity.MidnightAetherBondAccess;
import io.github.drakonkinst.worldsinger.entity.MidnightAetherBondData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMidnightAetherBondMixin extends LivingEntity implements
        MidnightAetherBondAccess {

    @Unique
    private final MidnightAetherBondData midnightAetherBondData = new MidnightAetherBondData(
            (PlayerEntity) (Object) this);

    protected PlayerEntityMidnightAetherBondMixin(EntityType<? extends LivingEntity> entityType,
            World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickBondData(CallbackInfo ci) {
        if (!this.getWorld().isClient()) {
            midnightAetherBondData.tick();
        }
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void breakBondsOnDeath(DamageSource damageSource, CallbackInfo ci) {
        midnightAetherBondData.onDeath();
    }

    @Override
    public MidnightAetherBondData worldsinger$getMidnightAetherBondData() {
        return midnightAetherBondData;
    }
}
