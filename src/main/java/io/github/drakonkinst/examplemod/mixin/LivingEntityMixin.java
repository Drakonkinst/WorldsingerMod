package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private static final double SPORE_SEA_SPEED = 0.0023333333333333335;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    private void allowWalkingOnSporesDuringRain(FluidState state,
            CallbackInfoReturnable<Boolean> cir) {
        // if (state.isIn(ModFluidTags.STILL_AETHER_SPORES) && getWorld().isRaining()) {
        if (state.isIn(ModFluidTags.AETHER_SPORES) && getWorld().isRaining()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void addSporeLogic(CallbackInfo ci) {
        checkSporeSeaState();
        if (isInSporeSea()) {
            damageFromSporeSea();
        }
    }

    @Unique
    private void checkSporeSeaState() {
        // boolean check = updateMovementInFluid(ModFluidTags.AETHER_SPORES, SPORE_SEA_SPEED);

    }

    @Unique
    private void damageFromSporeSea() {
        if (this.damage(this.getDamageSources().lightningBolt(), 4.0f)) {
            // this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
        }
    }

    @Unique
    private boolean isInSporeSea() {
        // return !this.firstUpdate && this.fluidHeight.getDouble(ModFluidTags.AETHER_SPORES) > 1.0;
        return !this.firstUpdate && this.isSubmergedIn(ModFluidTags.AETHER_SPORES);
    }
}
