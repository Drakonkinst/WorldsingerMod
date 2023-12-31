package io.github.drakonkinst.worldsinger.cosmere;

public enum SilverLiningLevel {
    NONE(0.0f), LOW(0.25f), MEDIUM(0.5f), HIGH(0.75f), PERFECT(1.0f);

    private static final SilverLiningLevel[] VALUES;

    static {
        VALUES = SilverLiningLevel.values();
    }

    public static SilverLiningLevel fromDurability(float durabilityFraction) {
        for (SilverLiningLevel level : VALUES) {
            if (durabilityFraction <= level.maxDurabilityFraction) {
                return level;
            }
        }
        return NONE;
    }

    private final float maxDurabilityFraction;

    SilverLiningLevel(float maxDurabilityFraction) {
        this.maxDurabilityFraction = maxDurabilityFraction;
    }
}
