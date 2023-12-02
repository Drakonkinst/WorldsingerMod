package io.github.drakonkinst.worldsinger.worldgen.structure;

import java.util.Locale;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;

public final class ModStructurePieceTypes {

    public static void initialize() {}

    public static final StructurePieceType CUSTOM_MINESHAFT_CORRIDOR = ModStructurePieceTypes.register(
            CustomMineshaftGenerator.MineshaftCorridor::new, "CMSCorridor");

    public static StructurePieceType register(StructurePieceType type, String id) {
        return Registry.register(Registries.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), type);
    }

    public static final StructurePieceType CUSTOM_MINESHAFT_CROSSING = ModStructurePieceTypes.register(
            CustomMineshaftGenerator.MineshaftCrossing::new, "CMSCrossing");

    private ModStructurePieceTypes() {}

    public static final StructurePieceType CUSTOM_MINESHAFT_ROOM = ModStructurePieceTypes.register(
            CustomMineshaftGenerator.MineshaftRoom::new, "CMSRoom");
    public static final StructurePieceType CUSTOM_MINESHAFT_STAIRS = ModStructurePieceTypes.register(
            CustomMineshaftGenerator.MineshaftStairs::new, "CMSStairs");

}
