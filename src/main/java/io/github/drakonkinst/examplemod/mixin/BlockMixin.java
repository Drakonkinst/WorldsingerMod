package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.Constants;
import io.github.drakonkinst.examplemod.Fluidlogged;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow
    protected abstract void setDefaultState(BlockState state);

    @Shadow
    public abstract BlockState getDefaultState();

    @Shadow
    protected abstract void appendProperties(StateManager.Builder<Block, BlockState> builder);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectDefaultState(AbstractBlock.Settings settings, CallbackInfo ci) {
        boolean hasWaterloggedProperty = getDefaultState().contains(Properties.WATERLOGGED);
        if (hasWaterloggedProperty != isWaterloggable()) {
            Constants.LOGGER.info("DOES NOT MATCH FOR " + ((Object) this).getClass().getName() + " - " + hasWaterloggedProperty + " vs " + isWaterloggable());
        }
        if (hasWaterloggedProperty) {
            Constants.LOGGER.info("SET DEFAULT STATE " + ((Object) this).getClass().getName() + " - " + hasWaterloggedProperty + " vs " + isWaterloggable());
            setDefaultState(getDefaultState().with(Fluidlogged.PROPERTY_FLUID, 0));
            Constants.LOGGER.info("END SET DEFAULT STATE " + ((Object) this).getClass().getName() + " - " + hasWaterloggedProperty + " vs " + isWaterloggable());
        }
    }

    // appendProperties() can be overridden by subclasses which makes it hard to inject into
    // Therefore, we catch it in the constructor instead
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;appendProperties(Lnet/minecraft/state/StateManager$Builder;)V", ordinal = 0))
    protected void injectFluidProperty(Block instance, StateManager.Builder<Block, BlockState> builder) {
        Constants.LOGGER.info("TEST PROPERTY FOR " + ((Object) this).getClass().getName());
        if (isWaterloggable()) {
            Constants.LOGGER.info("ADD PROPERTY FOR " + ((Object) this).getClass().getName());
            builder.add(Fluidlogged.PROPERTY_FLUID);
        }
        Constants.LOGGER.info("INVOKE FOR " + ((Object) this).getClass().getName());
        //((BlockAccessor) instance).examplemod$invokeAppendProperties(builder);
        this.appendProperties(builder);
        Constants.LOGGER.info("DONE INVOKE FOR " + ((Object) this).getClass().getName());
    }

    @Unique
    private boolean isWaterloggable() {
        return Waterloggable.class.isAssignableFrom(((Object) this).getClass());
    }
}
