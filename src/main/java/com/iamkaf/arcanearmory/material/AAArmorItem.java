package com.iamkaf.arcanearmory.material;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;

public class AAArmorItem extends ArmorItem {
    public AAArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public ArmorMaterial getMaterial() {
        // If this returns leather the fabric datagen will add an overlay layer to the armor json
        // which can be tinted through code.
        return ArmorMaterials.LEATHER;
    }

    public ArmorMaterial getTrueMaterial() {
        return this.material;
    }
}
