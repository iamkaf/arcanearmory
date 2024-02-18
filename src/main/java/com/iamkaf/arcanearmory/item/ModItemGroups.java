package com.iamkaf.arcanearmory.item;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.AAMaterialDatagen;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup ARCANE_ARMORY_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(ArcaneArmory.MOD_ID, "arcanearmory"),
            FabricItemGroup
                    .builder()
                    .displayName(Text.translatable("itemgroup.arcanearmory"))
                    .icon(() -> new ItemStack(ModItems.SPECIAL_CHOCOLATE_COOKIE))
                    .entries(((displayContext, entries) -> {
                        AAMaterialDatagen.addItemsToItemGroup(entries);
                    }))
                    .build()
    );

    public static void registerItemGroups() {
        ArcaneArmory.LOGGER.info("Registering Item Groups for " + ArcaneArmory.MOD_ID);
    }
}
