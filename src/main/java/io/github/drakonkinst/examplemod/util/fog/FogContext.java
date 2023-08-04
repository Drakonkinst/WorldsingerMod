package io.github.drakonkinst.examplemod.util.fog;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public record FogContext(MinecraftClient client, FogModifier vanillaFog, Camera camera,
                         ClientWorld world, Entity focusedEntity,
                         @Nullable RegistryEntry<Biome> biomeEntry, float viewDistance,
                         boolean thickFog, float tickDelta) {

}
