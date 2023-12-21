package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DeadSpores extends AetherSpores {

    public static final String NAME = "dead";
    public static final int ID = 0;

    private static final DeadSpores INSTANCE = new DeadSpores();
    private static final int COLOR = 0x888888;
    private static final int PARTICLE_COLOR = 0xaaaaaa;

    public static DeadSpores getInstance() {
        return INSTANCE;
    }

    private DeadSpores() {}

    @Override
    public void doReaction(World world, Vec3d pos, int spores, int water, Random random) {
        // Do nothing
    }

    @Override
    public void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos, int water) {
        // Do nothing
    }

    @Override
    public Item getBottledItem() {
        return ModItems.DEAD_SPORES_BOTTLE;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.DEAD_SPORES_BUCKET;
    }

    @Override
    public Block getFluidBlock() {
        return ModBlocks.DEAD_SPORE_SEA;
    }

    @Override
    public Block getSolidBlock() {
        return ModBlocks.DEAD_SPORE_BLOCK;
    }

    @Override
    public FlowableFluid getFluid() {
        return ModFluids.DEAD_SPORES;
    }

    @Override
    @Nullable
    public RegistryEntry<StatusEffect> getStatusEffect() {
        return null;
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
    public boolean isDead() {
        return true;
    }
}
