package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.mob.GuardianEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuardianEntity.class)
public interface GuardianEntityAccessor {

    // The method provided in the GuardianEntity class interpolates this value
    @Accessor("spikesExtension")
    float worldsinger$getSpikesExtension();

    @Accessor("spikesExtension")
    void worldsinger$setSpikesExtension(float value);

    @Accessor("prevSpikesExtension")
    void worldsinger$setPrevSpikesExtension(float value);

    // The method provided in the GuardianEntity class interpolates this value
    @Accessor("tailAngle")
    float worldsinger$getTailAngle();

    @Accessor("tailAngle")
    void worldsinger$setTailAngle(float value);

    @Accessor("prevTailAngle")
    void worldsinger$setPrevTailAngle(float value);
}
