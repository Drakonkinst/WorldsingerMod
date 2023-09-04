package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class AbstractSporeGrowthEntity extends MarkerEntity {

    private static final int INITIAL_GROWTH_SPEED = 3;
    private static final int DIRECTION_ARRAY_SIZE = 3 * 3 * 3 - 1;
    private static final Random random = Random.create();

    protected final SporeGrowthComponent sporeGrowthData;
    protected Int3 lastDir = Int3.ZERO;

    public AbstractSporeGrowthEntity(EntityType<?> entityType,
            World world) {
        super(entityType, world);
        this.sporeGrowthData = ModComponents.SPORE_GROWTH.get(this);
    }

    protected abstract BlockState getNextBlock();

    protected abstract int getWeight(World world, BlockPos pos, Int3 direction);

    protected abstract int getUpdatePeriod();

    protected abstract boolean shouldShowParticles();

    @Override
    public void tick() {
        if (!this.getWorld().isClient()) {
            if (this.shouldBeDead()) {
                this.discard();
            } else {
                this.grow();
            }
        }
    }

    protected boolean shouldBeDead() {
        return sporeGrowthData.getAge() > 100;
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
        this.placeBlock(this.getNextBlock(), this.shouldShowParticles());
        this.shiftBlock(this.getNextDirection());
    }

    private void placeBlock(BlockState state, boolean showParticles) {
        this.getWorld().setBlockState(this.getBlockPos(), state);
        Vec3d pos = this.getBlockPos().toCenterPos();
        if (showParticles && this.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                    pos.getX(), pos.getY(), pos.getZ(), 100, 0.0, 0.0, 0.0, 0.15f);
        }
    }

    private void shiftBlock(Int3 direction) {
        Vec3d pos = this.getPos();
        this.setPosition(pos.add(direction.x(), direction.y(), direction.z()));
        lastDir = direction;
    }

    protected Int3 getNextDirection() {
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();
        Mutable mutable = new Mutable();
        List<Int3> candidates = new ArrayList<>(DIRECTION_ARRAY_SIZE);
        IntList weights = new IntArrayList(DIRECTION_ARRAY_SIZE);
        int weightSum = 0;
        for (Int3 direction : Int3.DIAGONAL_3D) {
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
