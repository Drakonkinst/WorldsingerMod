package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.util.ModEnums;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class LumarDimensionEffects extends DimensionEffects {

    private static final float CLOUD_HEIGHT = 192.0f;

    public LumarDimensionEffects() {
        // TODO: What does alternateSkyColor do?
        super(CLOUD_HEIGHT, true, ModEnums.SkyType.LUMAR, false, false);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return null;
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }
}
