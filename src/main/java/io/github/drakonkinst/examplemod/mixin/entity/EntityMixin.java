package io.github.drakonkinst.examplemod.mixin.entity;

import io.github.drakonkinst.examplemod.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.examplemod.fluid.AetherSporeFluid;
import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements SporeFluidEntityStateAccess {

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

    @Override
    public boolean examplemod$isTouchingSporeSea() {
        return !this.firstUpdate && this.fluidHeight.getDouble(ModFluidTags.AETHER_SPORES) > 0.0;
    }

    @Override
    public boolean examplemod$isInSporeSea() {
        return !this.firstUpdate && this.isSubmergedIn(ModFluidTags.AETHER_SPORES);
    }
}
