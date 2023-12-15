package io.github.drakonkinst.worldsinger.mixin.client.accessor;

import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor {

    @Accessor("shadowRadius")
    float worldsinger$getShadowRadius();
}
