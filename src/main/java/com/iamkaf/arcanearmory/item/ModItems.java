package com.iamkaf.arcanearmory.item;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.item.custom.SpecialChocolateCookie;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(ArcaneArmory.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ArcaneArmory.LOGGER.info("Registering mod items for " + ArcaneArmory.MOD_ID);
    }
}
