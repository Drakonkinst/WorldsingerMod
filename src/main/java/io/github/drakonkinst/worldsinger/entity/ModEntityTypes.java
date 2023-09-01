package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntityTypes {

    // public static final EntityType<SilverLinedBoatEntity> SILVER_LINED_BOAT = register(
    //         "silver_lined_boat",
    //         FabricEntityTypeBuilder.create(SpawnGroup.MISC, SilverLinedBoatEntity::new)
    //                 .dimensions(EntityDimensions.fixed(1.375f, 0.5625f)).trackRangeChunks(10)
    //                 .build());

    public static void initialize() {
        // FabricDefaultAttributeRegistry.register(SILVER_LINED_BOAT, SilverLinedBoatEntity.)
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(ModConstants.MOD_ID, id),
                entityType);
    }

    private ModEntityTypes() {}
}
