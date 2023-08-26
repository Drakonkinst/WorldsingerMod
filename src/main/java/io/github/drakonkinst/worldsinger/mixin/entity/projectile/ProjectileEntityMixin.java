package io.github.drakonkinst.worldsinger.mixin.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity {

    public ProjectileEntityMixin(EntityType<?> type,
            World world) {
        super(type, world);
    }

    // @Inject(method = "onBlockHit", at = @At("RETURN"))
    // private void addLandingParticles(BlockHitResult blockHitResult, CallbackInfo ci) {
    //     BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
    //
    //     if (blockState == null) {
    //         return;
    //     }
    //
    //     if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
    //         return;
    //     }
    //
    //     FluidState fluidState = blockState.getFluidState();
    //     if (fluidState.isIn(ModFluidTags.AETHER_SPORES)
    //             && fluidState.getFluid() instanceof AetherSporeFluid aetherSporeFluid) {
    //         SporeParticleManager.spawnHitParticles(serverWorld, aetherSporeFluid.getSporeType(),
    //                 this);
    //     } else if (blockState.isIn(ModBlockTags.AETHER_SPORE_BLOCKS)
    //             && blockState.getBlock() instanceof AetherSporeBlock aetherSporeBlock) {
    //         SporeParticleManager.spawnHitParticles(serverWorld, aetherSporeBlock.getSporeType(),
    //                 this);
    //     }
    // }
}
