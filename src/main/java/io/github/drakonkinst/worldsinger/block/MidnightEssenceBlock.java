package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.ThirstManagerComponent;
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

    private static final int WATER_COST = 4;

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
        ThirstManagerComponent thirstManager = ModComponents.THIRST_MANAGER.get(player);
        if (thirstManager.get() < WATER_COST) {
            // Not enough water to summon anything, but should still swing hand
            return ActionResult.success(true);
        }
        thirstManager.remove(WATER_COST);
        world.removeBlock(pos, false);
        MidnightCreatureEntity entity = new MidnightCreatureEntity(world);
        entity.setMidnightEssenceAmount(1);
        entity.setPosition(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        entity.setController(player);
        world.spawnEntity(entity);
        return ActionResult.success(true);
    }
}
