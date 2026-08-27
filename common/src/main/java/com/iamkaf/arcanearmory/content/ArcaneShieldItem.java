package com.iamkaf.arcanearmory.content;

import net.minecraft.world.item.Item;
//? if <1.21.2
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class ArcaneShieldItem extends ShieldItem {
    private final int enchantmentValue;
    private final Item repairItem;

    public ArcaneShieldItem(int enchantmentValue, Item repairItem, Properties properties) {
        super(properties);
        this.enchantmentValue = enchantmentValue;
        this.repairItem = repairItem;
    }

    //? if <1.21.2 {
    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(this.repairItem);
    }
    //?}
}
