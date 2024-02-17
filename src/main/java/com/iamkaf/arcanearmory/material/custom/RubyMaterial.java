package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.AAMaterialConfiguration;
import com.iamkaf.arcanearmory.material.util.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;

public class RubyMaterial extends AAMaterial {
    public final static Item MATERIAL = new Item(new FabricItemSettings());
    public static final AAMaterialConfiguration configuration = new AAMaterialConfiguration(
            "ruby",
            RubyMaterial.MATERIAL,
            ColorUtil.tint(),
            ColorUtil.tint("#ff0000"),
            AttackDamage.of(3),
            ArmorDurability.of(1815),
            ToolDurability.of(1561),
            MiningLevel.of(MiningLevels.DIAMOND),
            MiningSpeed.of(15f),
            Protection.of(1, 2, 3, 4),
            Enchantability.of(42),
            Toughness.of(42),
            KnockbackResistance.of(8f),
            SoundEvents.ITEM_ARMOR_EQUIP_IRON
    );

    protected AAMaterialConfiguration getConfiguration() {
        return RubyMaterial.configuration;
    }
}
