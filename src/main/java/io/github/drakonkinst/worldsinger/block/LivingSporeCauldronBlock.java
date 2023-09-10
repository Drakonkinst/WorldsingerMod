package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LivingSporeCauldronBlock extends SporeCauldronBlock implements SporeKillable,
        WaterReactiveBlock {

    private static final int CATALYZE_VALUE = 250;

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
                    CATALYZE_VALUE,
                    waterAmount, true, false, false);
        }
        return true;
    }
}
