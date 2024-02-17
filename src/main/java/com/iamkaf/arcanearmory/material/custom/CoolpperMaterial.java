package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.AAMaterialConfiguration;
import com.iamkaf.arcanearmory.material.util.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;

public class CoolpperMaterial extends AAMaterial {
    public final static Item MATERIAL = new Item(new FabricItemSettings());
    public static final AAMaterialConfiguration configuration = new AAMaterialConfiguration(
            "coolpper",
            MATERIAL,
            ColorUtil.tint("#9c3710"),
            ColorUtil.tint(),
            AttackDamage.of(3),
            ArmorDurability.of(1815),
            ToolDurability.of(1561),
            MiningLevel.of(MiningLevels.IRON),
            MiningSpeed.of(11),
            Protection.of(1, 2, 3, 4),
            Enchantability.of(42),
            Toughness.of(42),
            KnockbackResistance.of(8f),
            SoundEvents.ITEM_ARMOR_EQUIP_IRON
    );

    protected AAMaterialConfiguration getConfiguration() {
        return CoolpperMaterial.configuration;
    }
}
