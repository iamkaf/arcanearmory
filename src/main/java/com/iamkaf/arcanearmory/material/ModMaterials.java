package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.custom.CoolpperMaterial;
import com.iamkaf.arcanearmory.material.custom.RubyMaterial;
import com.iamkaf.arcanearmory.material.custom.TitaniumMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.ArrayList;
import java.util.List;

public class ModMaterials {
    public static final List<AAMaterial> ALL_MATERIALS = new ArrayList<>();
    public static final AAMaterial COOLPPER = register(new CoolpperMaterial());
    public static final AAMaterial RUBY = register(new RubyMaterial());
    public static final AAMaterial TITANIUM = register(new TitaniumMaterial());

    public static void addItemsToItemGroup(ItemGroup.Entries entries) {
        for (AAMaterial material : ALL_MATERIALS) {
            List<Item> items = material.getItemsToAddToItemGroup();

            for (Item item : items) {
                entries.add(item);
            }
        }
    }

    private static AAMaterial register(AAMaterial material) {
        ALL_MATERIALS.add(material);
        return material;
    }

    public static void initializeArcaneArmoryMaterials() {
        ArcaneArmory.LOGGER.info("Initializing armor materials for " + ArcaneArmory.MOD_ID);
    }
}
