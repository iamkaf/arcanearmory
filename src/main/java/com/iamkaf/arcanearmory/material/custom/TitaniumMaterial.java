package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.AAMaterialConfiguration;
import com.iamkaf.arcanearmory.material.util.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;

public class TitaniumMaterial extends AAMaterial {
    public final static Item MATERIAL = new Item(new FabricItemSettings());
    public static final AAMaterialConfiguration configuration = new AAMaterialConfiguration(
            "titanium",
            TitaniumMaterial.MATERIAL,
            ColorUtil.tint("#173c91"),
            ColorUtil.tint(),
            AttackDamage.of(5),
            ArmorDurability.of(2500),
            ToolDurability.of(1200),
            MiningLevel.of(MiningLevels.DIAMOND),
            MiningSpeed.of(20f),
            Protection.of(1, 2, 3, 4),
            Enchantability.of(42),
            Toughness.of(42),
            KnockbackResistance.of(8f),
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND
    );

    protected AAMaterialConfiguration getConfiguration() {
        return TitaniumMaterial.configuration;
    }
}
