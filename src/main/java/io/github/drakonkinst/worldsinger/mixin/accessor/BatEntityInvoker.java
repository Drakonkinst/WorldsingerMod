package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.passive.BatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BatEntity.class)
public interface BatEntityInvoker {

    @Invoker("updateAnimations")
    void worldsinger$updateAnimations();
}
