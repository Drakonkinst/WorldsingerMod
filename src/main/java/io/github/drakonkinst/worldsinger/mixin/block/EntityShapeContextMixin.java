package io.github.drakonkinst.worldsinger.mixin.block;

import io.github.drakonkinst.worldsinger.block.WorldShapeContextAccess;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityShapeContext.class)
public abstract class EntityShapeContextMixin implements WorldShapeContextAccess {

    @Shadow
    public abstract @Nullable Entity getEntity();

    @Override
    public @Nullable World worldsinger$getWorld() {
        if (this.getEntity() == null) {
            return null;
        }
        return this.getEntity().getWorld();
    }
}
