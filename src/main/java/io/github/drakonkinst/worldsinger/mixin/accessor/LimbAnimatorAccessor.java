package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimbAnimator.class)
public interface LimbAnimatorAccessor {

    @Accessor("prevSpeed")
    float worldsinger$getPrevSpeed();

    @Accessor("prevSpeed")
    void worldsinger$setPrevSpeed(float prevSpeed);

    @Accessor("pos")
    void worldsinger$setPos(float pos);
}
