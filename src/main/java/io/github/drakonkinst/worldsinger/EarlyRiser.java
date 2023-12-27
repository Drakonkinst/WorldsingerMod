package io.github.drakonkinst.worldsinger;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRiser implements Runnable {

    private static final String INTERMEDIARY = "intermediary";

    private static void loadClient(MappingResolver remapper) {
        String skyTypeEnum = remapper.mapClassName(INTERMEDIARY,
                "net.minecraft.class_5294$class_5401");
        ClassTinkerers.enumBuilder(skyTypeEnum).addEnum("LUMAR").build();

        String cameraSubmersionTypeEnum = remapper.mapClassName(INTERMEDIARY,
                "net.minecraft.class_5636");
        ClassTinkerers.enumBuilder(cameraSubmersionTypeEnum).addEnum("SPORE_SEA").build();
    }

    private static void loadServer(MappingResolver remapper) {
        // Nothing yet
    }

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String pathNodeTypeEnum = remapper.mapClassName(INTERMEDIARY, "net.minecraft.class_7");
        ClassTinkerers.enumBuilder(pathNodeTypeEnum, float.class)
                .addEnum("AETHER_SPORE_SEA", -1.0f)
                .addEnum("BLOCKING_SILVER", -1.0f)
                .addEnum("DANGER_SILVER", 0.0f) // Should be 8.0f for mobs scared of silver
                .addEnum("DAMAGE_SILVER", 0.0f) // Should be -1.0f for mobs scared of silver
                .build();

        switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> EarlyRiser.loadClient(remapper);
            case SERVER -> EarlyRiser.loadServer(remapper);
        }
    }
}
