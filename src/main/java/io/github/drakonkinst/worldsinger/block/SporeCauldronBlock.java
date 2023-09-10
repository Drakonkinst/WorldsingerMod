package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleSpawner;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SporeCauldronBlock extends LeveledCauldronBlock implements SporeEmitting {

    protected final AetherSporeType sporeType;

    public SporeCauldronBlock(Settings settings,
            Map<Item, CauldronBehavior> behaviorMap, AetherSporeType sporeType) {
        super(settings, precipitation -> false, behaviorMap);
        this.sporeType = sporeType;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && this.isEntityTouchingFluid(state, pos,
                entity)) {
            if (entity.isOnFire()) {
                entity.extinguish();
            }
            entity.extinguish();
            if (world.getRandom().nextInt(10) == 0 && world instanceof ServerWorld serverWorld) {
                SporeParticleSpawner.spawnBlockParticles(serverWorld, sporeType, pos, 0.6, 1.0);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultStack();
    }

    @Override
    public AetherSporeType getSporeType() {
        return sporeType;
    }
}
