package io.github.drakonkinst.worldsinger.cosmere;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface WaterReactive {

    Random random = Random.create();

    boolean reactToWater(World world, BlockPos pos, int waterAmount);

    Type getReactiveType();

    enum Type {
        MISC(false),
        VERDANT_SPORES(true),
        CRIMSON_SPORES(true),
        ZEPHYR_SPORES(true),
        SUNLIGHT_SPORES(true),
        ROSEITE_SPORES(true),
        MIDNIGHT_SPORES(true);

        private final boolean shouldBeUnique;

        Type(boolean shouldBeUnique) {
            this.shouldBeUnique = shouldBeUnique;
        }

        public boolean shouldBeUnique() {
            return shouldBeUnique;
        }
    }
}
