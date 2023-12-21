package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.block.LivingAetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.VerdantSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.util.BlockPosUtil;
import io.github.drakonkinst.worldsinger.util.EntityUtil;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VerdantSpores extends GrowableAetherSpores<VerdantSporeGrowthEntity> {

    public static final String NAME = "verdant";
    public static final int ID = 1;

    private static final VerdantSpores INSTANCE = new VerdantSpores();
    private static final int COLOR = 0x2e522e;
    private static final int PARTICLE_COLOR = 0x64aa4a;

    public static VerdantSpores getInstance() {
        return INSTANCE;
    }

    private VerdantSpores() {
        super(VerdantSporeGrowthEntity.class);
    }

    @Override
    public int getSmallStage() {
        return VerdantSporeGrowthEntity.MAX_STAGE;
    }

    @Override
    public EntityType<VerdantSporeGrowthEntity> getSporeGrowthEntityType() {
        return ModEntityTypes.VERDANT_SPORE_GROWTH;
    }

    @Override
    public void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos, int water) {
        // Fill with snare blocks
        this.growVerdantSpores(world, entity);

        // Only spawn spore growth if in the spore sea
        if (!world.getFluidState(pos).isIn(ModFluidTags.VERDANT_SPORES)
                && !EntityUtil.isTouchingSporeSea(entity)) {
            return;
        }

        Vec3d startPos = this.getTopmostSeaPosForEntity(world, entity, ModFluidTags.VERDANT_SPORES);
        this.spawnSporeGrowth(world, startPos, LivingAetherSporeBlock.CATALYZE_VALUE, water, true,
                false, false, Int3.ZERO);
    }

    // Fill entity's bounding box with Verdant Vine Snare
    private void growVerdantSpores(World world, LivingEntity entity) {
        BlockState newBlockState = ModBlocks.VERDANT_VINE_SNARE.getDefaultState();

        for (BlockPos pos : BlockPosUtil.iterateBoundingBoxForEntity(entity)) {
            BlockState blockState = world.getBlockState(pos);
            if (blockState.isIn(ModBlockTags.SPORES_CAN_GROW) && newBlockState.canPlaceAt(world,
                    pos)) {
                world.setBlockState(pos, newBlockState);
            }
        }
    }

    @Override
    public Item getBottledItem() {
        return ModItems.VERDANT_SPORES_BOTTLE;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.VERDANT_SPORES_BUCKET;
    }

    @Override
    public Block getFluidBlock() {
        return ModBlocks.VERDANT_SPORE_SEA;
    }

    @Override
    public Block getSolidBlock() {
        return ModBlocks.VERDANT_SPORE_BLOCK;
    }

    @Override
    public FlowableFluid getFluid() {
        return ModFluids.VERDANT_SPORES;
    }

    @Override
    public RegistryEntry<StatusEffect> getStatusEffect() {
        return ModStatusEffects.VERDANT_SPORES;
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
        return ModBlocks.VERDANT_VINE_BLOCK.getDefaultState().with(ModProperties.CATALYZED, true);
    }
}
