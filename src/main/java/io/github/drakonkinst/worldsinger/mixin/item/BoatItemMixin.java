package io.github.drakonkinst.worldsinger.mixin.item;

import io.github.drakonkinst.worldsinger.api.ModApi;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import io.github.drakonkinst.worldsinger.entity.SilverLinedEntityData;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.SilverLined;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.BoatEntity.Type;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatItem.class)
public abstract class BoatItemMixin extends Item implements SilverLined {

    public BoatItemMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    protected abstract BoatEntity createEntity(World world, HitResult hitResult);

    @Shadow
    @Final
    private Type type;
    @Unique
    private int silverDurability;

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BoatItem;createEntity(Lnet/minecraft/world/World;Lnet/minecraft/util/hit/HitResult;)Lnet/minecraft/entity/vehicle/BoatEntity;"), cancellable = true)
    private void addSilverDataToEntity(World world, PlayerEntity user, Hand hand,
            CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        // This overwrites the portion of the original function that spawns the boat to add
        // silver data as well. We need access to both the itemStack and the entity, so this is
        // probably the best way to do it.

        // Need to perform raycast again since we can't capture locals, but if we've gotten this
        // far then we know it's good
        BlockHitResult hitResult = BoatItem.raycast(world, user, RaycastContext.FluidHandling.ANY);

        ItemStack boatItemStack = user.getStackInHand(hand);
        BoatEntity boatEntity = this.createEntity(world, hitResult);
        boatEntity.setVariant(this.type);
        boatEntity.setYaw(user.getYaw());

        this.copySilverDataFromItemToEntity(boatItemStack, boatEntity);

        if (!world.isSpaceEmpty(boatEntity, boatEntity.getBoundingBox())) {
            cir.setReturnValue(TypedActionResult.fail(boatItemStack));
            return;
        }
        if (!world.isClient) {
            world.spawnEntity(boatEntity);
            world.emitGameEvent(user, GameEvent.ENTITY_PLACE, hitResult.getPos());
            if (!user.getAbilities().creativeMode) {
                boatItemStack.decrement(1);
            }
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        cir.setReturnValue(TypedActionResult.success(boatItemStack, world.isClient()));
    }

    @Unique
    private void copySilverDataFromItemToEntity(ItemStack itemStack, BoatEntity entity) {
        SilverLined silverItemData = ModApi.SILVER_LINED_ITEM.find(itemStack, null);
        if (silverItemData == null) {
            ModConstants.LOGGER.error("Expected to find silver data for boat item");
            return;
        }
        int silverDurability = silverItemData.worldsinger$getSilverDurability();
        if (silverDurability <= 0) {
            return;
        }
        SilverLinedComponent silverEntityData = ModComponents.SILVER_LINED_ENTITY.get(entity);
        silverEntityData.setSilverDurability(silverDurability);
    }

    @Override
    public void worldsinger$setSilverDurability(int durability) {
        this.silverDurability = durability;
    }

    @Override
    public int worldsinger$getSilverDurability() {
        return silverDurability;
    }

    @Override
    public int worldsinger$getMaxSilverDurability() {
        return SilverLinedEntityData.MAX_BOAT_SILVER_DURABILITY;
    }
}
