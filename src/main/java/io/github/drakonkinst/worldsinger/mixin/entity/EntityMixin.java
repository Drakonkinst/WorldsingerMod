package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.util.Constants;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleManager;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Optional;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements SporeFluidEntityStateAccess {

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract boolean isPlayer();

    @Shadow
    public abstract BlockState getSteppingBlockState();

    @Shadow
    public abstract int getId();

    @Shadow
    public abstract EntityType<?> getType();

    @Inject(method = "updateWaterState", at = @At("RETURN"), cancellable = true)
    private void allowCustomFluidToPushEntity(CallbackInfoReturnable<Boolean> cir) {
        boolean isTouchingFluid = cir.getReturnValueZ();
        if (this.updateMovementInFluid(ModFluidTags.AETHER_SPORES, AetherSporeFluid.FLUID_SPEED)) {
            this.fallDistance = 0.0f;
            isTouchingFluid = true;

            this.extinguish();
        }
        cir.setReturnValue(isTouchingFluid);
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean allowCustomFluidToBeSwimmable(FluidState fluidState, TagKey<Fluid> tagKey) {
        if (fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
            return true;
        }
        return fluidState.isIn(tagKey);
    }

    @Redirect(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"))
    private BlockRenderType showSprintingParticlesForCustomFluid(BlockState instance) {
        if (!instance.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                || LumarSeetheManager.areSporesFluidized(this.getWorld())) {
            return instance.getRenderType();
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
        if (!this.getWorld().isClient() && this.getWorld() instanceof ServerWorld serverWorld) {
            BlockState steppingBlock = this.getSteppingBlockState();
            if (steppingBlock.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS) || steppingBlock.isIn(
                    ModBlockTags.AETHER_SPORE_BLOCKS)) {
                Optional<AetherSporeType> aetherSporeType = AetherSporeType.getAetherSporeTypeFromBlock(
                        steppingBlock);
                if (aetherSporeType.isEmpty()) {
                    Constants.LOGGER.error("Aether spore block should have a spore type defined");
                    return;
                }
                SporeParticleManager.spawnFootstepParticles(serverWorld, aetherSporeType.get(),
                        (Entity) (Object) this);
            }
        }
    }

    @Override
    public boolean worldsinger$isTouchingSporeSea() {
        return !this.firstUpdate && this.fluidHeight.getDouble(ModFluidTags.AETHER_SPORES) > 0.0;
    }

    @Override
    public boolean worldsinger$isInSporeSea() {
        return !this.firstUpdate && this.isSubmergedIn(ModFluidTags.AETHER_SPORES);
    }

    @Shadow
    public abstract boolean updateMovementInFluid(TagKey<Fluid> tag, double speed);

    @Shadow
    public float fallDistance;

    @Shadow
    public abstract void extinguish();

    @Shadow
    protected boolean firstUpdate;

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow
    public abstract boolean isSubmergedIn(TagKey<Fluid> fluidTag);

    @Shadow
    @Deprecated
    public abstract BlockPos getLandingPos();

    @Shadow
    public abstract World getWorld();
}
