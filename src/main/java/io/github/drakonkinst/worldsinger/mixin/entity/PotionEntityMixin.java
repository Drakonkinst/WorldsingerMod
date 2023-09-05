package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.block.WaterReactiveBlock;
import io.github.drakonkinst.worldsinger.fluid.WaterReactiveFluid;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity {

    @Unique
    private static final int HORIZONTAL_RADIUS = 4;
    @Unique
    private static final int VERTICAL_RADIUS = 2;
    @Unique
    private static final int WATER_AMOUNT = 50;

    public PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType,
            World world) {
        super(entityType, world);
    }

    @Inject(method = "applyWater", at = @At("TAIL"))
    private void addSporeReaction(CallbackInfo ci) {
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();
        for (BlockPos currPos : BlockPos.iterateOutwards(pos, HORIZONTAL_RADIUS, VERTICAL_RADIUS,
                HORIZONTAL_RADIUS)) {
            BlockState state = world.getBlockState(currPos);
            boolean reacted = false;
            if (state.getBlock() instanceof WaterReactiveBlock waterReactiveBlock) {
                reacted = waterReactiveBlock.reactToWater(world, currPos, state, WATER_AMOUNT,
                        random);
            } else {
                FluidState fluidState = state.getFluidState();
                if (fluidState.getFluid() instanceof WaterReactiveFluid waterReactiveFluid) {
                    reacted = waterReactiveFluid.reactToWater(world, currPos, fluidState,
                            WATER_AMOUNT, random);
                }
            }

            // Only make one block react
            if (reacted) {
                return;
            }
        }
    }
}
