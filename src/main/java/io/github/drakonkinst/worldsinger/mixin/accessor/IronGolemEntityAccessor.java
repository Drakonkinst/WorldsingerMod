package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IronGolemEntity.class)
public interface IronGolemEntityAccessor {

    @Accessor("attackTicksLeft")
    int worldsinger$getAttackTicksLeft();
    
    @Accessor("attackTicksLeft")
    void worldsinger$setAttackTicksLeft(int value);

}
