package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.entity.data.MidnightOverlayAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMidnightOverlayAccessMixin extends Entity implements
        MidnightOverlayAccess {

    @Unique
    private boolean hasMidnightOverlay = false;

    public LivingEntityMidnightOverlayAccessMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean worldsinger$hasMidnightOverlay() {
        return hasMidnightOverlay;
    }

    @Override
    public void worldsinger$setMidnightOverlay(boolean flag) {
        this.hasMidnightOverlay = flag;
    }
}
