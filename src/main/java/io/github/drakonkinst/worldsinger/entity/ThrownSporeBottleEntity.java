package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.item.SporeBottleItem;
import io.github.drakonkinst.worldsinger.world.WaterReactionManager;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.CrimsonSporeManager;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleSpawner;
import io.github.drakonkinst.worldsinger.world.lumar.SporeType;
import io.github.drakonkinst.worldsinger.world.lumar.VerdantSporeManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThrownSporeBottleEntity extends ThrownItemEntity implements FlyingItemEntity {

    private static final int SPORE_AMOUNT = 75;
    private static final int WATER_AMOUNT_PER_LEVEL = 80;

    public ThrownSporeBottleEntity(
            EntityType<? extends ThrownSporeBottleEntity> entityType,
            World world) {
        super(entityType, world);
    }

    public ThrownSporeBottleEntity(World world, LivingEntity owner) {
        super(ModEntityTypes.THROWN_SPORE_BOTTLE, owner, world);
    }

    public ThrownSporeBottleEntity(World world, double x, double y, double z) {
        super(ModEntityTypes.THROWN_SPORE_BOTTLE, x, y, z, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        World world = this.getWorld();
        if (world.isClient()) {
            return;
        }

        Vec3d pos = this.getPos();
        if (world instanceof ServerWorld serverWorld) {
            SporeParticleSpawner.spawnSplashPotionParticles(serverWorld, this.getSporeType(), pos);
        }

        SporeType sporeType = this.getSporeType();
        if (sporeType != AetherSporeType.DEAD) {
            this.handleLivingSporeBehavior(world, sporeType, pos);
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_SPLASH_POTION_BREAK,
                SoundCategory.NEUTRAL, 1.0f, random.nextFloat() * 0.1f + 0.9f,
                world.getRandom().nextLong());
        this.discard();
    }

    private void handleLivingSporeBehavior(World world, SporeType sporeType, Vec3d pos) {
        BlockPos blockPos = this.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            int waterAmount = WaterReactionManager.absorbWater(world, blockPos);
            if (sporeType == AetherSporeType.VERDANT) {
                VerdantSporeManager.spawnVerdantSporeGrowth(world, pos, SPORE_AMOUNT, waterAmount,
                        true, true, false);
            } else if (sporeType == AetherSporeType.CRIMSON) {
                CrimsonSporeManager.spawnCrimsonSporeGrowth(world, pos, SPORE_AMOUNT, waterAmount,
                        true, true, false);
            }
            // TODO: Add remaining spore behavior
        } else if (blockState.isOf(Blocks.WATER_CAULDRON)) {
            int waterAmount =
                    WATER_AMOUNT_PER_LEVEL * blockState.get(LeveledCauldronBlock.LEVEL);
            world.setBlockState(blockPos, Blocks.CAULDRON.getStateWithProperties(blockState));

            if (sporeType == AetherSporeType.VERDANT) {
                BlockPos posAbove = blockPos.up();
                BlockState stateAbove = world.getBlockState(posAbove);
                if (stateAbove.isIn(ModBlockTags.SPORES_CAN_GROW) || stateAbove.isIn(
                        ModBlockTags.SPORES_CAN_BREAK)) {
                    VerdantSporeManager.spawnVerdantSporeGrowth(world, posAbove.toCenterPos(),
                            SPORE_AMOUNT, waterAmount, true, true, false);
                }
            } else if (sporeType == AetherSporeType.CRIMSON) {
                BlockPos posAbove = blockPos.up();
                BlockState stateAbove = world.getBlockState(posAbove);
                if (stateAbove.isIn(ModBlockTags.SPORES_CAN_GROW) || stateAbove.isIn(
                        ModBlockTags.SPORES_CAN_BREAK)) {
                    CrimsonSporeManager.spawnCrimsonSporeGrowth(world, posAbove.toCenterPos(),
                            SPORE_AMOUNT, waterAmount, true, true, false);
                }
            }

        }
    }

    private SporeType getSporeType() {
        ItemStack stack = this.getStack();
        if (stack.getItem() instanceof SporeBottleItem sporeBottleItem) {
            return sporeBottleItem.getSporeType();
        }
        return null;
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.DEAD_SPORES_SPLASH_BOTTLE;
    }
}
