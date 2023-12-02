package io.github.drakonkinst.worldsinger.fluid;

import net.minecraft.state.property.IntProperty;

public class FluidProperty extends IntProperty {

    public static FluidProperty of(String name) {
        return new FluidProperty(name);
    }

    protected FluidProperty(String name) {
        super(name, 0, Fluidlogged.WATERLOGGABLE_FLUIDS.size());
    }
}
