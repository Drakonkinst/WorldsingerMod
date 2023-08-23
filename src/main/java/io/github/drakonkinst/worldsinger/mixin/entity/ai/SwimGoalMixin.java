package io.github.drakonkinst.worldsinger.mixin.entity.ai;

import io.github.drakonkinst.worldsinger.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwimGoal.class)
public abstract class SwimGoalMixin {

    @Shadow
    @Final
    private MobEntity mob;

    @Inject(method = "canStart", at = @At("RETURN"), cancellable = true)
    private void checkForSporeFluid(CallbackInfoReturnable<Boolean> cir) {
        if (!LumarSeetheManager.areSporesFluidized(this.mob.getWorld())) {
            return;
        }
        if (((SporeFluidEntityStateAccess) this.mob).worldsinger$isTouchingSporeSea()
                && this.mob.getFluidHeight(
                ModFluidTags.AETHER_SPORES) > this.mob.getSwimHeight()) {
            cir.setReturnValue(true);
        }
    }

}
