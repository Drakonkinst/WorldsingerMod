package io.github.drakonkinst.worldsinger.effect;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ThirstStatusEffect extends StatusEffect {

    private final float drainMultiplier;

    protected ThirstStatusEffect(float drainMultiplier, int color) {
        super(StatusEffectCategory.HARMFUL, color);
        this.drainMultiplier = drainMultiplier;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        if (entity instanceof PlayerEntity player) {
            ModComponents.THIRST_MANAGER.get(player)
                    .addDehydration(drainMultiplier * (float) (amplifier + 1));
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
