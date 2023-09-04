package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.block.VerdantVineBranchBlock;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VerdantSporeGrowthEntity extends AbstractSporeGrowthEntity {

    private static final Direction[] CARDINAL_DIRECTIONS = Direction.values();
    private static final int MAX_TWISTING_VINE_DEPTH_UP = 3;
    private static final int MAX_TWISTING_VINE_DEPTH_DOWN = 7;

    public VerdantSporeGrowthEntity(EntityType<?> entityType,
            World world) {
        super(entityType, world);
    }

    @Override
    protected BlockState getNextBlock() {
        if (sporeGrowthData.getStage() == 0) {
            return ModBlocks.VERDANT_VINE_BLOCK.getDefaultState()
                    .with(Properties.AXIS, this.getPlacementAxis());
        } else if (sporeGrowthData.getStage() == 1) {
            VerdantVineBranchBlock block = (VerdantVineBranchBlock) ModBlocks.VERDANT_VINE_BRANCH;
            return block.withConnectionProperties(this.getWorld(), this.getBlockPos());
        }
        return null;
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
    protected int getWeight(World world, BlockPos pos, Int3 direction) {
        if (sporeGrowthData.getStage() == 3) {
            if (direction.x() != 0 || direction.z() != 0) {
                return 0;
            }
        }

        BlockState state = world.getBlockState(pos);

        int weight = 0;

        // Prefer not to break through blocks
        if (this.canBreakHere(state, null)) {
            weight = 20;
        } else if (this.canGrowHere(state, null)) {
            // Can grow through lesser vines
            weight = 80;
        }

        if (weight == 0) {
            return 0;
        }

        // Prefer to grow upwards
        weight += 20 * direction.y();

        // Prefer to go in the same direction
        if (direction.equals(lastDir)) {
            weight += 10;
        }

        // Prefers to go in the same direction away from the origin
        int dirFromOriginX = (int) Math.signum(pos.getX() - sporeGrowthData.getOrigin().getX());
        int dirFromOriginY = (int) Math.signum(pos.getY() - sporeGrowthData.getOrigin().getY());
        int dirFromOriginZ = (int) Math.signum(pos.getZ() - sporeGrowthData.getOrigin().getZ());
        if (direction.y() == dirFromOriginX || direction.y() == dirFromOriginY
                || direction.z() == dirFromOriginZ) {
            weight += 50;
        }

        // Bonuses based on neighbors
        weight += this.getNeighborBonus(world, pos);

        // Bonus for moving away from origin
        weight += 10 * this.getDistanceFromOrigin(pos);

        return weight;
    }

    private int getNeighborBonus(World world, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int weightBonus = 0;
        int vineNeighbors = 0;
        boolean hugsBlock = false;
        for (Direction direction : CARDINAL_DIRECTIONS) {
            mutable.set(pos.offset(direction));
            BlockState state = world.getBlockState(mutable);
            if (state.isIn(ModBlockTags.VERDANT_VINES)) {
                // Prefer NOT to be adjacent to too many other of the same block
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
        if (sporeGrowthData.getStage() < this.getMaxStage() && random.nextInt(10) == 0) {
            sporeGrowthData.addStage(1);
        }
    }

    @Override
    protected boolean canBreakHere(BlockState state, BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_BREAK);
    }

    @Override
    protected boolean canGrowHere(BlockState state, BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_GROW)
                || state.isIn(ModBlockTags.VERDANT_VINE_SNARE)
                || state.isIn(ModBlockTags.TWISTING_VERDANT_VINES);
    }

    @Override
    protected void onPlaceBlock(BlockState state) {
        showParticles(state);
        attemptPlaceDecorators();
    }

    private void showParticles(BlockState state) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d pos = this.getBlockPos().toCenterPos();
            serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                    pos.getX(), pos.getY(), pos.getZ(), 100, 0.0, 0.0, 0.0, 0.15f);
        }
    }

    private boolean attemptPlaceDecorators() {
        World world = this.getWorld();
        if (random.nextInt(3) == 0) {
            return false;
        }
        List<Direction> validDirections = new ArrayList<>(6);
        BlockPos pos = this.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : CARDINAL_DIRECTIONS) {
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

        BlockState state = ModBlocks.TWISTING_VERDANT_VINES.getDefaultState()
                .with(Properties.VERTICAL_DIRECTION, direction);
        world.setBlockState(pos, state);

        BlockPos nextPos = pos.offset(direction);

        if (direction == Direction.UP && depth >= MAX_TWISTING_VINE_DEPTH_UP) {
            return;
        }
        if (direction == Direction.DOWN && depth >= MAX_TWISTING_VINE_DEPTH_DOWN) {
            return;
        }
        if (this.canPlaceDecorator(world.getBlockState(nextPos)) && random.nextInt(5) > 0) {
            this.placeTwistingVineChain(nextPos, direction, depth + 1);
        }
    }

    private void placeSnare(BlockPos pos, Direction direction) {
        WallMountLocation wallMountLocation = WallMountLocation.WALL;

        if (direction == Direction.UP) {
            direction = Direction.NORTH;
            wallMountLocation = WallMountLocation.FLOOR;
        } else if (direction == Direction.DOWN) {
            direction = Direction.NORTH;
            wallMountLocation = WallMountLocation.CEILING;
        }

        BlockState state = ModBlocks.VERDANT_VINE_SNARE.getDefaultState()
                .with(HorizontalFacingBlock.FACING, direction)
                .with(WallMountedBlock.FACE, wallMountLocation);
        this.getWorld().setBlockState(pos, state);
    }

    @Override
    protected int getMaxStage() {
        return 1;
    }

    @Override
    protected int getUpdatePeriod() {
        return 3;
    }
}
