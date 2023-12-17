package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.mob.RavagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RavagerEntity.class)
public interface RavagerEntityAccessor {

    @Accessor("attackTick")
    int worldsinger$getAttackTick();

    @Accessor("attackTick")
    void worldsinger$setAttackTick(int value);
}
