package io.github.drakonkinst.worldsinger.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDeathParticlesMixin extends Entity {

    public LivingEntityDeathParticlesMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "addDeathParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void changeMidnightCreatureDeathParticles(World instance, ParticleEffect parameters,
            double x, double y, double z, double velocityX, double velocityY, double velocityZ,
            Operation<Void> original) {
        if ((LivingEntity) (Object) this instanceof MidnightCreatureEntity) {
            original.call(instance, ModParticleTypes.MIDNIGHT_ESSENCE, x, y, z, velocityX,
                    velocityY, velocityZ);
        } else {
            original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
