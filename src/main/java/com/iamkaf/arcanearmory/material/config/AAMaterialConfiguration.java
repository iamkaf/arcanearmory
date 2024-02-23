package com.iamkaf.arcanearmory.material.config;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public class AAMaterialConfiguration {
    public final String name;
    public final Item ingot;
    public final int baseTint;
    public final int overlayTint;
    public final Supplier<Ingredient> repairIngredient;
    public final AAMaterialType type;
    public final AABlockConfiguration oreConfiguration;
    public final AAArmorConfiguration armorConfiguration;
    public final AAToolConfiguration toolConfiguration;
    public final AAGenerationConfiguration generationConfiguration;

    public AAMaterialConfiguration(
            String name,
            Item ingot,
            AAMaterialType type,
            int baseTint,
            int decorationTint,
            AABlockConfiguration oreConfiguration,
            AAArmorConfiguration armorConfiguration,
            AAToolConfiguration toolConfiguration,
            AAGenerationConfiguration generationConfiguration
    ) {
        this.name = name;
        this.ingot = ingot;
        this.type = type;
        this.baseTint = baseTint;
        this.overlayTint = decorationTint;
        this.repairIngredient = () -> Ingredient.ofItems(ingot);
        this.oreConfiguration = oreConfiguration;
        this.armorConfiguration = armorConfiguration;
        this.toolConfiguration = toolConfiguration;
        this.generationConfiguration = generationConfiguration;
    }
}
