package io.github.drakonkinst.worldsinger.mixin.entity.projectile;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.worldsinger.util.math.ExtendedRaycastContext;
import io.github.drakonkinst.worldsinger.util.math.ExtendedRaycastContext.ExtendedFluidHandling;
import io.github.drakonkinst.worldsinger.world.LumarSeetheManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {

    @Shadow
    protected boolean inGround;

    @Shadow
    public abstract boolean isNoClip();

    @Shadow
    private @Nullable BlockState inBlockState;

    @Shadow
    protected abstract boolean shouldFall();

    @Shadow
    protected abstract void fall();

    public PersistentProjectileEntityMixin(
            EntityType<? extends ProjectileEntity> entityType,
            World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void collideWithSolidSpores(CallbackInfo ci) {
        if (((SporeFluidEntityStateAccess) this).worldsinger$isTouchingSporeSea()
                && !LumarSeetheManager.areSporesFluidized(this.getWorld())) {
            this.inGround = true;
        }

        // Spore sea blocks can change solidity without warning, so check if it should fall even if there is no block update
        if (this.inGround && !this.isNoClip() && this.inBlockState != null
                && this.inBlockState.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                && this.shouldFall()) {
            this.fall();
        }
    }

    @Redirect(method = "tick", at = @At(value = "NEW", target = "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/RaycastContext$ShapeType;Lnet/minecraft/world/RaycastContext$FluidHandling;Lnet/minecraft/entity/Entity;)Lnet/minecraft/world/RaycastContext;"))
    private RaycastContext injectCustomRaycastFluidHandling(Vec3d start, Vec3d end,
            ShapeType shapeType,
            FluidHandling fluidHandling, Entity entity) {
        if (LumarSeetheManager.areSporesFluidized(this.getWorld())) {
            // Default
            return new RaycastContext(start, end, shapeType, fluidHandling, entity);
        } else {
            // Collide with spore sea
            return new ExtendedRaycastContext(start, end, shapeType,
                    ExtendedFluidHandling.SPORE_SEA, entity);
        }
    }

    @Inject(method = "shouldFall", at = @At("RETURN"), cancellable = true)
    private void doNotFallIfInSolidSpores(CallbackInfoReturnable<Boolean> cir) {
        boolean isInSolidSpores = this.inBlockState != null
                && this.inBlockState.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                && this.inBlockState.getFluidState().isStill()
                && !LumarSeetheManager.areSporesFluidized(this.getWorld());
        cir.setReturnValue(cir.getReturnValue() && !isInSolidSpores);
    }


}
