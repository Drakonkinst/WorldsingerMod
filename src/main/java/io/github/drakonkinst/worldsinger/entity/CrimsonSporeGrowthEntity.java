package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.util.math.Int3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrimsonSporeGrowthEntity extends SporeGrowthEntity {

    public CrimsonSporeGrowthEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected BlockState getNextBlock() {
        return null;
    }

    @Override
    protected int getWeight(World world, BlockPos pos, Int3 direction, boolean allowPassthrough) {
        return 0;
    }

    @Override
    protected int getUpdatePeriod() {
        return 0;
    }

    @Override
    protected void updateStage() {

    }

    @Override
    protected int getMaxStage() {
        return 0;
    }

    @Override
    protected void onGrowBlock(BlockPos pos, BlockState state) {

    }

    @Override
    protected boolean canBreakHere(BlockState state, @Nullable BlockState replaceWith) {
        return false;
    }

    @Override
    protected boolean canGrowHere(BlockState state, @Nullable BlockState replaceWith) {
        return false;
    }

    @Override
    protected boolean isGrowthBlock(BlockState state) {
        return false;
    }
}
