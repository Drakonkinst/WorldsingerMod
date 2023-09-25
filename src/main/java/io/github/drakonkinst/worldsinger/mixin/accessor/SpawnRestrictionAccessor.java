package io.github.drakonkinst.worldsinger.mixin.accessor;

import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnRestriction.Entry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpawnRestriction.class)
public interface SpawnRestrictionAccessor {

    @Accessor("RESTRICTIONS")
    static Map<EntityType<?>, Entry> worldsinger$getSpawnRestrictionsMap() {
        throw new AssertionError();
    }
}
