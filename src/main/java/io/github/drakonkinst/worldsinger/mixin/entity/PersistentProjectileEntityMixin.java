package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {

    @Inject(method = "tick", at = @At("HEAD"))
    private void collideWithSolidSpores(CallbackInfo ci) {
        // Spore sea blocks can change solidity without warning, so check if it should fall even if there is no block update
        if (this.inGround && !this.isNoClip() && this.inBlockState != null
                && this.inBlockState.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                && this.shouldFall()) {
            this.fall();
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

    @Shadow
    protected boolean inGround;
    @Shadow
    private @Nullable BlockState inBlockState;

    public PersistentProjectileEntityMixin(
            EntityType<? extends ProjectileEntity> entityType,
            World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean isNoClip();

    @Shadow
    protected abstract boolean shouldFall();

    @Shadow
    protected abstract void fall();
}
