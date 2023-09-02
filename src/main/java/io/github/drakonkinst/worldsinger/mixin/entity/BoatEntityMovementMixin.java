package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.BoatEntity.Location;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMovementMixin extends Entity {

    @Unique
    private static final double MAX_FLUID_HEIGHT_TO_NOT_EMBED = 0.05;

    @Unique
    private boolean inSporeSea;

    @Unique
    private AetherSporeFluid lastAetherSporeFluid = null;

    @Unique
    private final boolean[] firstPaddle = {true, true};

    public BoatEntityMovementMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void addParticlesToRowing(CallbackInfo ci) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld) || lastAetherSporeFluid == null) {
            return;
        }

        // Do not spawn particles on the first paddle of either oar
        for (int paddleIndex = 0; paddleIndex <= 1; ++paddleIndex) {
            if (this.isPaddleMoving(paddleIndex)) {
                if (isAtRowingApex(paddleIndex)) {
                    if (firstPaddle[paddleIndex]) {
                        firstPaddle[paddleIndex] = false;
                        continue;
                    }
                    if (this.inSporeSea) {
                        Vec3d vec3d = this.getRotationVec(1.0f);
                        double xOffset = paddleIndex == 1 ? -vec3d.z : vec3d.z;
                        double zOffset = paddleIndex == 1 ? vec3d.x : -vec3d.x;
                        Vec3d pos = new Vec3d(this.getX() + xOffset, this.getY(),
                                this.getZ() + zOffset);
                        SporeParticles.spawnRowingParticles(serverWorld,
                                lastAetherSporeFluid.getSporeType(), pos);
                    }
                }
            } else {
                firstPaddle[paddleIndex] = true;
            }
        }
    }

    @Unique
    private boolean isAtRowingApex(int paddleIndex) {
        float paddlePhase = this.paddlePhases[paddleIndex];
        return paddlePhase % (Math.PI * 2) <= Math.PI / 4
                && (paddlePhase + (Math.PI / 8)) % (Math.PI * 2) >= Math.PI / 4;
    }

    @Inject(method = "getPaddleSoundEvent", at = @At("HEAD"), cancellable = true)
    private void addSporeSeaPaddleSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (this.inSporeSea) {
            // TODO: Temp sound
            cir.setReturnValue(SoundEvents.BLOCK_SAND_BREAK);
        }
    }

    @Inject(method = "checkLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;getNearbySlipperiness()F"), cancellable = true)
    private void checkSporeSeaLocation(CallbackInfoReturnable<Location> cir) {
        this.inSporeSea = false;
        this.lastAetherSporeFluid = null;
        Location location = this.getUnderSporeSeaLocation();
        if (location != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            this.inSporeSea = true;
            cir.setReturnValue(location);
            return;
        }

        if (this.checkBoatInSporeSea()) {
            this.inSporeSea = true;
            double fluidHeight = this.getFluidHeight(ModFluidTags.AETHER_SPORES);
            World world = this.getWorld();
            if (!LumarSeethe.areSporesFluidized(world)
                    && fluidHeight <= MAX_FLUID_HEIGHT_TO_NOT_EMBED) {
                cir.setReturnValue(Location.ON_LAND);
            } else {
                cir.setReturnValue(Location.IN_WATER);
            }
        }
    }

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    private void injectSporeSeaLogic(CallbackInfo ci) {
        if (!this.inSporeSea) {
            return;
        }

        double gravity = this.hasNoGravity() ? 0.0 : -0.04;
        double f = 0.0;
        World world = this.getWorld();
        boolean isFluidized = LumarSeethe.areSporesFluidized(world);

        if (!isFluidized) {
            // Make turning velocity drop off immediately
            this.velocityDecay = 0.0f;
        }
        if (this.lastLocation == Location.IN_AIR && this.location != Location.IN_AIR
                && this.location != Location.ON_LAND) {
            this.waterLevel = this.getBodyY(1.0);
            this.setPosition(this.getX(),
                    (double) (this.getWaterHeightBelow() - this.getHeight()) + 0.101, this.getZ());
            this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            this.fallVelocity = 0.0;
            this.location = Location.IN_WATER;
            ci.cancel();
            return;
        }
        if (this.location == null || this.location == Location.IN_AIR) {
            // Let the original method handle it
            return;
        }

        if (this.location == Location.IN_WATER) {
            f = (this.waterLevel - this.getY()) / (double) this.getHeight();
            if (isFluidized) {
                this.velocityDecay = 0.9f;
            }
        } else if (this.location == Location.UNDER_FLOWING_WATER) {
            gravity = -7.0E-4;
            if (isFluidized) {
                this.velocityDecay = 0.9f;
            }
        } else if (this.location == Location.UNDER_WATER) {
            f = 0.01f;
            if (isFluidized) {
                this.velocityDecay = 0.45f;
            }
        } else if (this.location == Location.ON_LAND) {
            Vec3d vec3d = this.getVelocity();
            gravity = 0.0f;
            this.setVelocity(vec3d.getX(), Math.max(vec3d.getY(), 0.0), vec3d.getZ());
        }

        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double) this.velocityDecay, vec3d.y + gravity,
                vec3d.z * (double) this.velocityDecay);
        this.yawVelocity *= this.velocityDecay;
        if (f > 0.0) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, (vec3d2.y + f * 0.06153846016296973) * 0.75,
                    vec3d2.z);
        }
        ci.cancel();
    }

    @Inject(method = "updatePaddles", at = @At(value = "HEAD", target = "Lnet/minecraft/entity/vehicle/BoatEntity;setYaw(F)V"), cancellable = true)
    private void restrictMovementInSporeSea(CallbackInfo ci) {
        if (!this.hasPassengers()) {
            return;
        }

        if (this.inSporeSea && this.location != Location.ON_LAND) {
            World world = this.getWorld();
            if (!LumarSeethe.areSporesFluidized(world)) {
                // Skip to end of method
                this.setPaddleMovings(
                        this.pressingRight && !this.pressingLeft || this.pressingForward,
                        this.pressingLeft && !this.pressingRight || this.pressingForward);
                ci.cancel();
            }
        }
    }

    @Inject(method = "updatePassengerPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setYaw(F)V"), cancellable = true)
    private void restrictMovementInSporeSeaPassenger(CallbackInfo ci) {
        if (this.inSporeSea && this.location != Location.ON_LAND) {
            World world = this.getWorld();
            if (!LumarSeethe.areSporesFluidized(world)) {
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean checkBoatInSporeSea() {
        Box box = this.getBoundingBox();
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.ceil(box.minY + 0.001);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);
        boolean inSporeSea = false;
        this.waterLevel = -Double.MAX_VALUE;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    mutable.set(x, y, z);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (!fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
                        continue;
                    }
                    if (fluidState.getFluid() instanceof AetherSporeFluid aetherSporeFluid) {
                        if (this.lastAetherSporeFluid != null
                                && this.lastAetherSporeFluid.getSporeType() != AetherSporeType.DEAD
                                && aetherSporeFluid.getSporeType() == AetherSporeType.DEAD) {
                            // Do not allow dead spores to override living spores
                        } else {
                            this.lastAetherSporeFluid = aetherSporeFluid;
                        }
                    }
                    float f = (float) y + fluidState.getHeight(this.getWorld(), mutable);
                    this.waterLevel = Math.max(f, this.waterLevel);
                    inSporeSea |= box.minY < (double) f;
                }
            }
        }
        return inSporeSea;
    }

    @Unique
    private Location getUnderSporeSeaLocation() {
        Box box = this.getBoundingBox();
        double d = box.maxY + 0.001;
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.maxY);
        int maxY = MathHelper.ceil(d);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    mutable.set(x, y, z);
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (!fluidState.isIn(ModFluidTags.AETHER_SPORES) || !(d < (double) (
                            (float) mutable.getY() + fluidState.getHeight(this.getWorld(),
                                    mutable)))) {
                        continue;
                    }
                    if (fluidState.isStill()) {
                        bl = true;
                        continue;
                    }
                    return Location.UNDER_FLOWING_WATER;
                }
            }
        }
        return bl ? Location.UNDER_WATER : null;
    }

    // Switches to the entity-based collision shape, which can use the entity world object
    // to check fluidization and see the spore sea block as solid
    @Redirect(method = "getNearbySlipperiness", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape checkFluidizedBlock(BlockState instance, BlockView blockView,
            BlockPos blockPos) {
        return instance.getCollisionShape(blockView, blockPos, ShapeContext.of(this));
    }

    @Shadow
    public abstract float getWaterHeightBelow();

    @Shadow
    public abstract void setPaddleMovings(boolean leftMoving, boolean rightMoving);

    @Shadow
    public abstract boolean isPaddleMoving(int paddle);

    @Shadow
    private double waterLevel;
    @Shadow
    private float velocityDecay;
    @Shadow
    private Location lastLocation;
    @Shadow
    private Location location;
    @Shadow
    private double fallVelocity;
    @Shadow
    private float yawVelocity;
    @Shadow
    private boolean pressingRight;
    @Shadow
    private boolean pressingLeft;
    @Shadow
    private boolean pressingForward;
    @Shadow
    @Final
    private float[] paddlePhases;

}
