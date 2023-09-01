package io.github.drakonkinst.worldsinger.effect;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModStatusEffects {

    public static final StatusEffect VERDANT_SPORES = register("verdant_spores",
            new SporeStatusEffect(AetherSporeType.VERDANT));

    private ModStatusEffects() {}

    private static <T extends StatusEffect> T register(String id, T statusEffect) {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(ModConstants.MOD_ID, id),
                statusEffect);
        return statusEffect;
    }
}
