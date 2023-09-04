package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class AbstractSporeGrowthEntity extends MarkerEntity {

    private static final int INITIAL_GROWTH_SPEED = 3;
    private static final int DIRECTION_ARRAY_SIZE = 6;
    private static final int MAX_PLACE_ATTEMPTS = 3;
    protected static final Random random = Random.create();

    protected final SporeGrowthComponent sporeGrowthData;
    protected Int3 lastDir = Int3.ZERO;
    private int placeAttempts = 0;

    public AbstractSporeGrowthEntity(EntityType<?> entityType,
            World world) {
        super(entityType, world);
        this.sporeGrowthData = ModComponents.SPORE_GROWTH.get(this);
    }

    protected abstract BlockState getNextBlock();

    protected abstract int getWeight(World world, BlockPos pos, Int3 direction);

    protected abstract int getUpdatePeriod();

    protected abstract void updateStage();

    protected abstract int getMaxStage();

    protected abstract void onPlaceBlock(BlockState state);

    protected abstract boolean canBreakHere(BlockState state, BlockState replaceWith);

    protected abstract boolean canGrowHere(BlockState state, BlockState replaceWith);

    @Override
    public void tick() {
        if (sporeGrowthData.getOrigin() == null) {
            sporeGrowthData.setOrigin(this.getBlockPos());
        }

        if (!this.getWorld().isClient()) {
            if (this.shouldBeDead()) {
                this.discard();
            } else {
                this.grow();
            }
        }
    }

    protected boolean shouldBeDead() {
        return sporeGrowthData.getStage() > this.getMaxStage() || sporeGrowthData.getAge() > 100;
    }

    private void grow() {
        if (sporeGrowthData.isInitialGrowth()) {
            for (int i = 0; i < INITIAL_GROWTH_SPEED; ++i) {
                this.doGrowStep();
            }
        } else {
            if ((sporeGrowthData.getAge() + this.getId()) % this.getUpdatePeriod() == 0) {
                this.doGrowStep();
            }
        }
    }

    private void doGrowStep() {
        if (this.attemptPlaceBlock(this.getNextBlock())) {
            this.shiftBlock(this.getNextDirection());
            this.updateStage();
            placeAttempts = 0;
        } else {
            ModConstants.LOGGER.info("FAILED TO PLACE BLOCK");
            if (++placeAttempts >= MAX_PLACE_ATTEMPTS) {
                ModConstants.LOGGER.info("DEAD");
                this.discard();
            }
        }
    }

    private boolean attemptPlaceBlock(BlockState state) {
        if (state == null) {
            return false;
        }
        BlockPos blockPos = this.getBlockPos();
        BlockState originalState = this.getWorld().getBlockState(blockPos);
        if (this.canBreakHere(originalState, state)) {
            boolean shouldDropLoot = random.nextInt(3) > 0;
            this.getWorld().breakBlock(blockPos, shouldDropLoot, this);
            return this.placeBlock(state);
        } else if (this.canGrowHere(originalState, state)) {
            return this.placeBlock(state);
        }
        return false;
    }

    private boolean placeBlock(BlockState state) {
        boolean success = this.getWorld().setBlockState(this.getBlockPos(), state);
        if (success) {
            this.onPlaceBlock(state);
        }
        return success;
    }

    private void shiftBlock(Int3 direction) {
        Vec3d pos = this.getPos();
        this.setPosition(pos.add(direction.x(), direction.y(), direction.z()));
        if (!direction.isZero()) {
            lastDir = direction;
        }
    }

    private Int3 getNextDirection() {
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();
        Mutable mutable = new Mutable();
        List<Int3> candidates = new ArrayList<>(DIRECTION_ARRAY_SIZE);
        IntList weights = new IntArrayList(DIRECTION_ARRAY_SIZE);
        int weightSum = 0;
        for (Int3 direction : Int3.CARDINAL_3D) {
            if (direction.isZero() || direction.equals(lastDir)) {
                continue;
            }
            mutable.set(pos.getX() + direction.x(), pos.getY() + direction.y(),
                    pos.getZ() + direction.z());
            int weight = this.getWeight(world, mutable, direction);
            if (weight > 0) {
                candidates.add(direction);
                weights.add(weight);
                weightSum += weight;
            }
        }
        Int3 nextDirection = chooseWeighted(candidates, weights, weightSum);
        return nextDirection;
    }

    private Int3 chooseWeighted(List<Int3> candidates, IntList weights, int weightSum) {
        if (candidates.isEmpty()) {
            return Int3.ZERO;
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        int currentWeight = 0;
        int targetWeight = random.nextInt(weightSum);
        for (int i = 0; i < candidates.size(); ++i) {
            currentWeight += weights.getInt(i);
            if (currentWeight >= targetWeight) {
                return candidates.get(i);
            }
        }
        return Int3.ZERO;
    }
}
