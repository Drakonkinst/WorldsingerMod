package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.ShapeContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface WorldShapeContext extends ShapeContext {

    @Nullable World worldsinger$getWorld();
}
