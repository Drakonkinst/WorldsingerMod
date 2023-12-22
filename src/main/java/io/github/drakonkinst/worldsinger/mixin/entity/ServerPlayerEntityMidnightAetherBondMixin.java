package io.github.drakonkinst.worldsinger.mixin.entity;

import com.mojang.authlib.GameProfile;
import io.github.drakonkinst.worldsinger.entity.data.MidnightAetherBondAccess;
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
public abstract class ServerPlayerEntityMidnightAetherBondMixin extends PlayerEntity implements
        MidnightAetherBondAccess {

    public ServerPlayerEntityMidnightAetherBondMixin(World world, BlockPos pos, float yaw,
            GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    // To listen for death on the server-side, must use this class since it does not call its super
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void breakBondsOnDeath(DamageSource damageSource, CallbackInfo ci) {
        worldsinger$getMidnightAetherBondData().onDeath();
    }
}
