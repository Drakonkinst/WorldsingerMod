package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.LumarSeetheManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class LivingAetherSporeFluidBlock extends AetherSporeFluidBlock implements SporeKillable {

    private final Block deadSporeBlock;

    public LivingAetherSporeFluidBlock(FlowableFluid fluid, Block deadSporeBlock,
            Settings settings) {
        super(fluid, settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!LumarSeetheManager.areSporesFluidized(world)) {
            return;
        }

        super.randomTick(state, world, pos, random);
    }
}
