package io.github.drakonkinst.worldsinger.util;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DimensionEffects;

// Used to cache extended enum values
public final class ModEnums {

    public static class PathNodeType {

        public static final net.minecraft.entity.ai.pathing.PathNodeType AETHER_SPORE_SEA = ClassTinkerers.getEnum(
                net.minecraft.entity.ai.pathing.PathNodeType.class, "AETHER_SPORE_SEA");
    }

    @Environment(EnvType.CLIENT)
    public static class SkyType {

        public static final DimensionEffects.SkyType LUMAR = ClassTinkerers.getEnum(
                DimensionEffects.SkyType.class, "LUMAR");
    }

    @Environment(EnvType.CLIENT)
    public static class CameraSubmersionType {

        public static final net.minecraft.client.render.CameraSubmersionType SPORE_SEA = ClassTinkerers.getEnum(
                net.minecraft.client.render.CameraSubmersionType.class, "SPORE_SEA");
    }
}
