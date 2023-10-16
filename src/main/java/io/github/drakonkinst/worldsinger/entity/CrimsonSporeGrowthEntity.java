package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.block.TallCrimsonSpinesBlock;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class CrimsonSporeGrowthEntity extends SporeGrowthEntity {

    public static final int MAX_STAGE = 2;

    private static final int FORCE_MODIFIER_MULTIPLIER = 20;
    private static final int FULL_BLOCK_COST = 15;
    private static final int SPIKE_COST = 10;
    private static final int TALL_SPINES_COST = 2;
    private static final int SPINES_COST = 1;
    private static final float MIN_ROTATION = 10.0f * MathHelper.RADIANS_PER_DEGREE;
    private static final float MAX_ROTATION = 45.0f * MathHelper.RADIANS_PER_DEGREE;
    private static final int MAX_ATTEMPTS = 10;
    private static final float PARALLEL_THRESHOLD = 0.9f;
    private static final int NEXT_STAGE_WATER_THRESHOLD = 40;
    private static final int NEXT_STAGE_SPORE_THRESHOLD = 70;
    private static final int SPLIT_SPORE_MIN = 140;
    private static final int SPLIT_WATER_MIN = 140;
    private static final int SPLIT_AGE_MAX = 1;

    // Using the given cardinal direction as a basis, rotate up to 45 degrees in yaw or pitch.
    private static Vector3f randomizeDirectionFromCardinalDirection(Int3 cardinalDirection) {
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

    private Vector3f targetGrowthDirection = null;
    private final List<Int3> directionCandidates = new ArrayList<>(3);
    private Int3 primaryDirection = null;

    public CrimsonSporeGrowthEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    private void resetTargetGrowthDirection() {
        targetGrowthDirection = null;
        directionCandidates.clear();
        primaryDirection = null;
    }

    public void setTargetGrowthDirection(Vector3f direction) {
        this.targetGrowthDirection = direction;

        float deltaX = Math.abs(targetGrowthDirection.x());
        float deltaY = Math.abs(targetGrowthDirection.y());
        float deltaZ = Math.abs(targetGrowthDirection.z());

        // Precompute direction candidates
        directionCandidates.clear();
        if (deltaX != 0) {
            int signX = (int) Math.signum(targetGrowthDirection.x());
            directionCandidates.add(new Int3(signX, 0, 0));
        }
        if (deltaY != 0) {
            int signY = (int) Math.signum(targetGrowthDirection.y());
            directionCandidates.add(new Int3(0, signY, 0));
        }
        if (deltaZ != 0) {
            int signZ = (int) Math.signum(targetGrowthDirection.z());
            directionCandidates.add(new Int3(0, 0, signZ));
        }

        if (deltaY >= deltaX && deltaY >= deltaZ) {
            primaryDirection = targetGrowthDirection.y() > 0 ? Int3.UP : Int3.DOWN;
        } else if (deltaX >= deltaY && deltaX >= deltaZ) {
            primaryDirection = targetGrowthDirection.x() > 0 ? Int3.EAST : Int3.WEST;
        } else {
            primaryDirection = targetGrowthDirection.z() > 0 ? Int3.SOUTH : Int3.NORTH;
        }
    }

    @Override
    protected BlockState getNextBlock() {
        BlockState state = null;
        int stage = sporeGrowthData.getStage();
        if (stage == 0) {
            state = ModBlocks.CRIMSON_GROWTH.getDefaultState();
        } else if (stage == 1) {
            if (lastDir.equals(primaryDirection)) {
                state = ModBlocks.CRIMSON_SPIKE.getDefaultState()
                        .with(Properties.FACING, lastDir.toDirection(Direction.UP));
            } else {
                state = ModBlocks.CRIMSON_GROWTH.getDefaultState();
            }
        } else if (stage == 2) {
            state = ModBlocks.CRIMSON_SNARE.getDefaultState();
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

    // https://math.stackexchange.com/a/3757354
    private float getDistanceSqToTargetDir(Vec3i origin, Vec3i pos) {
        int originToCandidateX = pos.getX() - origin.getX();
        int originToCandidateY = pos.getY() - origin.getY();
        int originToCandidateZ = pos.getZ() - origin.getZ();
        Vector3f originToCandidate = new Vector3f(originToCandidateX, originToCandidateY,
                originToCandidateZ);
        float av2 = originToCandidate.dot(originToCandidate);
        float avd = originToCandidate.dot(targetGrowthDirection);
        float distanceSq = av2 - avd * avd;
        return distanceSq;
    }

    @Override
    protected Int3 getNextDirection(boolean allowPassthrough) {
        this.updateTargetGrowthDirection();
        if (sporeGrowthData.getStage() == 0) {
            return this.getNextDirectionForGrowthBlock(allowPassthrough);
        } else if (sporeGrowthData.getStage() == 1) {
            return this.getNextDirectionForSpikeBlock(allowPassthrough);
        }
        return super.getNextDirection(allowPassthrough);
    }

    private void updateTargetGrowthDirection() {
        if (targetGrowthDirection != null) {
            return;
        }
        Vector3f nextDirection;
        if (!lastDir.isZero()) {
            nextDirection = CrimsonSporeGrowthEntity.randomizeDirectionFromCardinalDirection(
                    lastDir);
        } else {
            nextDirection = CrimsonSporeGrowthEntity.randomizeDirectionFromCardinalDirection(
                    Int3.UP);
        }
        this.setTargetGrowthDirection(nextDirection);
    }

    private Int3 getNextDirectionForGrowthBlock(boolean allowPassthrough) {
        BlockPos currPos = this.getBlockPos();
        float minDistanceSq = Float.MAX_VALUE;
        int minDistanceIndex = 0;
        World world = this.getWorld();
        BlockPos origin = sporeGrowthData.getOrigin();
        BlockPos.Mutable candidatePos = new BlockPos.Mutable();
        for (int i = 0; i < directionCandidates.size(); ++i) {
            Int3 direction = directionCandidates.get(i);
            candidatePos.set(currPos.getX() + direction.x(), currPos.getY() + direction.y(),
                    currPos.getZ() + direction.z());
            float distanceSq = this.getDistanceSqToTargetDir(origin, candidatePos);
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                minDistanceIndex = i;
            }
        }

        Int3 minDistanceDirection = directionCandidates.get(minDistanceIndex);
        BlockPos minDistancePos = currPos.add(minDistanceDirection.x(), minDistanceDirection.y(),
                minDistanceDirection.z());
        BlockState minDistanceState = world.getBlockState(minDistancePos);

        // Failed to find a valid direction, so just go randomly
        if (!this.canBreakOrGrow(minDistanceState, true)) {
            this.resetTargetGrowthDirection();
            return super.getNextDirection(allowPassthrough);
        }

        return directionCandidates.get(minDistanceIndex);
    }

    private Int3 getNextDirectionForSpikeBlock(boolean allowPassthrough) {
        BlockPos nextPos = this.getBlockPos()
                .add(primaryDirection.x(), primaryDirection.y(), primaryDirection.z());
        BlockState nextState = this.getWorld().getBlockState(nextPos);
        if (this.canBreakOrGrow(nextState, allowPassthrough)) {
            return primaryDirection;
        } else {
            // If entity cannot go in primary direction, turn into a splinter by advancing the stage
            sporeGrowthData.addStage(1);
            return Int3.ZERO;
        }
    }

    @Override
    protected boolean shouldRecalculateForces() {
        return targetGrowthDirection == null;
    }

    private boolean canBreakOrGrow(BlockState state, boolean allowPassthrough) {
        return this.canBreakHere(state, null) || this.canGrowHere(state, null) || (
                allowPassthrough && this.isGrowthBlock(state));
    }

    @Override
    protected int getWeight(World world, BlockPos pos, Int3 direction, boolean allowPassthrough) {
        BlockState state = world.getBlockState(pos);
        int weight = 0;

        if (this.canBreakOrGrow(state, allowPassthrough)) {
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
            return 3;
        }
        if (water == spores) {
            return 4;
        }
        return 5;
    }

    @Override
    protected void updateStage() {
        if (sporeGrowthData.getStage() == 0) {
            if (sporeGrowthData.getWater() < NEXT_STAGE_WATER_THRESHOLD) {
                sporeGrowthData.addStage(1);
            } else if (sporeGrowthData.getSpores() < NEXT_STAGE_SPORE_THRESHOLD
                    && random.nextInt(3) == 0) {
                sporeGrowthData.addStage(1);
            }
        } else if (sporeGrowthData.getStage() == 2) {
            // Add one beyond the max, effectively killing it
            sporeGrowthData.addStage(1);
        }

        // Split branches
        if (sporeGrowthData.getSpores() >= SPLIT_SPORE_MIN
                && sporeGrowthData.getWater() >= SPLIT_WATER_MIN
                && sporeGrowthData.getAge() < SPLIT_AGE_MAX) {
            this.createSplitBranch();
        }
    }

    private void createSplitBranch() {
        float proportion = 0.25f + random.nextFloat() * 0.25f;
        int numSpores = MathHelper.ceil(sporeGrowthData.getSpores() * proportion);
        int numWater = MathHelper.ceil(sporeGrowthData.getWater() * proportion);
        Vec3d spawnPos = this.getBlockPos().toCenterPos();
        SporeGrowthSpawner.spawnCrimsonSporeGrowth(this.getWorld(),
                spawnPos, numSpores, numWater, sporeGrowthData.isInitialGrowth(),
                sporeGrowthData.getStage() > 0, true, Int3.UP);
        this.drainSpores(numSpores);
        this.drainWater(numWater);
    }

    @Override
    protected int getMaxStage() {
        return MAX_STAGE;
    }

    @Override
    protected void onGrowBlock(BlockPos pos, BlockState state) {
        boolean isFullBlock = state.isOf(ModBlocks.CRIMSON_GROWTH);
        int cost = isFullBlock ? FULL_BLOCK_COST : SPIKE_COST;
        boolean drainsWater = state.getOrEmpty(ModProperties.CATALYZED).orElse(false);
        this.doGrowEffects(pos, state, cost, drainsWater, true, true);

        // Two attempts to place decorators for extra density
        // Can only place on full blocks
        if (isFullBlock) {
            this.attemptPlaceDecorators();
            this.attemptPlaceDecorators();
        }
        // this.applySporeEffectToEntities(pos);
    }

    private boolean attemptPlaceDecorators() {
        World world = this.getWorld();
        if (sporeGrowthData.getSpores() <= 0 || random.nextInt(5) == 0) {
            return false;
        }
        List<Direction> validDirections = new ArrayList<>(6);
        BlockPos pos = this.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
            mutable.set(pos.offset(direction));
            if (this.canPlaceDecorator(world.getBlockState(mutable))) {
                validDirections.add(direction);
            }
        }
        if (!validDirections.isEmpty()) {
            Direction direction = validDirections.get(random.nextInt(validDirections.size()));
            this.placeDecorator(pos.offset(direction), direction);
            return true;
        }
        return false;
    }

    private void placeDecorator(BlockPos pos, Direction direction) {
        if (direction == Direction.UP && random.nextInt(3) == 0 && this.canPlaceDecorator(
                this.getWorld().getBlockState(pos.up()))) {
            this.placeTallSpines(pos);
        } else {
            this.placeSpines(pos, direction);
        }
    }

    private void placeSpines(BlockPos pos, Direction direction) {
        boolean shouldDrainWater = this.shouldDrainWater();
        int fluidloggedIndex = Fluidlogged.getFluidIndex(
                this.getWorld().getFluidState(pos).getFluid());
        BlockState state = ModBlocks.CRIMSON_SPINES.getDefaultState()
                .with(Properties.FACING, direction)
                .with(ModProperties.CATALYZED, shouldDrainWater)
                .with(ModProperties.FLUIDLOGGED, fluidloggedIndex);
        this.placeBlockWithEffects(pos, state, SPINES_COST, shouldDrainWater,
                false, true);
    }

    private void placeTallSpines(BlockPos pos) {
        boolean shouldDrainWater = this.shouldDrainWater();
        BlockState state = ModBlocks.TALL_CRIMSON_SPINES.getDefaultState()
                .with(ModProperties.CATALYZED, shouldDrainWater);
        // No need to check fluid state since this is handled in placeAt()
        TallCrimsonSpinesBlock.placeAt(this.getWorld(), state, pos, Block.NOTIFY_ALL);
        this.doGrowEffects(pos, state, TALL_SPINES_COST, shouldDrainWater, false, false);
    }

    private boolean canPlaceDecorator(BlockState state) {
        return state.isIn(ModBlockTags.SPORES_CAN_GROW);
    }

    @Override
    protected boolean canBreakHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_BREAK) || state.isIn(
                ModBlockTags.ALL_VERDANT_VINES);
    }

    @Override
    protected boolean canGrowHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_GROW)
                || state.isIn(ModBlockTags.CRIMSON_SNARE)
                || state.isIn(ModBlockTags.TALL_CRIMSON_SPINES)
                || state.isIn(ModBlockTags.CRIMSON_SPINES)
                || (sporeGrowthData.getStage() == 0 && state.isIn(ModBlockTags.CRIMSON_SPIKE))
                || (sporeGrowthData.getStage() == 2 && state.isIn(ModBlockTags.CRIMSON_SPIKE));
    }

    @Override
    protected boolean isGrowthBlock(BlockState state) {
        return state.isIn(ModBlockTags.ALL_CRIMSON_SPINES);
    }
}
