package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.fluid.FluidProperty;
import net.minecraft.block.enums.Thickness;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;

public final class ModProperties {

    public static final FluidProperty FLUIDLOGGED = FluidProperty.of("fluidlogged");
    public static final BooleanProperty CATALYZED = BooleanProperty.of("catalyzed");
    public static final EnumProperty<Thickness> THICKNESS_NO_MERGE = EnumProperty.of("thickness",
            Thickness.class, thickness -> thickness != Thickness.TIP_MERGE);

    private ModProperties() {}

    public static void initialize() {}
}
