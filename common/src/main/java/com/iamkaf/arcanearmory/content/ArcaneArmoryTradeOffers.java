package com.iamkaf.arcanearmory.content;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if >=1.21
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public final class ArcaneArmoryTradeOffers {
    private ArcaneArmoryTradeOffers() {
    }

    public static MerchantOffer coolpperAxeForEmeralds() {
        ItemStack coolpperAxe = new ItemStack(ArcaneArmoryContent.item("coolpper_axe").orElseThrow().get());
        //? if >=1.21 {
        return new MerchantOffer(new ItemCost(Items.EMERALD, 2), coolpperAxe, 6, 5, 0.075F);
        //?} else {
        /*return new MerchantOffer(new ItemStack(Items.EMERALD, 2), coolpperAxe, 6, 5, 0.075F);*/
        //?}
    }
}
