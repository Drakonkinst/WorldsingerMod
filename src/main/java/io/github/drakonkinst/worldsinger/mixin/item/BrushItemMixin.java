package io.github.drakonkinst.worldsinger.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.drakonkinst.worldsinger.block.AetherSporeBlock;
import io.github.drakonkinst.worldsinger.cosmere.lumar.AetherSpores;
import io.github.drakonkinst.worldsinger.cosmere.lumar.SporeParticleSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public abstract class BrushItemMixin extends Item {

    public BrushItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;)V"))
    private void spawnParticlesWhenBrushingSporeBlock(World world, LivingEntity user,
            ItemStack stack, int remainingUseTicks, CallbackInfo ci, @Local BlockPos blockPos) {
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            BlockState state = world.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block instanceof AetherSporeBlock aetherSporeBlock) {
                AetherSpores sporeType = aetherSporeBlock.getSporeType();
                SporeParticleSpawner.spawnBrushParticles(serverWorld, sporeType, blockPos);
            }
        }
    }
}
