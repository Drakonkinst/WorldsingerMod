package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class CrimsonSporeGrowthEntity extends SporeGrowthEntity {

    public static final int MAX_STAGE = 1;

    private static final int FORCE_MODIFIER_MULTIPLIER = 20;
    private static final int FULL_BLOCK_COST = 10;
    private static final int SPIKE_COST = 5;
    private static final float MIN_ROTATION = 10.0f * MathHelper.RADIANS_PER_DEGREE;
    private static final float MAX_ROTATION = 45.0f * MathHelper.RADIANS_PER_DEGREE;
    private static final int MAX_ATTEMPTS = 10;
    private static final float PARALLEL_THRESHOLD = 0.9f;

    private Vector3f targetGrowthDirection = null;
    private List<Int3> directionCandidates = null;

    public CrimsonSporeGrowthEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    public void setTargetGrowthDirection(Vector3f direction) {
        this.targetGrowthDirection = direction;
        this.directionCandidates = new ArrayList<>(3);
        if (targetGrowthDirection.x() != 0) {
            directionCandidates.add(new Int3((int) Math.signum(targetGrowthDirection.x()), 0, 0));
        }
        if (targetGrowthDirection.y() != 0) {
            directionCandidates.add(new Int3(0, (int) Math.signum(targetGrowthDirection.y()), 0));
        }
        if (targetGrowthDirection.z() != 0) {
            directionCandidates.add(new Int3(0, 0, (int) Math.signum(targetGrowthDirection.z())));
        }
    }

    // Using the given cardinal direction as a basis, rotate up to 45 degrees in yaw or pitch.
    private Vector3f randomizeDirectionFromCardinalDirection(Int3 cardinalDirection) {
        Vector3f direction = new Vector3f(cardinalDirection.x(), cardinalDirection.y(),
                cardinalDirection.z());
        Vector3f randomUnitVector = generateRandomUnitVector(direction);
        Vector3f rotationAxis = randomUnitVector.cross(direction);
        int rotationSign = random.nextInt(2) * 2 - 1;
        float rotationAngle =
                rotationSign * (random.nextFloat() * (MAX_ROTATION - MIN_ROTATION) + MIN_ROTATION);
        Quaternionfc rotation = new Quaternionf(
                new AxisAngle4f(rotationAngle, rotationAxis.x(), rotationAxis.y(),
                        rotationAxis.z()));
        return direction.rotate(rotation);
    }

    // Generate a random unit vector that is not too parallel to the given vector
    private static Vector3f generateRandomUnitVector(Vector3f notParallelWith) {
        float unitX;
        float unitY;
        float unitZ;
        Vector3f vector;
        int attempts = 0;

        do {
            do {
                unitX = random.nextFloat() * 2.0f - 1.0f;
                unitY = random.nextFloat() * 2.0f - 1.0f;
                unitZ = random.nextFloat() * 2.0f - 1.0f;
            } while (unitX * unitX + unitY * unitY + unitZ * unitZ > 1.0f
                    && ++attempts < MAX_ATTEMPTS);
            vector = new Vector3f(unitX, unitY, unitZ).normalize();
        } while (Math.abs(vector.dot(notParallelWith)) >= PARALLEL_THRESHOLD
                && ++attempts < MAX_ATTEMPTS);

        if (attempts >= MAX_ATTEMPTS) {
            Worldsinger.LOGGER.error("Maxed out attempts to generate valid unit vector");
        }

        return new Vector3f(unitX, unitY, unitZ).normalize();
    }

    @Override
    protected BlockState getNextBlock() {
        BlockState state = null;
        if (sporeGrowthData.getStage() == 0) {
            state = ModBlocks.CRIMSON_GROWTH.getDefaultState();
        } else if (sporeGrowthData.getStage() == 1) {
            state = ModBlocks.CRIMSON_SPIKE.getDefaultState()
                    .with(Properties.FACING, Direction.UP);
        }

        if (state == null) {
            return null;
        }

        if (state.contains(ModProperties.FLUIDLOGGED)) {
            int fluidloggedIndex = Fluidlogged.getFluidIndex(
                    this.getWorld().getFluidState(this.getBlockPos()).getFluid());
            state = state.with(ModProperties.FLUIDLOGGED, fluidloggedIndex);
        }
        if (this.shouldDrainWater() && state.contains(ModProperties.CATALYZED)) {
            state = state.with(ModProperties.CATALYZED, true);
        }
        return state;
    }

    @Override
    protected Int3 getNextDirection(boolean allowPassthrough) {
        BlockPos pos = this.getBlockPos();
        if (targetGrowthDirection == null) {
            Vector3f nextDirection;
            if (!lastDir.isZero()) {
                nextDirection = this.randomizeDirectionFromCardinalDirection(lastDir);
            } else {
                nextDirection = this.randomizeDirectionFromCardinalDirection(Int3.UP);
            }
            this.setTargetGrowthDirection(nextDirection);
        }

        float minDistanceSq = Float.MAX_VALUE;
        int minDistanceIndex = -1;
        World world = this.getWorld();
        BlockPos origin = sporeGrowthData.getOrigin();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < directionCandidates.size(); ++i) {
            Int3 direction = directionCandidates.get(i);
            mutable.set(pos.getX() + direction.x(), pos.getY() + direction.y(),
                    pos.getZ() + direction.z());
            BlockState candidateState = world.getBlockState(mutable);
            if (!this.canMoveThrough(candidateState, true)) {
                continue;
            }

            // https://math.stackexchange.com/a/3757354
            int originToCandidateX = mutable.getX() - origin.getX();
            int originToCandidateY = mutable.getY() - origin.getY();
            int originToCandidateZ = mutable.getZ() - origin.getZ();
            Vector3f originToCandidate = new Vector3f(originToCandidateX, originToCandidateY,
                    originToCandidateZ);
            float av2 = originToCandidate.dot(originToCandidate);
            float avd = originToCandidate.dot(targetGrowthDirection);
            float distanceSq = av2 - avd * avd;

            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                minDistanceIndex = i;
            }
        }

        // Failed to find a valid direction, so just go randomly
        if (minDistanceIndex < 0) {
            Worldsinger.LOGGER.info("FAIL");
            targetGrowthDirection = null;
            return super.getNextDirection(allowPassthrough);
        }

        return directionCandidates.get(minDistanceIndex);
    }

    @Override
    protected boolean shouldRecalculateForces() {
        return targetGrowthDirection == null;
    }

    private boolean canMoveThrough(BlockState state, boolean allowPassthrough) {
        return this.canBreakHere(state, null) || this.canGrowHere(state, null) || (
                allowPassthrough && this.isGrowthBlock(state));
    }

    @Override
    protected int getWeight(World world, BlockPos pos, Int3 direction, boolean allowPassthrough) {
        BlockState state = world.getBlockState(pos);
        int weight = 0;

        if (this.canMoveThrough(state, allowPassthrough)) {
            weight = 100;
        }

        if (weight == 0) {
            return 0;
        }

        // Massive bonus for going along with external force
        double forceModifier = this.getExternalForceModifier(direction);
        weight += MathHelper.floor(FORCE_MODIFIER_MULTIPLIER * forceModifier);

        // Always have some weight, so it is an options if no other options are good
        weight = Math.max(1, weight);
        return weight;
    }

    @Override
    protected int getUpdatePeriod() {
        int water = sporeGrowthData.getWater();
        int spores = sporeGrowthData.getSpores();

        if (water > spores) {
            return 2;
        }
        if (water == spores) {
            return 3;
        }
        return 4;
    }

    @Override
    protected void updateStage() {
        // Do nothing, for now
    }

    @Override
    protected int getMaxStage() {
        return MAX_STAGE;
    }

    @Override
    protected void onGrowBlock(BlockPos pos, BlockState state) {
        int cost = state.isOf(ModBlocks.CRIMSON_GROWTH) ? FULL_BLOCK_COST : SPIKE_COST;
        boolean drainsWater = state.getOrEmpty(ModProperties.CATALYZED).orElse(false);
        this.doGrowEffects(pos, state, cost, drainsWater, true, true);
        // this.applySporeEffectToEntities(pos);
    }

    @Override
    protected boolean canBreakHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_GROW);
    }

    @Override
    protected boolean canGrowHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_BREAK);
    }

    @Override
    protected boolean isGrowthBlock(BlockState state) {
        return state.isIn(ModBlockTags.ALL_CRIMSON_SPINES);
    }
}
