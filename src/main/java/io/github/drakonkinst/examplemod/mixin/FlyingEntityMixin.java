package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.examplemod.fluid.AetherSporeFluid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FlyingEntity.class, AllayEntity.class})
public abstract class FlyingEntityMixin extends MobEntity {

    protected FlyingEntityMixin(EntityType<? extends MobEntity> entityType,
            World world) {
        super(entityType, world);
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void injectCustomFluidPhysics(Vec3d movementInput, CallbackInfo ci) {
        if (!this.isLogicalSideForUpdatingMovement()) {
            return;
        }

        // Won't get too fancy here since most of these things will be dead anyways.
        if (((SporeFluidEntityStateAccess) this).examplemod$isTouchingSporeSea()) {
            this.updateVelocity(0.02f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity()
                    .multiply(AetherSporeFluid.HORIZONTAL_DRAG_MULTIPLIER,
                            AetherSporeFluid.VERTICAL_DRAG_MULTIPLIER,
                            AetherSporeFluid.HORIZONTAL_DRAG_MULTIPLIER));
            this.updateLimbs(false);
            ci.cancel();
        }
    }
}
