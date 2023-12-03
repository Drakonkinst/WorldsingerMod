package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RoseiteSporeGrowthEntity extends SporeGrowthEntity {

    public static final int MAX_STAGE = 1;

    public RoseiteSporeGrowthEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected int getMaxStage() {
        return MAX_STAGE;
    }

    @Override
    protected int getUpdatePeriod() {
        // TODO
        return 5;
    }

    @Override
    protected BlockState getNextBlock() {
        // TODO
        return null;
    }

    @Override
    protected void updateStage() {
        // TODO
    }

    @Override
    protected boolean canBreakHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_BREAK);
    }

    @Override
    protected boolean canGrowHere(BlockState state, @Nullable BlockState replaceWith) {
        return state.isIn(ModBlockTags.SPORES_CAN_GROW);
    }

    @Override
    protected int getWeight(World world, BlockPos pos, Int3 direction, boolean allowPassthrough) {
        // TODO
        return 0;
    }

    @Override
    protected boolean isGrowthBlock(BlockState state) {
        // TODO
        return false;
    }

    @Override
    protected void onGrowBlock(BlockPos pos, BlockState state) {
        // TODO
    }
}
