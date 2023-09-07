package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import io.github.drakonkinst.worldsinger.world.WaterReactionManager;
import io.github.drakonkinst.worldsinger.world.lumar.SporeKillingManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class AbstractSporeGrowthEntity extends MarkerEntity {

    private static final int INITIAL_GROWTH_SPEED = 3;
    private static final int DIRECTION_ARRAY_SIZE = 6;
    private static final int MAX_PLACE_ATTEMPTS = 3;
    private static final int MAX_AGE = 20 * 5;
    private static final int SPORE_DRAIN_NEAR_SPORE_KILLABLE = 50;
    protected static final Random random = Random.create();

    public static void playPlaceSoundEffect(World world, BlockPos pos, BlockState state) {
        Vec3d centerPos = pos.toCenterPos();
        world.playSound(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(),
                state.getSoundGroup().getPlaceSound(),
                SoundCategory.BLOCKS, 1.0f, 0.8f + 0.4f * random.nextFloat(),
                random.nextLong());
    }

    private static void resetCatalyzed(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.contains(ModProperties.CATALYZED) && state.get(ModProperties.CATALYZED)) {
            world.setBlockState(pos, state.with(ModProperties.CATALYZED, false));
        }
    }

    protected final SporeGrowthComponent sporeGrowthData;
    protected Int3 lastDir = Int3.ZERO;
    private int placeAttempts = 0;

    public AbstractSporeGrowthEntity(EntityType<?> entityType,
            World world) {
        super(entityType, world);
        this.sporeGrowthData = ModComponents.SPORE_GROWTH.get(this);
    }

    public void setSporeData(int spores, int water, boolean initialGrowth) {
        sporeGrowthData.setSpores(spores);
        sporeGrowthData.setWater(water);
        sporeGrowthData.setInitialGrowth(initialGrowth);
    }

    public void setInitialStage(int stage) {
        if (sporeGrowthData.getStage() == 0) {
            sporeGrowthData.addStage(stage);
        }
    }

    public void setLastDir(Int3 lastDir) {
        this.lastDir = lastDir;
    }

    protected abstract BlockState getNextBlock();

    protected abstract int getWeight(World world, BlockPos pos, Int3 direction,
            boolean allowPassthrough);

    protected abstract int getUpdatePeriod();

    protected abstract void updateStage();

    protected abstract int getMaxStage();

    protected abstract void onGrowBlock(BlockPos pos, BlockState state);

    protected abstract boolean canBreakHere(BlockState state, BlockState replaceWith);

    protected abstract boolean canGrowHere(BlockState state, BlockState replaceWith);

    protected abstract boolean isGrowthBlock(BlockState state);

    @Override
    public void tick() {
        if (sporeGrowthData.getOrigin() == null) {
            sporeGrowthData.setOrigin(this.getBlockPos());
        }

        if (!this.getWorld().isClient()) {
            if (this.shouldBeDead()) {
                if (sporeGrowthData.getSpores() > 0) {
                    this.onEarlyDiscard();
                }
                this.discard();
            } else {
                this.grow();
            }
        }
    }

    protected boolean placeBlockWithEffects(BlockPos pos, BlockState state, int cost,
            boolean drainsWater, boolean showParticles, boolean playSound) {
        boolean success = this.getWorld().setBlockState(pos, state);

        if (success) {
            this.doGrowEffects(pos, state, cost, drainsWater, showParticles, playSound);
        }

        return success;
    }

    protected void doGrowEffects(BlockPos pos, BlockState state, int cost, boolean drainsWater,
            boolean showParticles, boolean playSound) {
        if (drainsWater) {
            this.drainWater(cost);
        }
        this.drainSpores(cost);

        if (showParticles) {
            this.spawnParticles(pos.toCenterPos(), state);
        }

        if (playSound) {
            AbstractSporeGrowthEntity.playPlaceSoundEffect(this.getWorld(), pos, state);
        }
    }

    protected boolean shouldBeDead() {
        if (sporeGrowthData.getWater() <= 0) {
            ModConstants.LOGGER.info("NO WATER " + sporeGrowthData.getWater());
        }
        return sporeGrowthData.getStage() > this.getMaxStage()
                || sporeGrowthData.getSpores() <= 0 || sporeGrowthData.getAge() > MAX_AGE
                || placeAttempts >= MAX_PLACE_ATTEMPTS || sporeGrowthData.getWater() <= 0;
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
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();

        if (SporeKillingManager.isSporeKillingBlockNearby(world, pos)) {
            this.drainSpores(SPORE_DRAIN_NEAR_SPORE_KILLABLE);
        }

        if (sporeGrowthData.getWater() < sporeGrowthData.getSpores()
                && world.getFluidState(pos).isIn(FluidTags.WATER)) {
            int waterAbsorbed = WaterReactionManager.absorbWaterAtBlock(world, pos);
            if (waterAbsorbed > 0) {
                sporeGrowthData.setWater(sporeGrowthData.getWater() + waterAbsorbed);
            }
        }

        if (this.attemptGrowBlock(this.getNextBlock())) {
            this.shiftBlock(this.getNextDirection(false));
            this.updateStage();
            placeAttempts = 0;
        } else if (this.canMoveThrough()) {
            Int3 direction = this.getNextDirection(true);
            if (direction.isZero()) {
                placeAttempts++;
            } else {
                this.shiftBlock(direction);
            }
        } else {
            placeAttempts++;
        }
    }

    private boolean canMoveThrough() {
        return this.isGrowthBlock(this.getWorld().getBlockState(this.getBlockPos()));
    }

    private boolean attemptGrowBlock(BlockState state) {
        if (state == null) {
            return false;
        }
        BlockPos blockPos = this.getBlockPos();
        BlockState originalState = this.getWorld().getBlockState(blockPos);
        if (this.canBreakHere(originalState, state)) {
            boolean shouldDropLoot = random.nextInt(3) > 0;
            this.getWorld().breakBlock(blockPos, shouldDropLoot, this);
            return this.growBlock(state);
        } else if (this.canGrowHere(originalState, state)) {
            if (!originalState.isAir()) {
                boolean shouldDropLoot = random.nextInt(3) > 0;
                this.getWorld().breakBlock(blockPos, shouldDropLoot, this);
            }
            return this.growBlock(state);
        } else if (originalState.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)) {
            return this.growBlock(state);
        }
        return false;
    }

    private boolean growBlock(BlockState state) {
        // Set the block only, let each block handle its own grow effects in onGrowBlock()
        BlockPos pos = this.getBlockPos();
        boolean success = this.getWorld().setBlockState(pos, state);
        if (success) {
            this.onGrowBlock(pos, state);
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

    private Int3 getNextDirection(boolean allowPassthrough) {
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();
        Mutable mutable = new Mutable();
        List<Int3> candidates = new ArrayList<>(DIRECTION_ARRAY_SIZE);
        IntList weights = new IntArrayList(DIRECTION_ARRAY_SIZE);
        int weightSum = 0;
        for (Int3 direction : Int3.CARDINAL_3D) {
            if (direction.isZero() || direction.equals(lastDir.opposite())) {
                continue;
            }
            mutable.set(pos.getX() + direction.x(), pos.getY() + direction.y(),
                    pos.getZ() + direction.z());
            int weight = this.getWeight(world, mutable, direction, allowPassthrough);
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

    protected void drainSpores(int cost) {
        sporeGrowthData.setSpores(sporeGrowthData.getSpores() - cost);
    }

    protected void drainWater(int cost) {
        sporeGrowthData.setWater(sporeGrowthData.getWater() - cost);
    }

    private void spawnParticles(Vec3d centerPos, BlockState state) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                    centerPos.getX(), centerPos.getY(), centerPos.getZ(), 100, 0.0, 0.0, 0.0,
                    0.15f);
        }
    }

    private void onEarlyDiscard() {
        BlockPos pos = this.getBlockPos();
        BlockPos.Mutable mutable = pos.mutableCopy();
        World world = this.getWorld();
        AbstractSporeGrowthEntity.resetCatalyzed(world, pos);
        for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
            AbstractSporeGrowthEntity.resetCatalyzed(world, mutable.set(pos.offset(direction)));
        }
    }

    public SporeGrowthComponent getSporeGrowthData() {
        return sporeGrowthData;
    }
}
