package io.github.drakonkinst.worldsinger.effect;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.registry.ModDamageTypes;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModStatusEffects {

    public static final StatusEffect VERDANT_SPORES = register("verdant_spores",
            new SporeStatusEffect(AetherSporeType.VERDANT, ModDamageTypes.VERDANT_SPORE));
    public static final StatusEffect CRIMSON_SPORES = register("crimson_spores",
            new SporeStatusEffect(AetherSporeType.CRIMSON, ModDamageTypes.CRIMSON_SPORE));
    public static final StatusEffect ZEPHYR_SPORES = register("zephyr_spores",
            new SporeStatusEffect(AetherSporeType.ZEPHYR, ModDamageTypes.ZEPHYR_SPORE));
    public static final StatusEffect SUNLIGHT_SPORES = register("sunlight_spores",
            new SporeStatusEffect(AetherSporeType.SUNLIGHT, ModDamageTypes.SUNLIGHT_SPORE));
    public static final StatusEffect ROSEITE_SPORES = register("roseite_spores",
            new SporeStatusEffect(AetherSporeType.ROSEITE, ModDamageTypes.ROSEITE_SPORE));
    public static final StatusEffect MIDNIGHT_SPORES = register("midnight_spores",
            new SporeStatusEffect(AetherSporeType.MIDNIGHT, ModDamageTypes.MIDNIGHT_ESSENCE));

    private ModStatusEffects() {}

    private static <T extends StatusEffect> T register(String id, T statusEffect) {
        Registry.register(Registries.STATUS_EFFECT, Worldsinger.id(id),
                statusEffect);
        return statusEffect;
    }
}
