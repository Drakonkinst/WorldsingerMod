package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.mob.ShulkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShulkerEntity.class)
public interface ShulkerEntityAccessor {

    @Invoker("getPeekAmount")
    int worldsinger$getPeekAmount();

    @Invoker("setPeekAmount")
    void worldsinger$setPeekAmount(int peekAmount);

    @Invoker("tickOpenProgress")
    boolean worldsinger$tickOpenProgress();

    @Accessor("openProgress")
    float worldsinger$getOpenProgress();
}
