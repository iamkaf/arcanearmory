package com.iamkaf.arcanearmory.material.config;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AAMaterialConfiguration {
    public final String name;
    @Nullable
    public final Text ingotTooltip;
    public final Item ingot;
    public final int baseTint;
    public final int overlayTint;
    public final String armorMaterialName;
    public final Supplier<Ingredient> repairIngredient;
    public final AAMaterialType type;
    public final AABlockConfiguration blockConfiguration;
    public final AAArmorConfiguration armorConfiguration;
    public final AAToolConfiguration toolConfiguration;

    public AAMaterialConfiguration(
            String name,
            Item ingot,
            AAMaterialType type,
            int baseTint,
            int decorationTint,
            AABlockConfiguration blockConfiguration,
            AAArmorConfiguration armorConfiguration,
            AAToolConfiguration toolConfiguration
    ) {
        this.name = name;
        this.ingotTooltip = Text.translatable("tooltip.arcanearmory.material." + name + ".name");
        this.ingot = ingot;
        this.type = type;
        this.baseTint = baseTint;
        this.overlayTint = decorationTint;
        this.armorMaterialName = name;
        this.repairIngredient = () -> Ingredient.ofItems(ingot);
        this.blockConfiguration = blockConfiguration;
        this.armorConfiguration = armorConfiguration;
        this.toolConfiguration = toolConfiguration;
    }
}
