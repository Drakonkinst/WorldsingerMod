package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class RoseiteClusterBlock extends AmethystClusterBlock {

    public RoseiteClusterBlock(float height, float xzOffset, Settings settings) {
        super(height, xzOffset, settings);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit,
            ProjectileEntity projectile) {
        // Do nothing, cancel out the amethyst sound
    }
}
