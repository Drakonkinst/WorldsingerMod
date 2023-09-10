package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.item.SporeBottleItem;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// TODO: Add dispenser logic
public class ThrownSporeBottleEntity extends ThrownItemEntity implements FlyingItemEntity {

    public ThrownSporeBottleEntity(
            EntityType<? extends ThrownSporeBottleEntity> entityType,
            World world) {
        super(entityType, world);
    }

    public ThrownSporeBottleEntity(World world, LivingEntity owner) {
        super(ModEntityTypes.THROWN_SPORE_BOTTLE, owner, world);
    }

    public ThrownSporeBottleEntity(World world, double x, double y, double z) {
        super(ModEntityTypes.THROWN_SPORE_BOTTLE, x, y, z, world);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.getWorld().isClient()) {
            return;
        }
        // TODO
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld().isClient()) {
            return;
        }
        // TODO
        Vec3d pos = hitResult.getPos();
        // SporeParticleSpawner.
        this.getWorld()
                .playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.ENTITY_SPLASH_POTION_BREAK,
                        SoundCategory.NEUTRAL, 1.0f, random.nextFloat() * 0.1f + 0.9f,
                        this.getWorld().getRandom().nextLong());
        this.discard();
    }

    private AetherSporeType getSporeType() {
        ItemStack stack = this.getStack();
        if (stack.getItem() instanceof SporeBottleItem sporeBottleItem) {
            return sporeBottleItem.getSporeType();
        }
        return null;
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.DEAD_SPORES_SPLASH_BOTTLE;
    }
}
