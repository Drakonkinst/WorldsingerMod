package io.github.drakonkinst.worldsinger.particle;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class MidnightEssenceParticle extends AnimatedParticle {

    private static final float RED = 0.0f;
    private static final float GREEN = 0.0f;
    private static final float BLUE = 0.0f;
    private static final float ALPHA = 0.0f;

    protected MidnightEssenceParticle(ClientWorld world, double x, double y, double z,
            double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.0f);
        this.velocityMultiplier = 0.6f;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.scale = 0.3f;
        this.setColor(RED, GREEN, BLUE);
        this.setAlpha(ALPHA);
        this.maxAge = 60 + this.random.nextInt(12);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.dead) {
            this.setSpriteForAge(this.spriteProvider);

            // Fade out to 50%
            if (this.age < this.maxAge / 2) {
                this.setAlpha(1.0F - (float) (this.age) / this.maxAge);
            }

            this.velocityY -= 0.0074F;
        }
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType,
                ClientWorld clientWorld, double d, double e, double f, double g, double h,
                double i) {
            return new MidnightEssenceParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
