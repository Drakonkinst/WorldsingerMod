package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntityTypes {

    public static final EntityType<VerdantSporeGrowthEntity> VERDANT_SPORE_GROWTH = register(
            "verdant_spore_growth",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, VerdantSporeGrowthEntity::new)
                    .dimensions(EntityDimensions.fixed(0.0f, 0.0f))
                    .trackRangeChunks(0)
                    .build());
    public static final EntityType<ThrownSporeBottleEntity> THROWN_SPORE_BOTTLE = register(
            "spore_bottle",
            FabricEntityTypeBuilder.<ThrownSporeBottleEntity>create(SpawnGroup.MISC,
                            ThrownSporeBottleEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(10)
                    .build());

    public static void initialize() {
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(ModConstants.MOD_ID, id),
                entityType);
    }

    private ModEntityTypes() {}
}
