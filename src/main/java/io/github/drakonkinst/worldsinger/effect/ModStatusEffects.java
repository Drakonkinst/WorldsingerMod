package io.github.drakonkinst.worldsinger.effect;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.cosmere.lumar.CrimsonSpores;
import io.github.drakonkinst.worldsinger.cosmere.lumar.MidnightSpores;
import io.github.drakonkinst.worldsinger.cosmere.lumar.RoseiteSpores;
import io.github.drakonkinst.worldsinger.cosmere.lumar.SunlightSpores;
import io.github.drakonkinst.worldsinger.cosmere.lumar.VerdantSpores;
import io.github.drakonkinst.worldsinger.cosmere.lumar.ZephyrSpores;
import io.github.drakonkinst.worldsinger.registry.ModDamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModStatusEffects {

    public static final StatusEffect VERDANT_SPORES = register("verdant_spores",
            new SporeStatusEffect(VerdantSpores.getInstance(), ModDamageTypes.VERDANT_SPORE));
    public static final StatusEffect CRIMSON_SPORES = register("crimson_spores",
            new SporeStatusEffect(CrimsonSpores.getInstance(), ModDamageTypes.CRIMSON_SPORE));
    public static final StatusEffect ZEPHYR_SPORES = register("zephyr_spores",
            new SporeStatusEffect(ZephyrSpores.getInstance(), 3.0f, ModDamageTypes.ZEPHYR_SPORE));
    public static final StatusEffect SUNLIGHT_SPORES = register("sunlight_spores",
            new SporeStatusEffect(SunlightSpores.getInstance(), ModDamageTypes.SUNLIGHT_SPORE));
    public static final StatusEffect ROSEITE_SPORES = register("roseite_spores",
            new SporeStatusEffect(RoseiteSpores.getInstance(), ModDamageTypes.ROSEITE_SPORE));
    public static final StatusEffect MIDNIGHT_SPORES = register("midnight_spores",
            new SporeStatusEffect(MidnightSpores.getInstance(), ModDamageTypes.MIDNIGHT_ESSENCE));

    private static <T extends StatusEffect> T register(String id, T statusEffect) {
        Registry.register(Registries.STATUS_EFFECT, Worldsinger.id(id), statusEffect);
        return statusEffect;
    }

    private ModStatusEffects() {}
}
