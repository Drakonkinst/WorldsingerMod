package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Unique
    private static final double GRAVITY = -0.04;

    @Unique
    private static final float HEIGHT_OFFSET = 0.11111111f;

    @Unique
    private static final float HORIZONTAL_BUOYANCY_DRAG = 0.95f;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStandingEyeHeight()F"))
    private void injectCustomFluidCheck(CallbackInfo ci) {
        double height = this.getStandingEyeHeight() - HEIGHT_OFFSET;

        // Replicate if/else branching
        if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > height) {
            return;
        }
        if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > height) {
            return;
        }
        if (((SporeFluidEntityStateAccess) this).examplemod$isInSporeSea() && this.getFluidHeight(
                ModFluidTags.AETHER_SPORES) > height) {
            this.applySporeSeaBuoyancy();

            // Counteract gravity call
            if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().subtract(0.0, GRAVITY, 0.0));
            }
        }
    }

    @Unique
    private void applySporeSeaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        double yVelocityOffset = vec3d.y < 0.06 ? 5.0E-4 : 0.0;
        this.setVelocity(vec3d.x * HORIZONTAL_BUOYANCY_DRAG, vec3d.y + yVelocityOffset,
                vec3d.z * HORIZONTAL_BUOYANCY_DRAG);
    }


}
