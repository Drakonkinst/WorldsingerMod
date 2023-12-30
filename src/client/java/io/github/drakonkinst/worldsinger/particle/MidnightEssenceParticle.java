package io.github.drakonkinst.worldsinger.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class MidnightEssenceParticle extends MidnightParticle {

    protected MidnightEssenceParticle(ClientWorld world, double x, double y, double z,
            double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        this.velocityMultiplier = 0.6f;
        this.scale = 0.3f;
        this.maxAge = 100 + this.random.nextInt(20);
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
