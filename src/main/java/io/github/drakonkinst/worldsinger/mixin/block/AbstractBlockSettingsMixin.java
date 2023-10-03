package io.github.drakonkinst.worldsinger.mixin.block;

import io.github.drakonkinst.worldsinger.block.CustomBlockOffsetterAccess;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.Offsetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.Settings.class)
public class AbstractBlockSettingsMixin implements CustomBlockOffsetterAccess {

    @Shadow
    Optional<Offsetter> offsetter;

    @Override
    public void worldsinger$setCustomOffsetter(Offsetter offsetter) {
        this.offsetter = Optional.of(offsetter);
    }
}
