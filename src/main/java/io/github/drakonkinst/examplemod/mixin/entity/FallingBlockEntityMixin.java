package io.github.drakonkinst.examplemod.mixin.entity;

import io.github.drakonkinst.examplemod.block.ModBlockTags;
import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    @Shadow
    private BlockState block;

    @Shadow
    public boolean dropItem;

    @Shadow
    public abstract BlockState getBlockState();

    @Shadow
    public abstract void setDestroyedOnLanding();

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void destroyIfInSporeSea(CallbackInfo ci) {
        BlockPos blockPos = this.getBlockPos();
        FluidState fluidState = this.getWorld().getFluidState(blockPos);
        if (fluidState.isIn(ModFluidTags.AETHER_SPORES) && fluidState.getLevel() >= 8
                && fluidState.isStill()) {
            // Allow spore blocks through as they will fluidize anyway
            if (this.getBlockState().isIn(ModBlockTags.AETHER_SPORE_BLOCKS)) {
                this.setDestroyedOnLanding();
                return;
            }

            this.discard();
            if (this.dropItem) {
                this.dropItem(this.block.getBlock());
            }
        }
    }
}
