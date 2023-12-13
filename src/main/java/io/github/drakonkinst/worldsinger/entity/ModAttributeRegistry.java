package io.github.drakonkinst.worldsinger.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public final class ModAttributeRegistry {

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(ModEntityTypes.MIDNIGHT_CREATURE,
                MidnightCreatureEntity.createMidnightCreatureAttributes());
    }

    private ModAttributeRegistry() {}
}
