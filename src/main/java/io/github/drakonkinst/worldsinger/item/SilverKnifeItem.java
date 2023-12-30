package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.entity.SilverVulnerable;
import io.github.drakonkinst.worldsinger.material.ModToolMaterials;
import io.github.drakonkinst.worldsinger.mixin.accessor.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class SilverKnifeItem extends KnifeItem {

    public static final float SILVER_BONUS_DAMAGE = 6.0f;

    public SilverKnifeItem(float attackDamage, float attackSpeed, Settings settings) {
        super(attackDamage, attackSpeed, ModToolMaterials.SILVER, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof SilverVulnerable) {
            // applyDamage() always applies the damage, versus damage() which only damages the mob
            // with the highest damage value that frame. So this is ideal for bonus damage
            ((LivingEntityAccessor) target).worldsinger$applyDamage(
                    attacker.getDamageSources().mobAttack(attacker), SILVER_BONUS_DAMAGE);
        }
        return super.postHit(stack, target, attacker);
    }
}
