package io.github.drakonkinst.worldsinger.particle;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@SuppressWarnings("UnqualifiedStaticUsage")
public class ModParticleTypes {

    public static final ParticleType<SporeDustParticleEffect> SPORE_DUST = register("spore_dust",
            true, SporeDustParticleEffect.PARAMETERS_FACTORY);

    public static void initialize() {}

    private static <T extends ParticleEffect> ParticleType<T> register(String name,
            boolean alwaysShow, ParticleEffect.Factory<T> factory) {

        return Registry.register(Registries.PARTICLE_TYPE, Worldsinger.id(name),
                FabricParticleTypes.complex(alwaysShow, factory));
    }
}
