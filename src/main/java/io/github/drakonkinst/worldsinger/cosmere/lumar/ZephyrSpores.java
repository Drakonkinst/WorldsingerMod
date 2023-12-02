package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.LivingAetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ZephyrSpores extends AetherSpores {

    public static final String NAME = "zephyr";
    public static final int ID = 4;

    private static final ZephyrSpores INSTANCE = new ZephyrSpores();
    private static final int COLOR = 0x4b9bb7;
    private static final int PARTICLE_COLOR = 0x4b9bb7;

    private static final float SPORE_TO_POWER_MULTIPLIER = 0.25f;

    public static ZephyrSpores getInstance() {
        return INSTANCE;
    }

    protected ZephyrSpores() {}

    @Override
    public void doReaction(World world, Vec3d pos, int spores, int water, Random random) {
        // TODO: Deal damage
        // TODO: Drain surrounding zephyr spores too?
        float power = Math.min(spores, water) * SPORE_TO_POWER_MULTIPLIER + random.nextFloat();
        Worldsinger.LOGGER.info("spores = " + spores + ", water = " + water
                + " base_value = " + (Math.min(spores, water) * SPORE_TO_POWER_MULTIPLIER)
                + ", power = " + power);
        world.createExplosion(
                null,
                null,
                WindChargeEntity.EXPLOSION_BEHAVIOR,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                power,
                false,
                World.ExplosionSourceType.BLOW,
                ParticleTypes.GUST,
                ParticleTypes.GUST_EMITTER,
                ModSoundEvents.BLOCK_ZEPHYR_SEA_CATALYZE
        );
    }

    @Override
    public void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos, int water) {
        Vec3d startPos = this.getTopmostSeaPosForEntity(world, entity,
                ModFluidTags.ZEPHYR_SPORES);
        this.doReaction(world, startPos, LivingAetherSporeBlock.CATALYZE_VALUE, water,
                world.getRandom());
    }

    @Override
    public Item getBottledItem() {
        return ModItems.ZEPHYR_SPORES_BOTTLE;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.ZEPHYR_SPORES_BUCKET;
    }

    @Override
    public Block getFluidBlock() {
        return ModBlocks.ZEPHYR_SPORE_SEA;
    }

    @Override
    public Block getSolidBlock() {
        return ModBlocks.ZEPHYR_SPORE_BLOCK;
    }

    @Override
    public FlowableFluid getFluid() {
        return ModFluids.ZEPHYR_SPORES;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return ModStatusEffects.ZEPHYR_SPORES;
    }

    @Override
    public int getColor() {
        return COLOR;
    }

    @Override
    public int getParticleColor() {
        return PARTICLE_COLOR;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @Nullable BlockState getFluidCollisionState() {
        return Blocks.AIR.getDefaultState();
    }
}
