package io.github.drakonkinst.worldsinger.mixin.entity;

import com.mojang.authlib.GameProfile;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityDeathMixin extends PlayerEntity {

    public ServerPlayerEntityDeathMixin(World world, BlockPos pos, float yaw,
            GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void callDeathEvents(DamageSource damageSource, CallbackInfo ci) {
        ModComponents.MIDNIGHT_AETHER_BOND.get(this).onDeath();
    }
}
