package io.github.drakonkinst.worldsinger.mixin.block;

import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Shadow
    public abstract BlockState getDefaultState();

    @Shadow
    protected abstract void setDefaultState(BlockState state);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/state/StateManager$Builder;build" +
                    "(Ljava/util/function/Function;Lnet/minecraft/state/StateManager$Factory;)" +
                    "Lnet/minecraft/state/StateManager;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectFluidProperty(AbstractBlock.Settings settings, CallbackInfo ci,
            StateManager.Builder<Block, BlockState> builder) {
        if (isWaterloggable()) {
            builder.add(ModProperties.FLUIDLOGGED);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectDefaultState(AbstractBlock.Settings settings, CallbackInfo ci) {
        if (isWaterloggable()) {
            setDefaultState(getDefaultState().with(ModProperties.FLUIDLOGGED, 0));
        }
    }

    @Unique
    private boolean isWaterloggable() {
        Block block = (Block) (Object) this;
        return block instanceof Waterloggable;
    }
}
