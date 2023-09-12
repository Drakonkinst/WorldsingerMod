package io.github.drakonkinst.worldsinger.worldgen;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class ModDimensionTypes {
    
    public static final RegistryKey<DimensionType> LUMAR = ModDimensionTypes.of("lumar");

    private static RegistryKey<DimensionType> of(String id) {
        return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(ModConstants.MOD_ID, id));
    }
}
