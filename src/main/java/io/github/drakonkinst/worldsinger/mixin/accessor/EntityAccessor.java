package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("FALL_FLYING_FLAG_INDEX")
    static int worldsinger$getFallFlyingIndex() {
        throw new UnsupportedOperationException();
    }

    @Accessor("firstUpdate")
    boolean worldsinger$isFirstUpdate();

    @Accessor("vehicle")
    void worldsinger$setVehicle(Entity vehicle);

    @Accessor("touchingWater")
    void worldsinger$setTouchingWater(boolean flag);

    @Invoker("setFlag")
    void worldsinger$setFlag(int index, boolean value);
}
