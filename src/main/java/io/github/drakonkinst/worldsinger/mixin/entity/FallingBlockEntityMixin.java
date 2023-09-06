package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.block.LivingAetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Unique
    private static final float BREAKING_FALL_DISTANCE = 16.0f;

    @Inject(method = "handleFallDamage", at = @At("HEAD"))
    private void destroyAetherSporeBlockOnLanding(float fallDistance, float damageMultiplier,
            DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this.block.isIn(ModBlockTags.AETHER_SPORE_BLOCKS)
                && fallDistance >= BREAKING_FALL_DISTANCE) {
            this.destroyedOnLanding = true;
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(classValue = ConcretePowderBlock.class))
    private static boolean alsoCheckSporeBlock(Object obj, Class<? extends Object> objClass) {
        return objClass.isAssignableFrom(obj.getClass()) || obj instanceof LivingAetherSporeBlock;
    }

    @Shadow
    private boolean destroyedOnLanding;
    @Shadow
    private BlockState block;
}
