package com.iamkaf.arcanearmory.material;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public class ModToolMaterial implements ToolMaterial {
    private final String name;
    private final int swordDamage;
    private final int axeDamage;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int miningLevel;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    public ModToolMaterial(
            String name,
            int swordDamage,
            int axeDamage,
            int itemDurability,
            float miningSpeed,
            float attackDamage,
            int miningLevel,
            int enchantability,
            Supplier<Ingredient> repairIngredient
    ) {
        this.name = name;
        this.swordDamage = swordDamage;
        this.axeDamage = axeDamage;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    public int getSwordDamage() {
        return swordDamage;
    }

    public int getAxeDamage() {
        return axeDamage;
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    // this gets applied to as a bonus damage to all tools, it is added on top of the attack
    // damage passed into the tool's constructor
    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}

