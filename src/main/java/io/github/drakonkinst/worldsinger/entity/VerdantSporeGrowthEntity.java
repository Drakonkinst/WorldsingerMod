package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VerdantSporeGrowthEntity extends AbstractSporeGrowthEntity {

    public VerdantSporeGrowthEntity(EntityType<?> entityType,
            World world) {
        super(entityType, world);
    }

    @Override
    protected BlockState getNextBlock() {
        return ModBlocks.VERDANT_VINE_BLOCK.getDefaultState();
    }

    @Override
    protected int getWeight(World world, BlockPos pos, Int3 direction) {
        BlockState state = world.getBlockState(pos);

        if (state.isIn(ModBlockTags.SPORES_CAN_GROW)) {
            if (direction.y() > 0) {
                return 2;
            }
            return 1;
        }
        return 0;
    }

    @Override
    protected int getUpdatePeriod() {
        return 5;
    }

    @Override
    protected boolean shouldShowParticles() {
        return true;
    }
}
