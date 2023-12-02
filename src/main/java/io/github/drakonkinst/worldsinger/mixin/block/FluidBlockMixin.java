package io.github.drakonkinst.worldsinger.mixin.block;

import com.google.common.collect.ImmutableList;
import io.github.drakonkinst.worldsinger.cosmere.WaterReactionManager;
import io.github.drakonkinst.worldsinger.cosmere.lumar.AetherSpores;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.fluid.LivingAetherSporeFluid;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerFluidBlocks(FlowableFluid fluid, AbstractBlock.Settings settings,
            CallbackInfo ci) {
        Fluidlogged.registerFluidBlockForFluid(fluid, (FluidBlock) (Object) this);
    }

    @Inject(method = "receiveNeighborFluids", at = @At("TAIL"), cancellable = true)
    private void addSporeFluidWaterInteraction(World world, BlockPos pos, BlockState state,
            CallbackInfoReturnable<Boolean> cir) {
        if (!(fluid instanceof LivingAetherSporeFluid sporeFluid)) {
            return;
        }

        AetherSpores sporeType = sporeFluid.getSporeType();

        for (Direction direction : FLOW_DIRECTIONS) {
            BlockPos neighborPos = pos.offset(direction.getOpposite());
            if (world.getFluidState(neighborPos).isIn(FluidTags.WATER)) {
                WaterReactionManager.catalyzeAroundWater(world, neighborPos);

                // Replace touching block with a different block if applicable, tends to help
                // with limiting spread.
                BlockState replacingState = sporeType.getFluidCollisionState();
                if (replacingState != null) {
                    world.setBlockState(neighborPos, replacingState);
                }
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Shadow
    @Final
    protected FlowableFluid fluid;

    @Shadow
    @Final
    public static ImmutableList<Direction> FLOW_DIRECTIONS;
}
