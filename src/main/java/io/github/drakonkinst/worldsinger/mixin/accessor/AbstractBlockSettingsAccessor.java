package io.github.drakonkinst.worldsinger.mixin.accessor;

import java.util.function.ToIntFunction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.Settings.class)
public interface AbstractBlockSettingsAccessor {

    @Accessor("luminance")
    ToIntFunction<BlockState> worldsinger$getLuminance();
}
