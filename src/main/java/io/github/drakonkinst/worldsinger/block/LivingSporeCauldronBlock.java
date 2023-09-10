package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LivingSporeCauldronBlock extends SporeCauldronBlock implements SporeKillable,
        WaterReactiveBlock {

    private static final int CATALYZE_VALUE_PER_LEVEL = 80;

    public LivingSporeCauldronBlock(Settings settings, Map<Item, CauldronBehavior> behaviorMap,
            AetherSporeType sporeType) {
        super(settings, behaviorMap, sporeType);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_SPORE_CAULDRON;
    }

    @Override
    public boolean canReactToWater(BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }
    }

    @Override
    public boolean reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random) {
        BlockPos abovePos = pos.up();
        BlockState aboveBlockState = world.getBlockState(abovePos);
        if (!aboveBlockState.isAir() && !aboveBlockState.isIn(ModBlockTags.SPORES_CAN_GROW)
                && !aboveBlockState.isIn(ModBlockTags.SPORES_CAN_BREAK)) {
            return false;
        }
        world.setBlockState(pos, Blocks.CAULDRON.getStateWithProperties(state));
        if (sporeType == AetherSporeType.VERDANT) {
            SporeGrowthSpawner.spawnVerdantSporeGrowth(world, abovePos.toCenterPos(),
                    CATALYZE_VALUE_PER_LEVEL * state.get(LEVEL),
                    waterAmount, true, false, false);
        }
        return true;
    }
}
