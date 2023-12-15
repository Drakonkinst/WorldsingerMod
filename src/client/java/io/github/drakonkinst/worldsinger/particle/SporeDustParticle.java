package io.github.drakonkinst.worldsinger.particle;

import net.minecraft.client.particle.AbstractDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

public class SporeDustParticle extends AbstractDustParticle<SporeDustParticleEffect> {

    protected SporeDustParticle(ClientWorld world, double x, double y, double z, double velocityX,
            double velocityY, double velocityZ, SporeDustParticleEffect parameters,
            SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
    }

    @Override
    protected float darken(float colorComponent, float multiplier) {
        // Yep. This is all to remove the random color variation
        return colorComponent * multiplier;
    }

    public static class Factory implements ParticleFactory<SporeDustParticleEffect> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SporeDustParticleEffect parameters, ClientWorld world,
                double x, double y, double z, double velocityX, double velocityY,
                double velocityZ) {
            return new SporeDustParticle(world, x, y, z, velocityX, velocityY, velocityZ,
                    parameters, this.spriteProvider);
        }
    }
}
