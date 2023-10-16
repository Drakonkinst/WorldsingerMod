package io.github.drakonkinst.worldsinger.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.cauldron.CauldronBehavior.CauldronBehaviorMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome.Precipitation;

public class SporeCauldronBlock extends LeveledCauldronBlock implements SporeEmitting {

    // Unused Codec
    public static final MapCodec<SporeCauldronBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            LeveledCauldronBlock.createSettingsCodec(),
                            CauldronBehavior.CODEC.fieldOf("interactions")
                                    .forGetter(block -> block.behaviorMap),
                            AetherSporeType.CODEC.fieldOf("sporeType").forGetter(
                                    SporeCauldronBlock::getSporeType))
                    .apply(instance, SporeCauldronBlock::new));

    protected final AetherSporeType sporeType;

    public SporeCauldronBlock(Settings settings,
            CauldronBehaviorMap behaviorMap, AetherSporeType sporeType) {
        super(Precipitation.NONE, behaviorMap, settings);
        this.sporeType = sporeType;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && this.isEntityTouchingFluid(state, pos,
                entity)) {
            if (entity.isOnFire()) {
                entity.extinguish();
            }
        }
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity,
            float fallDistance) {
        super.onLandedUpon(world, state, pos, entity, fallDistance);
        if (!world.isClient() && this.isEntityTouchingFluid(state, pos,
                entity)) {
            if (world instanceof ServerWorld serverWorld) {
                SporeParticleSpawner.spawnBlockParticles(serverWorld, sporeType, pos, 0.6,
                        Math.min(fallDistance, 3.0));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return Items.CAULDRON.getDefaultStack();
    }

    @Override
    public AetherSporeType getSporeType() {
        return sporeType;
    }

    // @Override
    // public MapCodec<LeveledCauldronBlock> getCodec() {
    //     return super.getCodec();
    // }
}
