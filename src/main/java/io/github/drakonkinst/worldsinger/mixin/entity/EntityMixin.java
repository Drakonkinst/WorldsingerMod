package io.github.drakonkinst.worldsinger.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.cosmere.lumar.AetherSpores;
import io.github.drakonkinst.worldsinger.cosmere.lumar.LumarSeethe;
import io.github.drakonkinst.worldsinger.cosmere.lumar.SporeParticleSpawner;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public float fallDistance;
    @Unique
    private boolean wasTouchingSporeSea = false;

    @Shadow
    @Deprecated
    public abstract BlockPos getLandingPos();

    @Shadow
    public abstract int getId();

    @Shadow
    public abstract EntityType<?> getType();

    @ModifyReturnValue(method = "updateWaterState", at = @At("RETURN"))
    private boolean allowCustomFluidToPushEntity(boolean isTouchingAnyFluid) {
        // All custom fluid logic should run every time this is called, no early returns.

        if (this.updateMovementInFluid(ModFluidTags.AETHER_SPORES, AetherSporeFluid.FLUID_SPEED)) {
            if (!this.wasTouchingSporeSea) {
                // Just entered spore sea
                // This needs to run server-side since particles are not just client-side.
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    Optional<AetherSpores> optionalSporeType = AetherSpores.getFirstSporeTypeFromFluid(
                            getAllTouchingFluids());
                    optionalSporeType.ifPresent(
                            sporeType -> SporeParticleSpawner.spawnSplashParticles(serverWorld,
                                    sporeType, (Entity) (Object) this, this.fallDistance, true));
                }
            }
            this.fallDistance = 0.0f;
            isTouchingAnyFluid = true;
            this.wasTouchingSporeSea = true;
            this.extinguish();
        } else {
            this.wasTouchingSporeSea = false;
        }

        // Sunlight fluid is still and does not push entities, but necessary to update
        // fluidHeight trackers and other logic
        if (this.updateMovementInFluid(ModFluidTags.SUNLIGHT, 0)) {
            this.fallDistance = 0.0f;
            isTouchingAnyFluid = true;
        }

        return isTouchingAnyFluid;
    }

    @Shadow
    public abstract boolean updateMovementInFluid(TagKey<Fluid> tag, double speed);

    @Shadow
    public abstract World getWorld();

    @Unique
    private Set<Fluid> getAllTouchingFluids() {
        return BlockPos.stream(this.getBoundingBox())
                .map(pos -> this.getWorld().getFluidState(pos).getFluid())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Shadow
    public abstract void extinguish();

    @Shadow
    public abstract Box getBoundingBox();

    @WrapOperation(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"))
    private BlockRenderType showSprintingParticlesForCustomFluid(BlockState instance,
            Operation<BlockRenderType> original) {
        World world = this.getWorld();
        if (!instance.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS) || LumarSeethe.areSporesFluidized(
                world)) {
            return original.call(instance);
        }
        return BlockRenderType.MODEL;
    }

    @Inject(method = "stepOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/event/GameEvent$Emitter;)V"))
    private void spawnParticlesOnStep(BlockPos pos, BlockState state, boolean playSound,
            boolean emitEvent, Vec3d movement, CallbackInfoReturnable<Boolean> cir) {
        // Never spawn particles when sneaking
        if (this.isSneaking()) {
            return;
        }

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            BlockState steppingBlock = this.getSteppingBlockState();
            if (steppingBlock.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS) || steppingBlock.isIn(
                    ModBlockTags.AETHER_SPORE_BLOCKS)) {
                Optional<AetherSpores> sporeType = AetherSpores.getSporeTypeFromBlock(
                        steppingBlock);
                if (sporeType.isEmpty()) {
                    Worldsinger.LOGGER.error("Aether spore block should have a spore type defined");
                    return;
                }
                SporeParticleSpawner.spawnFootstepParticles(serverWorld, sporeType.get(),
                        (Entity) (Object) this);
            }
        }
    }

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract BlockState getSteppingBlockState();
}
