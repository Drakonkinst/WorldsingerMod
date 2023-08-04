package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.Fluidlogged;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateManager.Builder.class)
public abstract class StateManagerBuilderMixin<O, S extends State<O, S>> {
    @Shadow
    public abstract StateManager.Builder<O, S> add(Property<?>... properties);

    @Inject(method = "add", at = @At("HEAD"))
    private void injectFluidloggedProperty(Property<?>[] properties, CallbackInfoReturnable<StateManager.Builder<O, S>> cir) {
        for (Property<?> property : properties) {
            if (property.equals(Properties.WATERLOGGED)) {
                add(Fluidlogged.PROPERTY_FLUID);
                break;
            }
        }
    }
}
