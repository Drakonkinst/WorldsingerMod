package io.github.drakonkinst.examplemod.util.fog;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.render.FogShape;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public final class FogModifiers {

  private static final List<FogModifier> ALL_FOG_MODIFIERS = new LinkedList<>();

  public static void register(FogModifier.InjectionPoint injectionPoint, FogModifier modifier) {
    injectionPoint.register(modifier);
  }

  public static FogModifier create(Predicate<FogContext> predicate,
      Function<FogContext, Float> fogStart,
      Function<FogContext, Float> fogEnd, float densitySpeed, FogShape fogShape,
      int color) {
    return new FogModifier.Builder()
        .predicate(predicate)
        .fogStart(fogStart)
        .fogEnd(fogEnd)
        .densitySpeedTicks(densitySpeed)
        .fogShape(fogShape)
        .color(color)
        .build();
  }

  public static FogModifier create(Predicate<FogContext> predicate,
      Function<FogContext, Float> fogStart,
      Function<FogContext, Float> fogEnd, float densitySpeed, FogShape fogShape) {
    return create(predicate, fogStart, fogEnd, densitySpeed, fogShape, -1);
  }

  public static FogModifier create(Predicate<FogContext> predicate, float fogStart, float fogEnd,
      float densitySpeed, FogShape fogShape, int color) {
    return create(predicate, context -> fogStart, context -> fogEnd, densitySpeed, fogShape, color);
  }

  public static FogModifier create(Predicate<FogContext> predicate, float fogStart, float fogEnd,
      float densitySpeed, FogShape fogShape) {
    return create(predicate, context -> fogStart, context -> fogEnd, densitySpeed, fogShape, -1);
  }

  public static List<FogModifier> getAllFogModifiers() {
    return ALL_FOG_MODIFIERS;
  }

  public static boolean testBiomeEntry(FogContext fogContext,
      Predicate<RegistryEntry<Biome>> biomePredicate) {
    RegistryEntry<Biome> biomeEntry = fogContext.biomeEntry();
    if (biomeEntry != null) {
      return biomePredicate.test(biomeEntry);
    }
    return false;
  }

  public static boolean testBiomeKey(FogContext fogContext,
      Predicate<RegistryKey<Biome>> biomePredicate) {
    RegistryEntry<Biome> biomeEntry = fogContext.biomeEntry();
    if (biomeEntry != null && biomeEntry.hasKeyAndValue() && biomeEntry.hasKeyAndValue() &&
        biomeEntry.getKey().isPresent()) {
      return biomePredicate.test(biomeEntry.getKey().get());
    }
    return false;
  }
}
