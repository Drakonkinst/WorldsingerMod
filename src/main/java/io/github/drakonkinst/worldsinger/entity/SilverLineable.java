package io.github.drakonkinst.worldsinger.entity;

public interface SilverLineable {

    int MAX_DURABILITY = 100;
    int REPAIR_AMOUNT = 25;

    enum Level {
        NONE(0.0f),
        LOW(0.25f),
        MEDIUM(0.5f),
        HIGH(0.75f),
        PERFECT(1.0f);

        private static final Level[] VALUES;
        private final float maxDurabilityFraction;

        Level(float maxDurabilityFraction) {
            this.maxDurabilityFraction = maxDurabilityFraction;
        }

        public static Level fromDurabilityFraction(float durabilityFraction) {
            for (Level level : VALUES) {
                if (durabilityFraction <= level.maxDurabilityFraction) {
                    return level;
                }
            }
            return NONE;
        }

        public static Level fromDurability(int durability) {
            return fromDurabilityFraction((float) durability / MAX_DURABILITY);
        }

        static {
            VALUES = Level.values();
        }
    }


    int worldsinger$getSilverDurability();

    void worldsinger$setSilverDurability(int silverDurability);
}
