package com.iamkaf.arcanearmory.content;

import net.minecraft.world.item.ItemStack;

public final class ArcaneArmoryFuels {
    public static final int SOLARFLARE_GEM_BURN_TIME = 2400;
    public static final int SOLARFLARE_GEM_BLOCK_BURN_TIME = 24000;

    private ArcaneArmoryFuels() {
    }

    public static int burnTime(ItemStack stack) {
        if (stack.is(ArcaneArmoryContent.item("solarflare_gem").orElseThrow().get())) {
            return SOLARFLARE_GEM_BURN_TIME;
        }
        if (stack.is(ArcaneArmoryContent.item("solarflare_gem_block").orElseThrow().get())) {
            return SOLARFLARE_GEM_BLOCK_BURN_TIME;
        }
        return 0;
    }
}
