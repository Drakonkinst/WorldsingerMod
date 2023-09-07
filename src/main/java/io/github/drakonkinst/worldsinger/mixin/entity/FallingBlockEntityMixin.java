package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.block.LivingAetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.SteelAnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    @Shadow
    private int fallHurtMax;
    @Shadow
    private float fallHurtAmount;
    @Unique
    private static final float BREAKING_FALL_DISTANCE = 16.0f;

    public FallingBlockEntityMixin(EntityType<?> type,
            World world) {
        super(type, world);
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"))
    private void destroyAetherSporeBlockOnLanding(float fallDistance, float damageMultiplier,
            DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this.block.isIn(ModBlockTags.AETHER_SPORE_BLOCKS)
                && fallDistance >= BREAKING_FALL_DISTANCE) {
            this.destroyedOnLanding = true;
        }
    }

    @Inject(method = "handleFallDamage", at = @At("TAIL"))
    private void addSteelAnvilDurabilityDamage(float fallDistance, float damageMultiplier,
            DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        int extraFallDistance = MathHelper.ceil(fallDistance - 1.0f);
        boolean isSteelDamage = this.block.isIn(ModBlockTags.STEEL_ANVIL);
        float fallDamage = Math.min(MathHelper.floor(extraFallDistance * this.fallHurtAmount),
                this.fallHurtMax);
        // Half chance to take damage compared to regular anvil
        float chanceToTakeDamage = (0.05f + (float) extraFallDistance * 0.05f) * 0.5f;
        if (isSteelDamage && fallDamage > 0.0f && this.random.nextFloat() < chanceToTakeDamage) {
            BlockState blockState = SteelAnvilBlock.getLandingState(this.block);
            if (blockState == null) {
                this.destroyedOnLanding = true;
            } else {
                this.block = blockState;
            }
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(classValue = ConcretePowderBlock.class))
    private static boolean alsoCheckSporeBlock(Object obj, Class<? extends Object> objClass) {
        return objClass.isAssignableFrom(obj.getClass()) || obj instanceof LivingAetherSporeBlock;
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void addSteelAnvilHurtsEntities(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("HurtEntities", NbtElement.NUMBER_TYPE)) {
            return;
        }
        if (this.block.isIn(ModBlockTags.STEEL_ANVIL)) {
            this.hurtEntities = true;
        }
    }

    @Shadow
    private boolean hurtEntities;
    @Shadow
    private boolean destroyedOnLanding;
    @Shadow
    private BlockState block;
}
