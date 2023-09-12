package io.github.drakonkinst.worldsinger.dimension;

import io.github.drakonkinst.worldsinger.mixin.accessor.DimensionEffectsAccessor;
import io.github.drakonkinst.worldsinger.world.lumar.LumarDimensionEffects;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModDimensionEffects {

    public static void initialize() {
        Object2ObjectMap<Identifier, DimensionEffects> dimensionEffectsMap = DimensionEffectsAccessor.worldsinger$getDimensionEffectsMap();
        dimensionEffectsMap.put(ModDimensionTypes.LUMAR_ID, new LumarDimensionEffects());
    }
}
