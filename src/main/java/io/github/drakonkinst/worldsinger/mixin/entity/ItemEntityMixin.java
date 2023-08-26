package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
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
    private static final float HEIGHT_OFFSET = 1.0f / 9.0f;

    @Unique
    private static final double HORIZONTAL_BUOYANCY_DRAG = 0.95;

    @Unique
    private static final double HORIZONTAL_LAND_DRAG = 0.7;

    @Unique
    private static final double VERTICAL_BUOYANCY_FORCE_VANILLA = 5.0E-4;

    @Unique
    private static final double LAND_BUOYANCY = VERTICAL_BUOYANCY_FORCE_VANILLA;

    @Unique
    private static final double VERTICAL_BUOYANCY_FORCE = VERTICAL_BUOYANCY_FORCE_VANILLA * 4;

    @Unique
    private static final double MAX_VERTICAL_VELOCITY_VANILLA = 0.06;

    @Unique
    private static final double MAX_VERTICAL_VELOCITY = MAX_VERTICAL_VELOCITY_VANILLA * 4;

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
        if (((SporeFluidEntityStateAccess) this).worldsinger$isInSporeSea() && this.getFluidHeight(
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
        if (!LumarSeetheManager.areSporesFluidized(this.getWorld())) {
            // Items should not move in solid spores
            this.setVelocity(this.getVelocity().getX() * HORIZONTAL_LAND_DRAG, LAND_BUOYANCY,
                    this.getVelocity().getZ() * HORIZONTAL_LAND_DRAG);
            return;
        }

        Vec3d vec3d = this.getVelocity();
        double yVelocityOffset = vec3d.y < MAX_VERTICAL_VELOCITY ? VERTICAL_BUOYANCY_FORCE : 0.0;
        this.setVelocity(vec3d.x * HORIZONTAL_BUOYANCY_DRAG, vec3d.y + yVelocityOffset,
                vec3d.z * HORIZONTAL_BUOYANCY_DRAG);
    }


}
