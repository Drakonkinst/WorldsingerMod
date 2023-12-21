package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MidnightEssenceBlock extends Block {

    public MidnightEssenceBlock(Settings settings) {
        super(settings);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
            BlockHitResult hit) {
        // TODO: Should not always succeed
        world.removeBlock(pos, false);
        MidnightCreatureEntity entity = new MidnightCreatureEntity(world);
        entity.setPosition(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        world.spawnEntity(entity);
        return ActionResult.success(true);
    }
}
