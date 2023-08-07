package io.github.drakonkinst.examplemod.mixin.world;

import io.github.drakonkinst.examplemod.world.LumarSeetheManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickLumarSeetheManager(CallbackInfo ci) {
        LumarSeetheManager.tick();
    }
}
