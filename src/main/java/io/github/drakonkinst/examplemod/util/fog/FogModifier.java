package io.github.drakonkinst.examplemod.util.fog;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.FogShape;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FogModifier {

  private final InjectionPoint injectionPoint;
  private final Predicate<FogContext> predicate;
  private final Function<FogContext, Float> fogStart;
  private final Function<FogContext, Float> fogEnd;
  private final Operation operation;
  private final FogShape fogShape;
  private final float densitySpeed;
  private final Function<FogContext, Integer> color;
  @Nullable
  private final RegistryKey<Biome> biomeKey;
  @Nullable
  private final Vec3d pos;
  private final double innerBounds;
  private final double outerBounds;
  private final int priority;
  private final BackgroundRenderer.FogType fogType;

  private DensityState densityState;
  private int time;

  private FogModifier(InjectionPoint injectionPoint, Predicate<FogContext> predicate,
      Function<FogContext, Float> fogStart, Function<FogContext, Float> fogEnd, Operation operation
      , FogShape fogShape, float densitySpeed, Function<FogContext, Integer> color,
      @Nullable RegistryKey<Biome> biomeKey, @Nullable Vec3d pos, double innerBounds,
      double outerBounds, BackgroundRenderer.FogType fogType, int priority,
      DensityState densityState) {
    this.injectionPoint = injectionPoint;
    this.predicate = predicate;
    this.fogStart = fogStart;
    this.fogEnd = fogEnd;
    this.operation = operation;
    this.fogShape = fogShape;
    this.densitySpeed = densitySpeed;
    this.color = color;
    this.biomeKey = biomeKey;
    this.pos = pos;
    this.innerBounds = innerBounds;
    this.outerBounds = outerBounds;
    this.fogType = fogType;
    this.priority = priority;
    this.densityState = densityState;
    time = -1;
  }

  public InjectionPoint getInjectionPoint() {
    return injectionPoint;
  }

  public int getPriority() {
    return priority;
  }

  public Predicate<FogContext> getPredicate() {
    return predicate;
  }

  public boolean test(FogContext context) {
    return isBiome(context) ?
        FogModifiers.testBiomeKey(context, biomeRegistryKey -> biomeRegistryKey.equals(biomeKey)) &&
            predicate.test(context) : predicate.test(context);
  }

  public float getDensitySpeed(FogContext context) {
    if (predicate.test(context)) {
      return densitySpeed;
    }
    return .02f;
  }

  public FogShape getFogShape(FogContext context) {
    return fogShape;
  }

  public BackgroundRenderer.FogType getFogType(FogContext context) {
    return fogType;
  }

  public int getColor(FogContext context) {
    if (predicate.test(context)) {
      return color.apply(context);
    }
    return context.vanillaFog() != null ? context.vanillaFog().color.apply(context) : -1;
  }

  public @Nullable Vec3d getPos() {
    return pos;
  }

  public double getInnerBounds() {
    return innerBounds;
  }

  public double getOuterBounds() {
    return outerBounds;
  }

  public float getFogStart(FogContext context) {
    if (predicate.test(context)) {
      return fogStart.apply(context);
    }
    return context.vanillaFog().fogStart.apply(context);
  }

  public float getFogEnd(FogContext context) {
    if (predicate.test(context)) {
      return fogEnd.apply(context);
    }
    return context.vanillaFog().fogEnd.apply(context);
  }

  public Operation getOperation() {
    return operation;
  }

  public float apply(float current, float modified) {
    return operation.apply(current, modified);
  }

  public boolean isLocational(FogContext context) {
    return pos != null && innerBounds != -1d && outerBounds != -1d;
  }

  public boolean isBiome(FogContext context) {
    return biomeKey != null;
  }

  public boolean affectsFogColor(FogContext context) {
    return color.apply(context) >= 0;
  }

  public void resetTime() {
    time = -1;
  }

  public void incrementTime() {
    ++time;
  }

  public int getTime() {
    return time;
  }

  public void setInterpolationState(DensityState densityState) {
    this.densityState = densityState;
  }

  public DensityState getInterpolationState() {
    return densityState;
  }

  public static class Builder {

    private InjectionPoint injectionPoint = InjectionPoint.POST;
    private Predicate<FogContext> predicate;
    private Function<FogContext, Float> fogStart;
    private Function<FogContext, Float> fogEnd;
    private Operation operation = Operation.OVERRIDE;
    private float densitySpeed = .01f;
    private FogShape fogShape = FogShape.SPHERE;
    private Function<FogContext, Integer> color = context -> -1;
    @Nullable
    private RegistryKey<Biome> biomeKey;
    @Nullable
    private Vec3d pos = null;
    private double innerBounds = -1d;
    private double outerBounds = -1d;
    private int priority = 1;
    private DensityState densityState = DensityState.FROZEN;
    private BackgroundRenderer.FogType fogType = BackgroundRenderer.FogType.FOG_SKY;

    public Builder injectionPoint(InjectionPoint injectionPoint) {
      this.injectionPoint = injectionPoint;
      return this;
    }

    public Builder predicate(Predicate<FogContext> predicate) {
      this.predicate = predicate;
      return this;
    }

    public Builder fogStart(Function<FogContext, Float> fogStart) {
      this.fogStart = fogStart;
      return this;
    }

    public Builder fogEnd(Function<FogContext, Float> fogEnd) {
      this.fogEnd = fogEnd;
      return this;
    }

    public Builder fogStart(float fogStart) {
      this.fogStart = context -> fogStart;
      return this;
    }

    public Builder fogEnd(float fogEnd) {
      this.fogEnd = context -> fogEnd;
      return this;
    }

    public Builder operation(Operation operation) {
      this.operation = operation;
      return this;
    }

    public Builder fogShape(FogShape fogShape) {
      this.fogShape = fogShape;
      return this;
    }

    public Builder densitySpeedTicks(float densitySpeed) {
      this.densitySpeed = densitySpeed;
      return this;
    }

    public Builder densitySpeedSeconds(int densitySpeed) {
      this.densitySpeed = densitySpeed * 20f;
      return this;
    }

    public Builder color(int color) {
      return color(context -> color);
    }

    public Builder color(Function<FogContext, Integer> color) {
      this.color = color;
      return this;
    }

    public Builder biome(RegistryKey<Biome> biomeKey) {
      this.biomeKey = biomeKey;
      return this;
    }

    public Builder pos(@Nullable Vec3d pos) {
      this.pos = pos;
      return this;
    }

    public Builder innerBounds(double innerBounds) {
      this.innerBounds = innerBounds;
      return this;
    }

    public Builder outerBounds(double outerBounds) {
      this.outerBounds = outerBounds;
      return this;
    }

    public Builder priority(int priority) {
      this.priority = priority;
      return this;
    }

    public Builder fogType(BackgroundRenderer.FogType fogType) {
      this.fogType = fogType;
      return this;
    }

    public Builder densityState(DensityState densityState) {
      this.densityState = densityState;
      return this;
    }

    public FogModifier build() {
      return new FogModifier(injectionPoint, predicate, fogStart, fogEnd, operation, fogShape,
          densitySpeed,
          color, biomeKey, pos, innerBounds, outerBounds, fogType, priority, densityState);
    }
  }

  public enum InjectionPoint {
    PRE,
    LAVA,
    POWDER_SNOW,
    STATUS_EFFECT,
    WATER,
    CLOSER_WATER,
    THICK_FOG,
    RAIN,
    THUNDER,
    POST;

    private final List<FogModifier> MODIFIERS;
    private FogModifier highestPriority;

    InjectionPoint() {
      MODIFIERS = new LinkedList<>();
    }

    public List<FogModifier> getModifiers() {
      return MODIFIERS;
    }

    public FogModifier getHighestPriority() {
      return highestPriority;
    }

    public void register(FogModifier modifier) {
      if (highestPriority == null || modifier.getPriority() > highestPriority.getPriority()) {
        highestPriority = modifier;
      }
      MODIFIERS.add(modifier);
      FogModifiers.getAllFogModifiers().add(modifier);
    }
  }

  public enum Operation {
    ADD(Float::sum),
    OVERRIDE((f1, f2) -> f2),
    MULTIPLY((f1, f2) -> f1 * f2),
    AVERAGE((f1, f2) -> (f1 + f2) / 2f),
    MIN(Math::min),
    MAX(Math::max);

    private final BiFunction<Float, Float, Float> function;

    Operation(BiFunction<Float, Float, Float> function) {
      this.function = function;
    }

    public float apply(float current, float modified) {
      return function.apply(current, modified);
    }
  }

  public enum DensityState {
    FROZEN,
    MOVING,
    ACTIVE
  }
}
