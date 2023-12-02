package io.github.drakonkinst.worldsinger.cosmere.lumar;

import com.google.common.collect.Maps;
import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.LivingAetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class ZephyrSpores extends AetherSpores {

    public static final String NAME = "zephyr";
    public static final int ID = 4;

    private static final ZephyrSpores INSTANCE = new ZephyrSpores();
    private static final int COLOR = 0x4b9bb7;
    private static final int PARTICLE_COLOR = 0x4b9bb7;

    private static final float SPORE_TO_POWER_MULTIPLIER = 0.15f;
    private static final float KNOCKBACK_MULTIPLIER = 2.0f;
    private static final int MAX_DISTANCE = 64;

    public static ZephyrSpores getInstance() {
        return INSTANCE;
    }

    private ZephyrSpores() {}

    @Override
    public void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos, int water) {
        Vec3d startPos = this.getTopmostSeaPosForEntity(world, entity, ModFluidTags.ZEPHYR_SPORES);
        this.doReaction(world, startPos, LivingAetherSporeBlock.CATALYZE_VALUE, water,
                world.getRandom());
    }

    @Override
    public void doReaction(World world, Vec3d pos, int spores, int water, Random random) {
        float power = Math.min(spores, water) * SPORE_TO_POWER_MULTIPLIER + random.nextFloat();
        Worldsinger.LOGGER.info("spores = " + spores + ", water = " + water + " base_value = " + (
                Math.min(spores, water) * SPORE_TO_POWER_MULTIPLIER) + ", power = " + power);
        int numAffectedEntities = this.explode(world, pos, power, KNOCKBACK_MULTIPLIER);
        Worldsinger.LOGGER.info("Hit " + numAffectedEntities + " entities");
    }

    // Too inefficient to use actual Explosions, so we'll need our own solution
    private int explode(World world, Vec3d centerPos, float radius,
            float globalKnockbackMultiplier) {
        world.emitGameEvent(null, GameEvent.EXPLODE, centerPos);
        Box box = BoxUtil.createBoxAroundPos(centerPos, radius);
        List<Entity> entities = world.getEntitiesByClass(Entity.class, box,
                EntityPredicates.EXCEPT_SPECTATOR);

        int numAffectedEntities = 0;
        Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();
        for (Entity entity : entities) {
            if (entity.isImmuneToExplosion(null)) {
                continue;
            }

            double sqrDistance = entity.getPos().squaredDistanceTo(centerPos);
            if (sqrDistance > radius * radius) {
                continue;
            }
            double distanceMultiplier = Math.sqrt(sqrDistance) / radius;
            double forceX = entity.getX() - centerPos.getX();
            double forceY = entity.getY() - centerPos.getY();
            double forceZ = entity.getZ() - centerPos.getZ();
            double delta = Math.sqrt(forceX * forceX + forceY * forceY + forceZ * forceZ);
            if (delta <= 0.0) {
                continue;
            }

            double knockbackMultiplier =
                    (1.0 - distanceMultiplier) * Explosion.getExposure(centerPos, entity)
                            * globalKnockbackMultiplier;
            if (entity instanceof LivingEntity livingEntity) {
                knockbackMultiplier = ProtectionEnchantment.transformExplosionKnockback(
                        livingEntity, knockbackMultiplier);
            }

            double forceMultiplier = knockbackMultiplier / delta;
            forceX = forceX * forceMultiplier;
            forceY = forceY * forceMultiplier;
            forceZ = forceZ * forceMultiplier;
            Vec3d forceVector = new Vec3d(forceX, forceY, forceZ);
            entity.setVelocity(entity.getVelocity().add(forceVector));

            if (entity instanceof PlayerEntity playerEntity && !playerEntity.isSpectator() && (
                    !playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                affectedPlayers.put(playerEntity, forceVector);
            }

            numAffectedEntities += 1;
        }

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                if (player.squaredDistanceTo(centerPos) < MAX_DISTANCE * MAX_DISTANCE) {
                    player.networkHandler.sendPacket(
                            new ExplosionS2CPacket(centerPos.getX(), centerPos.getY(),
                                    centerPos.getZ(), radius, Collections.emptyList(),
                                    affectedPlayers.get(player),
                                    Explosion.DestructionType.TRIGGER_BLOCK, ParticleTypes.GUST,
                                    ParticleTypes.GUST_EMITTER,
                                    ModSoundEvents.BLOCK_ZEPHYR_SEA_CATALYZE));
                }
            }
        }
        return numAffectedEntities;
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
