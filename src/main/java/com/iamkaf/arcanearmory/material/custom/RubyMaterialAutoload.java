package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialAutoload;
import com.iamkaf.arcanearmory.material.config.*;
import com.iamkaf.arcanearmory.material.util.ColorUtil;
import com.iamkaf.arcanearmory.material.util.Enchantability;
import com.iamkaf.arcanearmory.material.util.ToolDurability;
import com.iamkaf.arcanearmory.material.util.Toughness;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;

public class RubyMaterialAutoload extends AAMaterialAutoload {
    public final static Item MATERIAL = new Item(new FabricItemSettings());
    public static final AAMaterialConfiguration configuration = new AAMaterialConfiguration("ruby",
            RubyMaterialAutoload.MATERIAL,
            AAMaterialType.GEM,
            ColorUtil.tint("#ff0000"),
            ColorUtil.tint("#00ff00"),
            new AABlockConfiguration(1),
            new AAArmorConfiguration(1815,
                    new int[]{1, 2, 3, 4},
                    Toughness.DIAMOND,
                    3f,
                    Enchantability.DIAMOND,
                    SoundEvents.ITEM_ARMOR_EQUIP_IRON
            ),
            new AAToolConfiguration(ToolDurability.DIAMOND,
                    6,
                    2f,
                    8,
                    1.5f,
                    4,
                    MiningLevels.DIAMOND,
                    12,
                    Enchantability.DIAMOND
            )
    );

    protected AAMaterialConfiguration getConfiguration() {
        return RubyMaterialAutoload.configuration;
    }
}
