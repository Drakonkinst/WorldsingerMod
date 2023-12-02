package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.fluid.FluidProperty;
import net.minecraft.block.enums.Thickness;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;

public final class ModProperties {

    public static final FluidProperty FLUIDLOGGED = FluidProperty.of("fluidlogged");
    public static final BooleanProperty CATALYZED = BooleanProperty.of("catalyzed");
    public static final EnumProperty<Thickness> DISCRETE_THICKNESS = EnumProperty.of("thickness",
            Thickness.class, thickness -> thickness != Thickness.TIP_MERGE);
    public static final IntProperty SUNLIGHT_LEVEL = IntProperty.of("level", 1, 3);

    public static void initialize() {}

    private ModProperties() {}
}
