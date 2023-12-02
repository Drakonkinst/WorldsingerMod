package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.block.LivingAetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.entity.CrimsonSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.SporeGrowthEntity;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.util.BlockPosUtil;
import io.github.drakonkinst.worldsinger.util.EntityUtil;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CrimsonSpores extends AetherSpores {

    public static final String NAME = "crimson";
    public static final int ID = 2;

    private static final CrimsonSpores INSTANCE = new CrimsonSpores();
    private static final int COLOR = 0xd72e2d;
    private static final int PARTICLE_COLOR = 0xe44d61;

    public static CrimsonSpores getInstance() {
        return INSTANCE;
    }

    protected CrimsonSpores() {}

    @Override
    public void doReaction(World world, Vec3d pos, int spores, int water, Random random) {
        // Do nothing
        this.spawnSporeGrowth(world, pos, spores, water, true, false, false, Int3.ZERO);
    }

    @Override
    public void doReactionFromFluidContainer(World world, BlockPos fluidContainerPos, int spores,
            int water, Random random) {
        super.doReactionFromFluidContainer(world, fluidContainerPos.up(), spores, water, random);
    }

    @Override
    public void doReactionFromSplashBottle(World world, Vec3d pos, int spores, int water,
            Random random, boolean affectingFluidContainer) {
        if (affectingFluidContainer) {
            BlockPos posAbove = BlockPosUtil.toRoundedBlockPos(pos).up();
            BlockState stateAbove = world.getBlockState(posAbove);
            if (stateAbove.isIn(ModBlockTags.SPORES_CAN_GROW) || stateAbove.isIn(
                    ModBlockTags.SPORES_CAN_BREAK)) {
                this.spawnSporeGrowth(world, pos.add(0.0, 1.0, 0.0), spores, water, true, true,
                        false, Int3.ZERO);
            }
        }
        this.spawnSporeGrowth(world, pos, spores, water, true, true, false, Int3.ZERO);
    }

    @Override
    public void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos, int water) {
        this.growCrimsonSpores(world, entity);

        // Only spawn spore growth if in the spore sea
        if (!world.getFluidState(pos).isIn(ModFluidTags.CRIMSON_SPORES)
                && !EntityUtil.isTouchingSporeSea(entity)) {
            return;
        }

        Vec3d startPos = this.getTopmostSeaPosForEntity(world, entity,
                ModFluidTags.CRIMSON_SPORES);
        this.spawnSporeGrowth(world, startPos, LivingAetherSporeBlock.CATALYZE_VALUE, water, true,
                false, false, Int3.ZERO);
    }

    public void spawnSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit, Int3 lastDir) {
        // If one already exists nearby, just augment that one
        if (!isSplit && this.tryCombineWithNearbyGrowth(world, pos, spores, water, initialGrowth,
                isSmall)) {
            return;
        }

        CrimsonSporeGrowthEntity entity = ModEntityTypes.CRIMSON_SPORE_GROWTH.create(world);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos);
        entity.setSporeData(spores, water, initialGrowth);
        entity.setLastDir(lastDir);
        if (isSmall) {
            entity.setInitialStage(CrimsonSporeGrowthEntity.MAX_STAGE - 1);
        }

        world.spawnEntity(entity);
    }

    // Fill entity's bounding box with Crimson Snare
    private void growCrimsonSpores(World world, LivingEntity entity) {
        BlockState newBlockState = ModBlocks.CRIMSON_SNARE.getDefaultState();

        for (BlockPos pos : BlockPosUtil.iterateBoundingBoxForEntity(entity)) {
            if (!newBlockState.canPlaceAt(world, pos)) {
                continue;
            }

            BlockState blockState = world.getBlockState(pos);
            if (blockState.isIn(ModBlockTags.SPORES_CAN_GROW)) {
                world.setBlockState(pos, newBlockState);
            } else if (blockState.isIn(ModBlockTags.SPORES_CAN_BREAK)) {
                SporeGrowthEntity.breakBlockFromSporeGrowth(world, pos, null);
                world.setBlockState(pos, newBlockState);
            }
        }
    }

    private boolean tryCombineWithNearbyGrowth(World world, Vec3d pos, int spores,
            int water, boolean isInitial, boolean isSmall) {
        Box box = Box.from(pos).expand(VerdantSpores.COMBINE_GROWTH_MAX_RADIUS);
        List<CrimsonSporeGrowthEntity> nearbySporeGrowthEntities = world
                .getEntitiesByClass(CrimsonSporeGrowthEntity.class, box, sporeGrowthEntity -> {
                    SporeGrowthComponent sporeGrowthData = sporeGrowthEntity.getSporeGrowthData();
                    return sporeGrowthData.getAge() == 0
                            && sporeGrowthData.isInitialGrowth() == isInitial
                            && (sporeGrowthData.getStage() == 1) == isSmall;
                });
        if (nearbySporeGrowthEntities.isEmpty()) {
            return false;
        }
        CrimsonSporeGrowthEntity existingSporeGrowthEntity = nearbySporeGrowthEntities.get(0);
        SporeGrowthComponent sporeGrowthData = existingSporeGrowthEntity.getSporeGrowthData();
        sporeGrowthData.setSpores(sporeGrowthData.getSpores() + spores);
        sporeGrowthData.setWater(sporeGrowthData.getWater() + water);
        return true;
    }

    @Override
    public Item getBottledItem() {
        return ModItems.CRIMSON_SPORES_BOTTLE;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.CRIMSON_SPORES_BUCKET;
    }

    @Override
    public Block getFluidBlock() {
        return ModBlocks.CRIMSON_SPORE_SEA;
    }

    @Override
    public Block getSolidBlock() {
        return ModBlocks.CRIMSON_SPORE_BLOCK;
    }

    @Override
    public FlowableFluid getFluid() {
        return ModFluids.CRIMSON_SPORES;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return ModStatusEffects.CRIMSON_SPORES;
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
        return ModBlocks.CRIMSON_GROWTH.getDefaultState().with(
                ModProperties.CATALYZED, true);
    }
}
