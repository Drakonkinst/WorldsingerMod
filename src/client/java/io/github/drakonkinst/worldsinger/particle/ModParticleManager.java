package io.github.drakonkinst.worldsinger.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ModParticleManager {

    public static void register() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

        registry.register(ModParticleTypes.SPORE_DUST, SporeDustParticle.Factory::new);
        registry.register(ModParticleTypes.MIDNIGHT_ESSENCE, MidnightEssenceParticle.Factory::new);
        registry.register(ModParticleTypes.MIDNIGHT_TRAIL, MidnightTrailParticle.Factory::new);
    }

    private ModParticleManager() {}
}
