package com.iamkaf.arcanearmory.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent COTTON_CANDY = new FoodComponent.Builder()
            .hunger(4)
            .saturationModifier(0.3f)
            .build();


    public static final FoodComponent SPECIAL_CHOCOLATE_COOKIE = new FoodComponent.Builder()
            .hunger(6)
            .saturationModifier(0.4f)
            .alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 1), 1f)
            .build();
    public static final FoodComponent EXTRA_SPECIAL_CHOCOLATE_COOKIE = new FoodComponent.Builder()
            .hunger(6)
            .saturationModifier(0.4f)
            .alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 2), 1f)
            .build();
}
