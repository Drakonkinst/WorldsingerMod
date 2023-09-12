package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeKillingManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class LivingAetherSporeFluidBlock extends AetherSporeFluidBlock implements SporeKillable {

    public LivingAetherSporeFluidBlock(FlowableFluid fluid, AetherSporeType aetherSporeType,
            Settings settings) {
        super(fluid, aetherSporeType, settings);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (SporeKillingManager.isSporeKillingBlockNearby(world, pos)) {
            world.setBlockState(pos, SporeKillingManager.convertToDeadVariant(this, state));
        }
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_SPORE_SEA_BLOCK;
    }
}
