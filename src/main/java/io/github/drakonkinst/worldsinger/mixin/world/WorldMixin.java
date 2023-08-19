package io.github.drakonkinst.worldsinger.mixin.world;

import io.github.drakonkinst.worldsinger.world.LumarSeetheAccess;
import io.github.drakonkinst.worldsinger.world.LumarSeetheData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
public abstract class WorldMixin implements LumarSeetheAccess {

    @Unique
    private final LumarSeetheData lumarSeetheData = new LumarSeetheData();

    @Unique
    @Override
    public LumarSeetheData worldsinger$getLumarSeetheData() {
        return this.lumarSeetheData;
    }

}
