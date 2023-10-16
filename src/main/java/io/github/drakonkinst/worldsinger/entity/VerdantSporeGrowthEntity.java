package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.block.VerdantVineBranchBlock;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VerdantSporeGrowthEntity extends SporeGrowthEntity {

    public static final int MAX_STAGE = 1;

    private static final int MAX_TWISTING_VINE_DEPTH_UP = 3;
    private static final int MAX_TWISTING_VINE_DEPTH_DOWN = 7;
    private static final int VINE_BLOCK_COST = 10;
    private static final int VINE_BRANCH_COST = 5;
    private static final int VINE_SNARE_COST = 3;
    private static final int TWISTING_VINES_COST = 1;
    private static final int SPORE_BRANCH_THRESHOLD_MIN = 50;
    private static final int SPORE_BRANCH_THRESHOLD_MAX = 100;
    private static final int SPORE_BRANCH_THICK_THRESHOLD = 300;
    private static final int SPORE_SPLIT_MIN = 100;
    private static final int WATER_SPLIT_MIN = 1;
    private static final int SPORE_WATER_THRESHOLD = 25;
    private static final int FORCE_MODIFIER_MULTIPLIER = 20;

    public VerdantSporeGrowthEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected BlockState getNextBlock() {
        BlockState state = null;
        if (sporeGrowthData.getStage() == 0) {
            state = ModBlocks.VERDANT_VINE_BLOCK.getDefaultState()
                    .with(Properties.AXIS, this.getPlacementAxis());
        } else if (sporeGrowthData.getStage() == 1) {
            VerdantVineBranchBlock block = (VerdantVineBranchBlock) ModBlocks.VERDANT_VINE_BRANCH;
            state = block.withConnectionProperties(this.getWorld(), this.getBlockPos());
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

    private Axis getPlacementAxis() {
        if (lastDir.x() != 0) {
            return Axis.X;
        }
        if (lastDir.z() != 0) {
            return Axis.Z;
        }
        return Axis.Y;
    }

    private int getDistanceFromOrigin(BlockPos pos) {
        return pos.getManhattanDistance(sporeGrowthData.getOrigin());
    }

    @Override
    protected int getWeight(World world, BlockPos pos, Int3 direction, boolean allowPassthrough) {
        BlockState state = world.getBlockState(pos);
        int weight = 0;

        // Prefer not to break through blocks
        if (this.canBreakHere(state, null)) {
            weight = 10;
        } else if (this.canGrowHere(state, null)) {
            // Can grow through lesser vines
            weight = 200;
        } else if (allowPassthrough && this.isGrowthBlock(state)) {
            // If allowPassthrough is true, we assume that no actual block will be placed
            weight = 10;
        }

        if (weight == 0) {
            return 0;
        }

        // Heavy penalty for going in the same direction to encourage curved paths
        if (direction.equals(lastDir)) {
            return 5;
        }

        // Prefer to grow upwards
        weight += 30 * direction.y();

        // Prefers to go in the same direction away from the origin
        int dirFromOriginX = Integer.signum(pos.getX() - sporeGrowthData.getOrigin().getX());
        int dirFromOriginY = Integer.signum(pos.getY() - sporeGrowthData.getOrigin().getY());
        int dirFromOriginZ = Integer.signum(pos.getZ() - sporeGrowthData.getOrigin().getZ());
        if (direction.y() == dirFromOriginX || direction.y() == dirFromOriginY
                || direction.z() == dirFromOriginZ) {
            weight += 50;
        }

        // Bonuses based on neighbors
        weight += this.getNeighborBonus(world, pos);

        // Bonus for moving away from origin
        int bonusDistanceFromOrigin =
                this.getDistanceFromOrigin(pos) - this.getDistanceFromOrigin(this.getBlockPos());
        weight += 10 * bonusDistanceFromOrigin;

        // Massive bonus for going along with external force
        double forceModifier = this.getExternalForceModifier(direction);
        weight += MathHelper.floor(FORCE_MODIFIER_MULTIPLIER * forceModifier);

        // Always have some weight, so it is an options if no other options are good
        weight = Math.max(1, weight);
        return weight;
    }

    private int getNeighborBonus(World world, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int weightBonus = 0;
        int vineNeighbors = 0;
        boolean hugsBlock = false;
        for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
            mutable.set(pos.offset(direction));
            BlockState state = world.getBlockState(mutable);
            if (state.isIn(ModBlockTags.ALL_VERDANT_VINES)) {
                // Prefer NOT to be adjacent to too many other of the same block
                if (sporeGrowthData.getStage() == 0
                        && state.isIn(ModBlockTags.VERDANT_VINE_BLOCK)
                        && sporeGrowthData.getSpores() > SPORE_BRANCH_THICK_THRESHOLD) {
                    // Allow thick branches
                    continue;
                }
                vineNeighbors++;
            } else if (state.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)) {
                // Prefer to move away from the spore sea
                weightBonus -= 20;
            } else if (state.isSideSolidFullSquare(world, mutable, direction.getOpposite())) {
                // Prefer to wrap around blocks
                hugsBlock = true;
            }
        }

        if (vineNeighbors > 1) {
            weightBonus -= 150 * (vineNeighbors - 1);
        }
        if (hugsBlock) {
            weightBonus += 100;
        }
        return weightBonus;
    }

    @Override
    protected void updateStage() {
        if (sporeGrowthData.getStage() == 0) {
            // Advance stage if low on water
            if (sporeGrowthData.getWater() <= SPORE_WATER_THRESHOLD) {
                sporeGrowthData.addStage(1);
            } else if (sporeGrowthData.getSpores() <= SPORE_BRANCH_THRESHOLD_MIN
                    || (sporeGrowthData.getSpores() <= SPORE_BRANCH_THRESHOLD_MAX
                    && random.nextInt(5) == 0)) {
                // Chance to advance stage if low on spores
                sporeGrowthData.addStage(1);
            }
        }

        if (sporeGrowthData.getSpores() >= SPORE_SPLIT_MIN
                && sporeGrowthData.getWater() >= WATER_SPLIT_MIN && random.nextInt(10) == 0) {
            this.createSplitBranch();
        }
    }

    private void createSplitBranch() {
        float proportion = 0.25f + random.nextFloat() * 0.25f;
        int numSpores = MathHelper.ceil(sporeGrowthData.getSpores() * proportion);
        int numWater = MathHelper.ceil(sporeGrowthData.getWater() * proportion);
        Vec3d spawnPos = this.getBlockPos().toCenterPos();
        SporeGrowthSpawner.spawnVerdantSporeGrowth(this.getWorld(),
                spawnPos, numSpores, numWater,
                sporeGrowthData.isInitialGrowth(), sporeGrowthData.getStage() > 0, true);
        this.drainSpores(numSpores);
        this.drainWater(numWater);
    }

    @Override
    protected boolean canBreakHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_BREAK);
    }

    @Override
    protected boolean canGrowHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_GROW)
                || state.isIn(ModBlockTags.VERDANT_VINE_SNARE)
                || state.isIn(ModBlockTags.TWISTING_VERDANT_VINES)
                || (state.isIn(ModBlockTags.VERDANT_VINE_BRANCH)
                && sporeGrowthData.getStage() == 0);
    }

    @Override
    protected boolean isGrowthBlock(BlockState state) {
        return state.isIn(ModBlockTags.ALL_VERDANT_VINES);
    }

    @Override
    protected void onGrowBlock(BlockPos pos, BlockState state) {
        int cost = state.isOf(ModBlocks.VERDANT_VINE_BLOCK) ? VINE_BLOCK_COST : VINE_BRANCH_COST;
        boolean drainsWater = state.getOrEmpty(ModProperties.CATALYZED).orElse(false);
        this.doGrowEffects(pos, state, cost, drainsWater, true, true);
        this.attemptPlaceDecorators();
        this.applySporeEffectToEntities(pos);
    }

    private boolean attemptPlaceDecorators() {
        World world = this.getWorld();
        if (sporeGrowthData.getSpores() <= 0 || random.nextInt(3) == 0) {
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

    private boolean canPlaceDecorator(BlockState state) {
        return state.isIn(ModBlockTags.SPORES_CAN_GROW);
    }

    private void placeDecorator(BlockPos pos, Direction direction) {
        if ((direction == Direction.UP || direction == Direction.DOWN) && random.nextInt(4) > 0) {
            this.placeTwistingVineChain(pos, direction, 0);
        } else {
            this.placeSnare(pos, direction);
        }
    }

    private void placeTwistingVineChain(BlockPos pos, Direction direction, int depth) {
        World world = this.getWorld();

        boolean shouldDrainWater = this.shouldDrainWater();
        int fluidloggedIndex = Fluidlogged.getFluidIndex(world.getFluidState(pos).getFluid());
        BlockState state = ModBlocks.TWISTING_VERDANT_VINES.getDefaultState()
                .with(Properties.VERTICAL_DIRECTION, direction)
                .with(ModProperties.CATALYZED, shouldDrainWater)
                .with(ModProperties.FLUIDLOGGED, fluidloggedIndex);

        boolean success = this.placeBlockWithEffects(pos, state, TWISTING_VINES_COST,
                shouldDrainWater, false,
                false);
        if (!success) {
            return;
        }

        // Chance to continue growth
        if (direction == Direction.UP && depth >= MAX_TWISTING_VINE_DEPTH_UP) {
            return;
        }
        if (direction == Direction.DOWN && depth >= MAX_TWISTING_VINE_DEPTH_DOWN) {
            return;
        }
        BlockPos nextPos = pos.offset(direction);
        if (sporeGrowthData.getSpores() > 0 && this.canPlaceDecorator(world.getBlockState(nextPos))
                && random.nextInt(5) > 0) {
            this.placeTwistingVineChain(nextPos, direction, depth + 1);
        }
    }

    private void placeSnare(BlockPos pos, Direction direction) {
        BlockFace blockFace = BlockFace.WALL;

        if (direction == Direction.UP) {
            direction = Direction.NORTH;
            blockFace = BlockFace.FLOOR;
        } else if (direction == Direction.DOWN) {
            direction = Direction.NORTH;
            blockFace = BlockFace.CEILING;
        }

        boolean shouldDrainWater = this.shouldDrainWater();
        int fluidloggedIndex = Fluidlogged.getFluidIndex(
                this.getWorld().getFluidState(pos).getFluid());
        BlockState state = ModBlocks.VERDANT_VINE_SNARE.getDefaultState()
                .with(HorizontalFacingBlock.FACING, direction)
                .with(WallMountedBlock.FACE, blockFace)
                .with(ModProperties.CATALYZED, shouldDrainWater)
                .with(ModProperties.FLUIDLOGGED, fluidloggedIndex);

        this.placeBlockWithEffects(pos, state, VINE_SNARE_COST, shouldDrainWater,
                false, true);
    }

    private void applySporeEffectToEntities(BlockPos pos) {
        if (this.getWorld() instanceof ServerWorld world) {
            SporeParticleManager.damageEntitiesInBlock(world, AetherSporeType.VERDANT, pos);
        }
    }

    @Override
    protected int getMaxStage() {
        return MAX_STAGE;
    }

    @Override
    protected int getUpdatePeriod() {
        int water = sporeGrowthData.getWater();
        int spores = sporeGrowthData.getSpores();

        if (water > spores) {
            return 5;
        }
        if (water == spores) {
            return 7;
        }
        return 8;
    }
}
