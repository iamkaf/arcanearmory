package com.iamkaf.arcanearmory.material;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AAMaterialConfiguration {
    public final String name;
    public final String ingotName;
    @Nullable
    public final Text ingotTooltip;
    public final Item ingot;
    public final int baseTint;
    public final int decorationTint;

    public final int armorDurability;
    public final String armorMaterialName;
    public final int toolDurability;
    public final int miningLevel;
    public final float miningSpeed;
    public final float attackDamage;
    public final int[] protectionAmounts;
    public final int enchantability;
    public final SoundEvent equipSound;
    public final float toughness;
    public final float knockbackResistance;
    public final Supplier<Ingredient> repairIngredient;

    public AAMaterialConfiguration(
            String name,
            Item ingot,
            int baseTint,
            int decorationTint,
            float attackDamage,
            int armorDurability,
            int toolDurability,
            int miningLevel,
            float miningSpeed,
            int[] protectionAmounts,
            int enchantability,
            float toughness,
            float knockbackResistance,
            SoundEvent equipSound
    ) {
        this.name = name;
        this.ingotName = name;
        this.ingotTooltip = Text.translatable("tooltip.arcanearmory.material." + name + ".ingot");
        this.ingot = ingot;
        this.baseTint = baseTint;
        this.decorationTint = decorationTint;
        this.armorDurability = armorDurability;
        this.armorMaterialName = name;
        this.toolDurability = toolDurability;
        this.miningLevel = miningLevel;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = () -> Ingredient.ofItems(ingot);
    }
}
