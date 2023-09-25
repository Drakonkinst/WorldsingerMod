package io.github.drakonkinst.worldsinger.worldgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public final class ModStructureTypes {

    public static final StructureType<CustomMineshaftStructure> CUSTOM_MINESHAFT = ModStructureTypes.register(
            "custom_mineshaft", CustomMineshaftStructure.CODEC);

    public static void initialize() {}

    private static <S extends Structure> StructureType<S> register(String id, Codec<S> codec) {
        return Registry.register(Registries.STRUCTURE_TYPE, id, () -> codec);
    }

    private ModStructureTypes() {}
}
