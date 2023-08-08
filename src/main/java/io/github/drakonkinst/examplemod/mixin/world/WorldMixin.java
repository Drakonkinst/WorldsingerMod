package io.github.drakonkinst.examplemod.mixin.world;

import io.github.drakonkinst.examplemod.world.LumarSeetheAccess;
import io.github.drakonkinst.examplemod.world.LumarSeetheData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
public abstract class WorldMixin implements LumarSeetheAccess {

    @Unique
    private final LumarSeetheData lumarSeetheData = new LumarSeetheData();

    @Unique
    @Override
    public LumarSeetheData examplemod$getLumarSeetheData() {
        return this.lumarSeetheData;
    }

}
