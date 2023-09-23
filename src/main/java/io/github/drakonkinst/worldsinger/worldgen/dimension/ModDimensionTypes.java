package io.github.drakonkinst.worldsinger.worldgen.dimension;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.worldgen.lumar.LumarChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class ModDimensionTypes {

    public static final RegistryKey<DimensionType> LUMAR = ModDimensionTypes.of("lumar");
    public static final Identifier LUMAR_ID = Worldsinger.id("lumar");

    public static void initialize() {
        Registry.register(Registries.CHUNK_GENERATOR, Worldsinger.id("lumar"),
                LumarChunkGenerator.CODEC);
    }

    private static RegistryKey<DimensionType> of(String id) {
        return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Worldsinger.id(id));
    }
}
