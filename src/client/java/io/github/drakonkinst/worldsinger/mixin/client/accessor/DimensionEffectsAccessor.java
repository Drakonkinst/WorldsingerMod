package io.github.drakonkinst.worldsinger.mixin.client.accessor;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionEffects.class)
public interface DimensionEffectsAccessor {

    @Accessor("BY_IDENTIFIER")
    static Object2ObjectMap<Identifier, DimensionEffects> worldsinger$getDimensionEffectsMap() {
        throw new UnsupportedOperationException();
    }
}
