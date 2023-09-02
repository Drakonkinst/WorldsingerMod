package io.github.drakonkinst.worldsinger.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticles;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
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
public abstract class EntityMixin implements SporeFluidEntityStateAccess {

    @ModifyReturnValue(method = "updateWaterState", at = @At("RETURN"))
    private boolean allowCustomFluidToPushEntity(boolean isTouchingAnyFluid) {
        if (this.updateMovementInFluid(ModFluidTags.AETHER_SPORES, AetherSporeFluid.FLUID_SPEED)) {
            if (!this.isTouchingSporeSea) {
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    Optional<AetherSporeType> optionalSporeType = AetherSporeType.getFirstSporeTypeFromFluid(
                            getAllTouchingFluids());
                    optionalSporeType.ifPresent(
                            sporeType -> SporeParticles.spawnSplashParticles(serverWorld, sporeType,
                                    (Entity) (Object) this, this.fallDistance, true));
                }
            }
            this.fallDistance = 0.0f;
            isTouchingAnyFluid = true;
            this.isTouchingSporeSea = true;
            this.extinguish();
        } else {
            this.isTouchingSporeSea = false;
        }
        return isTouchingAnyFluid;
    }

    @Unique
    private Set<Fluid> getAllTouchingFluids() {
        return BlockPos.stream(this.getBoundingBox())
                .map(pos -> this.getWorld().getFluidState(pos).getFluid()).collect(
                        Collectors.toUnmodifiableSet());
    }

    @WrapOperation(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"))
    private BlockRenderType showSprintingParticlesForCustomFluid(BlockState instance,
            Operation<BlockRenderType> original) {
        World world = this.getWorld();
        if (!instance.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                || LumarSeethe.areSporesFluidized(world)) {
            return original.call();
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
                Optional<AetherSporeType> aetherSporeType = AetherSporeType.getSporeTypeFromBlock(
                        steppingBlock);
                if (aetherSporeType.isEmpty()) {
                    ModConstants.LOGGER.error(
                            "Aether spore block should have a spore type defined");
                    return;
                }
                SporeParticles.spawnFootstepParticles(serverWorld, aetherSporeType.get(),
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
    public abstract void extinguish();

    @Shadow
    public abstract boolean isSubmergedIn(TagKey<Fluid> fluidTag);

    @Shadow
    @Deprecated
    public abstract BlockPos getLandingPos();

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract BlockState getSteppingBlockState();

    @Shadow
    public abstract int getId();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public float fallDistance;
    @Shadow
    protected boolean firstUpdate;
    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;
    @Unique
    private boolean isTouchingSporeSea = false;

}
