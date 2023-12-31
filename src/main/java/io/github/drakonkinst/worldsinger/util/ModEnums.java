package io.github.drakonkinst.worldsinger.util;

import com.chocohead.mm.api.ClassTinkerers;

// Used to cache extended enum values
public final class ModEnums {

    public static class PathNodeType {

        public static final net.minecraft.entity.ai.pathing.PathNodeType AETHER_SPORE_SEA = ClassTinkerers.getEnum(
                net.minecraft.entity.ai.pathing.PathNodeType.class, "AETHER_SPORE_SEA");
        public static final net.minecraft.entity.ai.pathing.PathNodeType BLOCKING_SILVER = ClassTinkerers.getEnum(
                net.minecraft.entity.ai.pathing.PathNodeType.class, "BLOCKING_SILVER");
        public static final net.minecraft.entity.ai.pathing.PathNodeType DANGER_SILVER = ClassTinkerers.getEnum(
                net.minecraft.entity.ai.pathing.PathNodeType.class, "DANGER_SILVER");
        public static final net.minecraft.entity.ai.pathing.PathNodeType DAMAGE_SILVER = ClassTinkerers.getEnum(
                net.minecraft.entity.ai.pathing.PathNodeType.class, "DAMAGE_SILVER");
    }
}
