package com.iamkaf.arcanearmory.content;

import net.minecraft.world.item.Item;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
*///?}
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
    /*@Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(this.repairItem);
    }

    // Before 1.21.2, vanilla puts only the vanilla shield on cooldown when an axe disables a shield.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(Items.SHIELD)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }
    *///?}
}
