package io.github.drakonkinst.worldsinger.mixin.block;

import com.google.common.collect.ImmutableList;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.fluid.LivingAetherSporeFluid;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.world.WaterReactionManager;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
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

    @Shadow
    @Final
    protected FlowableFluid fluid;

    @Shadow
    @Final
    public static ImmutableList<Direction> FLOW_DIRECTIONS;

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

        AetherSporeType sporeType = sporeFluid.getSporeType();

        for (Direction direction : FLOW_DIRECTIONS) {
            BlockPos neighborPos = pos.offset(direction.getOpposite());
            if (world.getFluidState(neighborPos).isIn(FluidTags.WATER)) {
                WaterReactionManager.catalyzeAroundWater(world, neighborPos);

                if (sporeType == AetherSporeType.VERDANT) {
                    world.setBlockState(neighborPos,
                            ModBlocks.VERDANT_VINE_BLOCK.getDefaultState()
                                    .with(ModProperties.CATALYZED, true));
                } else if (sporeType == AetherSporeType.CRIMSON) {
                    world.setBlockState(neighborPos,
                            ModBlocks.CRIMSON_GROWTH.getDefaultState()
                                    .with(ModProperties.CATALYZED, true));
                }

                cir.setReturnValue(false);
                return;
            }
        }
    }
}
