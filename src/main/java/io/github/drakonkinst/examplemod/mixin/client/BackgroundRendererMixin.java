package io.github.drakonkinst.examplemod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.drakonkinst.examplemod.util.fog.FogContext;
import io.github.drakonkinst.examplemod.util.fog.FogModifier;
import io.github.drakonkinst.examplemod.util.math.VectorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

  @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render" +
      "/BackgroundRenderer;getFogModifier(Lnet/minecraft/entity/Entity;F)" +
      "Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;", shift = At.Shift.BY, by = 2),
      locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyPreFogDensities(Camera camera, BackgroundRenderer.FogType fogType,
      float viewDistance,
      boolean thickFog, float tickDelta, CallbackInfo ci,
      CameraSubmersionType cameraSubmersionType, Entity entity,
      BackgroundRenderer.FogData fogData,
      BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier) {
    applyModifiedFogDensity(FogModifier.InjectionPoint.PRE, entity.getWorld(), camera, fogType,
        viewDistance,
        thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Inject(method = "applyFog", at = @At(value = "CONSTANT", args = "floatValue=1f", shift = At.Shift.BY, by = 2),
      locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyLavaFogDensities(Camera camera, BackgroundRenderer.FogType fogType,
      float viewDistance,
      boolean thickFog, float tickDelta, CallbackInfo ci,
      CameraSubmersionType cameraSubmersionType, Entity entity,
      BackgroundRenderer.FogData fogData) {
    applyModifiedFogDensity(FogModifier.InjectionPoint.LAVA, entity.getWorld(), camera, fogType,
        viewDistance,
        thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Inject(method = "applyFog", at = @At(value = "CONSTANT", args = "floatValue=2f", shift = At.Shift.BY, by = 2),
      locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyPowderSnowFogDensities(Camera camera, BackgroundRenderer.FogType fogType,
      float viewDistance, boolean thickFog, float tickDelta,
      CallbackInfo ci, CameraSubmersionType cameraSubmersionType,
      Entity entity, BackgroundRenderer.FogData fogData) {
    applyModifiedFogDensity(FogModifier.InjectionPoint.POWDER_SNOW, entity.getWorld(), camera,
        fogType,
        viewDistance, thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render" +
      "/BackgroundRenderer$StatusEffectFogModifier;applyStartEndModifier" +
      "(Lnet/minecraft/client/render/BackgroundRenderer$FogData;Lnet/minecraft/entity/LivingEntity;"
      +
      "Lnet/minecraft/entity/effect/StatusEffectInstance;FF)V", shift = At.Shift.AFTER), locals =
      LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyStatusEffectFogDensities(Camera camera,
      BackgroundRenderer.FogType fogType,
      float viewDistance, boolean thickFog, float tickDelta,
      CallbackInfo ci, CameraSubmersionType cameraSubmersionType,
      Entity entity, BackgroundRenderer.FogData fogData) {
    applyModifiedFogDensity(FogModifier.InjectionPoint.STATUS_EFFECT, entity.getWorld(), camera,
        fogType,
        viewDistance, thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Inject(method = "applyFog", at = @At(value = "CONSTANT", args = "floatValue=.85f", shift = At.Shift.AFTER),
      locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyCloserWaterFogDensities(Camera camera,
      BackgroundRenderer.FogType fogType,
      float viewDistance, boolean thickFog, float tickDelta,
      CallbackInfo ci, CameraSubmersionType cameraSubmersionType,
      Entity entity, BackgroundRenderer.FogData fogData,
      BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier,
      ClientPlayerEntity clientPlayerEntity, RegistryEntry<Biome> registryEntry) {
    applyModifiedFogDensity(FogModifier.InjectionPoint.CLOSER_WATER, entity.getWorld(), camera,
        fogType,
        viewDistance, thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Inject(method = "applyFog", at = @At(value = "CONSTANT", args = "floatValue=.85f", shift = At.Shift.BY, by = 4),
      locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyWaterFogDensities(Camera camera, BackgroundRenderer.FogType fogType,
      float viewDistance,
      boolean thickFog, float tickDelta, CallbackInfo ci,
      CameraSubmersionType cameraSubmersionType, Entity entity,
      BackgroundRenderer.FogData fogData,
      BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier) {
    applyModifiedFogDensity(FogModifier.InjectionPoint.WATER, entity.getWorld(), camera, fogType,
        viewDistance,
        thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F", shift =
      At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyThickFogDensities(Camera camera, BackgroundRenderer.FogType fogType,
      float viewDistance,
      boolean thickFog, float tickDelta, CallbackInfo ci,
      CameraSubmersionType cameraSubmersionType, Entity entity,
      BackgroundRenderer.FogData fogData) {
    applyModifiedFogDensity(FogModifier.InjectionPoint.THICK_FOG, entity.getWorld(), camera,
        fogType,
        viewDistance, thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Inject(method = "applyFog", at = @At(value = "INVOKE", target =
      "Lcom/mojang/blaze3d/systems/RenderSystem;" +
          "setShaderFogStart(F)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
  private static void applyRainAndPostFogDensities(Camera camera,
      BackgroundRenderer.FogType fogType,
      float viewDistance, boolean thickFog, float tickDelta,
      CallbackInfo ci, CameraSubmersionType cameraSubmersionType,
      Entity entity, BackgroundRenderer.FogData fogData) {
    if (entity.getWorld().isThundering()) {
      applyModifiedFogDensity(FogModifier.InjectionPoint.THUNDER, entity.getWorld(), camera,
          fogType,
          viewDistance, thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
    } else if (entity.getWorld().isRaining()) {
      applyModifiedFogDensity(FogModifier.InjectionPoint.RAIN, entity.getWorld(), camera, fogType,
          viewDistance
          , thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
    }

    applyModifiedFogDensity(FogModifier.InjectionPoint.POST, entity.getWorld(), camera, fogType,
        viewDistance,
        thickFog, tickDelta, cameraSubmersionType, entity, fogData, ci);
  }

  @Unique
  private static void applyModifiedFogDensity(FogModifier.InjectionPoint injectionPoint,
      World world, Camera camera
      , BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta,
      CameraSubmersionType cameraSubmersionType, Entity entity,
      BackgroundRenderer.FogData fogData, CallbackInfo ci) {
    if (injectionPoint.getModifiers().isEmpty()) {
      return;
    }
    boolean apply = false;

    for (FogModifier modifier : injectionPoint.getModifiers()) {
      float currentFogStart = fogData.fogStart;
      float currentFogEnd = fogData.fogEnd;
      FogShape currentFogShape = fogData.fogShape;
      BackgroundRenderer.FogType currentFogType = fogData.fogType;

      RegistryEntry<Biome> biomeEntry = entity.getWorld().getBiome(entity.getBlockPos());
      if (!biomeEntry.hasKeyAndValue()) {
        biomeEntry = null;
      }

      FogContext context = new FogContext(MinecraftClient.getInstance(),
          new FogModifier.Builder()
              .predicate(context1 -> true)
              .fogEnd(currentFogEnd)
              .fogStart(currentFogStart)
              .fogShape(currentFogShape)
              .fogType(currentFogType)
              .build(),
          camera, (ClientWorld) camera.getFocusedEntity().getWorld(), camera.getFocusedEntity(),
          biomeEntry
          , viewDistance, thickFog, tickDelta);

      double densitySpeed = modifier.getDensitySpeed(context);
      if (modifier.getPredicate().test(context)) {
        if (modifier.getTime() >= densitySpeed) {
          modifier.setInterpolationState(FogModifier.DensityState.ACTIVE);
        } else {
          modifier.setInterpolationState(FogModifier.DensityState.MOVING);
        }
        apply = true;
      } else {
        modifier.setInterpolationState(FogModifier.DensityState.FROZEN);
        modifier.resetTime();
        continue;
      }

      float modifiedFogStart = -modifier.getFogStart(context);
      float modifiedFogEnd = -modifier.getFogEnd(context);
      FogShape modifiedFogShape = modifier.getFogShape(context);
      BackgroundRenderer.FogType modifiedFogType = modifier.getFogType(context);

      if (modifiedFogShape != currentFogShape) {
        fogData.fogShape = modifiedFogShape;
      }
      if (modifiedFogType != currentFogType) {
        fogData.fogType = modifiedFogType;
      }

      if (modifier.isLocational(context)) {
        assert modifier.getPos() != null;

        Vec3d currentPos = camera.getPos();
        double innerBounds = modifier.getInnerBounds();
        double outerBounds = modifier.getOuterBounds();
        Box innerBox = Box.of(modifier.getPos(), innerBounds, innerBounds, innerBounds);
        Box outerBox = Box.of(modifier.getPos(), outerBounds, outerBounds, outerBounds);

        boolean inEdge = outerBox.contains(currentPos);
        boolean inCenter = innerBox.contains(currentPos);
        if (inEdge && !inCenter) {
          innerBounds /= 2d;
          outerBounds /= 2d;
          Vec3d inner = VectorUtil.pointInDirection(modifier.getPos(), currentPos, innerBounds);
          Vec3d outer = VectorUtil.pointInDirection(modifier.getPos(), currentPos, outerBounds);

          double totalDistance = outer.distanceTo(inner);
          double distance = currentPos.distanceTo(inner);
          double depth = 1d - MathHelper.clamp(distance / totalDistance, 0d, 1d);
          modifiedFogStart *= depth;
          modifiedFogEnd *= depth;

          fogData.fogStart = modifier.apply(currentFogStart, modifiedFogStart);
          fogData.fogEnd = modifier.apply(currentFogEnd, modifiedFogEnd);
        } else if (inCenter) {
          fogData.fogStart = modifier.apply(currentFogStart, modifiedFogStart);
          fogData.fogEnd = modifier.apply(currentFogEnd, modifiedFogEnd);
        }
      } else if (modifier.isBiome(context)) {
        BiomeAccess biomeAccess = world.getBiomeAccess();
        Vec3d vec3d2 = camera.getPos().subtract(2d, 2d, 2d).multiply(.25d);

      } else {
        int time = Math.max(modifier.getTime(), 0);
        switch (modifier.getInterpolationState()) {
          case FROZEN -> {
            if (modifier.getTime() > 0) {
              modifier.resetTime();
            }
          }
          case MOVING -> {
            modifier.incrementTime();
            if (time <= densitySpeed) {
              double depth = MathHelper.clamp(time / densitySpeed, 0d, 1d);
              modifiedFogStart *= depth;
              modifiedFogEnd *= depth;

              fogData.fogStart = modifier.apply(currentFogStart, modifiedFogStart);
              fogData.fogEnd = modifier.apply(currentFogEnd, modifiedFogEnd);
            }
          }
          case ACTIVE -> {
            fogData.fogStart = modifier.apply(currentFogStart, modifiedFogStart);
            fogData.fogEnd = modifier.apply(currentFogEnd, modifiedFogEnd);
          }
        }
      }
    }

    if (apply) {
      RenderSystem.setShaderFogStart(fogData.fogStart);
      RenderSystem.setShaderFogEnd(fogData.fogEnd);
      RenderSystem.setShaderFogShape(fogData.fogShape);
      ci.cancel();
    }
  }

  private static Vec3d adjustFogColor(ClientWorld world, Camera camera, float viewDistance,
      float tickDelta,
      Vec3d prevColor) {
    float g;
    float h;
    float f;
    float r = .25f + .75f * viewDistance / 32f;
    r = 1f - (float) Math.pow(r, .25f);
    Vec3d vec3d = world.getSkyColor(camera.getPos(), tickDelta);
    float s = (float) vec3d.x;
    float t = (float) vec3d.y;
    float u = (float) vec3d.z;
    float v = MathHelper.clamp(
        MathHelper.cos(world.getSkyAngle(tickDelta) * ((float) Math.PI * 2)) * 2f + .5f, 0f, 1f);
    BiomeAccess biomeAccess = world.getBiomeAccess();
    Vec3d vec3d2 = camera.getPos().subtract(2d, 2d, 2d).multiply(.25d);
    Vec3d color =
        CubicSampler.sampleColor(vec3d2,
            (x, y, z) -> world.getDimensionEffects().adjustFogColor(
                Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(x, y, z).value().getFogColor())
                    .lerp(prevColor, tickDelta), v));
    if (viewDistance >= 4) {
      float[] fs;
      f = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0f ? -1f : 1f;
      Vector3f vector3f = new Vector3f(f, 0f, 0f);
      h = camera.getHorizontalPlane().dot(vector3f);
      if (h < 0f) {
        h = 0f;
      }
      if (h > 0f &&
          (fs = world.getDimensionEffects()
              .getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta)) !=
              null) {
        color.multiply((1f - (h *= fs[3])) + fs[0] * h, (1f - h) + fs[1] * h, (1f - h) + fs[2] * h);
      }
    }
    color.add((s - color.x) * r, (t - color.y) * r, (u - color.z) * r);
    f = world.getRainGradient(tickDelta);
    if (f > 0f) {
      float g2 = 1f - f * .5f;
      h = 1f - f * .4f;
      color.multiply(g2, g2, h);
    }
    if ((g = world.getThunderGradient(tickDelta)) > 0f) {
      h = 1f - g * .5f;
      color.multiply(h);
    }
    return color;
  }
}
