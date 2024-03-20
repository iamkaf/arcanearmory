package com.iamkaf.arcanearmory.item.factory;

import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.ModArmorMaterial;
import com.iamkaf.arcanearmory.material.config.MaterialNamingUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

public class ArmorFactory {
    /**
     * Creates and registers a full set of armor based on the provided material and namer.
     *
     * @param material The material to use for the armor.
     * @param namer    A namer to generate the ids of the items.
     * @return The armor set.
     */
    public static ArmorSet createFullSet(ModArmorMaterial material, MaterialNamingUtil namer) {

        return new ArmorSet(
                createHelmet(namer.helmet(), material),
                createChestplate(namer.chestplate(), material),
                createLeggings(namer.leggings(), material),
                createBoots(namer.boots(), material)
        );
    }

    /**
     * Creates a helmet ArmorItem and registers it.
     *
     * @param name          The namespace of the item.
     * @param armorMaterial
     * @return The helmet created.
     */
    public static ArmorItem createHelmet(String name, ModArmorMaterial armorMaterial) {
        var item = new ArmorItem(armorMaterial, ArmorItem.Type.HELMET, new Item.Settings());
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a chestplate ArmorItem and registers it.
     *
     * @param name          The namespace of the item.
     * @param armorMaterial
     * @return The chestplate created.
     */
    public static ArmorItem createChestplate(String name, ModArmorMaterial armorMaterial) {
        var item = new ArmorItem(armorMaterial, ArmorItem.Type.CHESTPLATE, new Item.Settings());
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a leggings ArmorItem and registers it.
     *
     * @param name          The namespace of the item.
     * @param armorMaterial
     * @return The leggings created.
     */
    public static ArmorItem createLeggings(String name, ModArmorMaterial armorMaterial) {
        var item = new ArmorItem(armorMaterial, ArmorItem.Type.LEGGINGS, new Item.Settings());
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a boots ArmorItem and registers it.
     *
     * @param name          The namespace of the item.
     * @param armorMaterial
     * @return The boots created.
     */
    public static ArmorItem createBoots(String name, ModArmorMaterial armorMaterial) {
        var item = new ArmorItem(armorMaterial, ArmorItem.Type.BOOTS, new Item.Settings());
        ModItems.registerItem(name, item);
        return item;
    }

    public record ArmorSet(ArmorItem helmet, ArmorItem chestplate, ArmorItem leggings,
                           ArmorItem boots) {
    }
}
