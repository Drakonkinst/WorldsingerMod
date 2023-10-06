package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public final class ModDamageTypes {

    public static final RegistryKey<DamageType> VERDANT_SPORE = ModDamageTypes.of("verdant_spore");
    public static final RegistryKey<DamageType> CRIMSON_SPORE = ModDamageTypes.of("crimson_spore");
    public static final RegistryKey<DamageType> ZEPHYR_SPORE = ModDamageTypes.of("zephyr_spore");
    public static final RegistryKey<DamageType> ROSEITE_SPORE = ModDamageTypes.of("roseite_spore");
    public static final RegistryKey<DamageType> SUNLIGHT_SPORE = ModDamageTypes.of(
            "sunlight_spore");
    public static final RegistryKey<DamageType> MIDNIGHT_ESSENCE = ModDamageTypes.of(
            "midnight_essence");
    public static final RegistryKey<DamageType> SPIKE = ModDamageTypes.of("spike");
    public static final RegistryKey<DamageType> SPIKE_FALL = ModDamageTypes.of("spike_fall");
    public static final RegistryKey<DamageType> DROWN_SPORE = ModDamageTypes.of("drown_spore");

    private static RegistryKey<DamageType> of(String id) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Worldsinger.id(id));
    }

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    private ModDamageTypes() {}
}
