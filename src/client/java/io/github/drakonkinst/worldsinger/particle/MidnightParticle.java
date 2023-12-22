package io.github.drakonkinst.worldsinger.particle;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public abstract class MidnightParticle extends AnimatedParticle {

    private static final float RED = 0.0f;
    private static final float GREEN = 0.0f;
    private static final float BLUE = 0.0f;
    private static final float ALPHA = 0.0f;

    protected MidnightParticle(ClientWorld world, double x, double y, double z, double velocityX,
            double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.0f);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.setColor(RED, GREEN, BLUE);
        this.setAlpha(ALPHA);
        this.setSpriteForAge(spriteProvider);
    }
}
