package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.RoseiteSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RoseiteSpores extends GrowableAetherSpores<RoseiteSporeGrowthEntity> {

    public static final String NAME = "roseite";
    public static final int ID = 5;

    private static final RoseiteSpores INSTANCE = new RoseiteSpores();
    private static final int COLOR = 0xce9db2;
    private static final int PARTICLE_COLOR = 0xce9db2;

    public static RoseiteSpores getInstance() {
        return INSTANCE;
    }

    private RoseiteSpores() {
        super(RoseiteSporeGrowthEntity.class);
    }

    @Override
    public EntityType<RoseiteSporeGrowthEntity> getSporeGrowthEntityType() {
        return ModEntityTypes.ROSEITE_SPORE_GROWTH;
    }

    @Override
    public int getSmallStage() {
        return 0;
    }

    @Override
    public void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos, int water) {
        // TODO
    }

    @Override
    public Item getBottledItem() {
        return ModItems.ROSEITE_SPORES_BOTTLE;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.ROSEITE_SPORES_BUCKET;
    }

    @Override
    public Block getFluidBlock() {
        return ModBlocks.ROSEITE_SPORE_SEA;
    }

    @Override
    public Block getSolidBlock() {
        return ModBlocks.ROSEITE_SPORE_BLOCK;
    }

    @Override
    public FlowableFluid getFluid() {
        return ModFluids.ROSEITE_SPORES;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return ModStatusEffects.ROSEITE_SPORES;
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
    public BlockState getFluidCollisionState() {
        return ModBlocks.ROSEITE_BLOCK.getDefaultState();
    }
}
