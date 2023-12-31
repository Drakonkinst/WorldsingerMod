package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
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

public class MidnightSpores extends AetherSpores {

    public static final String NAME = "midnight";
    public static final int ID = 6;

    private static final MidnightSpores INSTANCE = new MidnightSpores();
    private static final int COLOR = 0x111111;
    private static final int PARTICLE_COLOR = 0x111111;

    public static MidnightSpores getInstance() {
        return INSTANCE;
    }

    private MidnightSpores() {}

    @Override
    public void doReaction(World world, Vec3d pos, int spores, int water, Random random) {
        // TODO
    }

    @Override
    public void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos, int water) {
        // TODO
    }

    @Override
    public Item getBottledItem() {
        return ModItems.MIDNIGHT_SPORES_BOTTLE;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.MIDNIGHT_SPORES_BUCKET;
    }

    @Override
    public Block getFluidBlock() {
        return ModBlocks.MIDNIGHT_SPORE_SEA;
    }

    @Override
    public Block getSolidBlock() {
        return ModBlocks.MIDNIGHT_SPORE_BLOCK;
    }

    @Override
    public FlowableFluid getFluid() {
        return ModFluids.MIDNIGHT_SPORES;
    }

    @Override
    public RegistryEntry<StatusEffect> getStatusEffect() {
        return ModStatusEffects.MIDNIGHT_SPORES;
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
}
