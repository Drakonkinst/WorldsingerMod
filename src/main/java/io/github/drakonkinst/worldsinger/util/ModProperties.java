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
    public static final IntProperty LEVEL_5 = IntProperty.of("level", 0, 5);

    private ModProperties() {}

    public static void initialize() {}
}
