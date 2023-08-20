package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.fluid.FluidProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public final class ModProperties {

    private ModProperties() {}

    public static void initialize() {}
    
    public static final FluidProperty FLUIDLOGGED = FluidProperty.of("fluidlogged");
    public static final DirectionProperty GROWTH_DIRECTION = DirectionProperty.of(
            "growth_direction", Direction.UP, Direction.DOWN);
}
