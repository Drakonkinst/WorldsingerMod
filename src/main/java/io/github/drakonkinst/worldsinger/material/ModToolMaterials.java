package io.github.drakonkinst.worldsinger.material;

import com.google.common.base.Suppliers;
import io.github.drakonkinst.worldsinger.item.ModItems;
import java.util.function.Supplier;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum ModToolMaterials implements ToolMaterial {

    // Steel is identical to Iron, but has more durability and slightly more enchantability
    STEEL(MiningLevels.IRON, 484, 6.5f, 2.0f, 16, () -> Ingredient.ofItems(ModItems.STEEL_INGOT)),
    // Silver is similar to Gold, but does not mine as fast and has more durability
    // Not planned to support a full toolset for silver
    SILVER(MiningLevels.WOOD, 181, 6.0F, 0.0F, 22, () -> Ingredient.ofItems(ModItems.SILVER_INGOT));

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage,
            int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
    }

    @Override
    public int getDurability() {
        return itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return miningLevel;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
