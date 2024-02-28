package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.*;
import com.iamkaf.arcanearmory.material.util.Enchantability;
import com.iamkaf.arcanearmory.material.util.ToolDurability;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.sound.SoundEvents;

public class RubyMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .create(AAMaterialType.GEM, "ruby")
            .miningLevelRequired(MiningLevels.IRON)
            .veinsPerChunk(2)
            .veinSize(6)
            .minYOffset(-64)
            .maxYOffset(12)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(true)
            .spawnInTheNether(false)
            .spawnInTheEnd(false)
            .armorDurability(1815)
            .toolDurability(ToolDurability.DIAMOND)
            .protection(new int[]{3,8,6,3})
            .toughness(0)
            .knockbackResistance(0f)
            .enchantability(Enchantability.DIAMOND)
            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON)
            .swordDamage(2)
            .swordSwingSpeed(1.6f)
            .axeDamage(4)
            .axeSwingSpeed(1f)
            .bonusDamage(4)
            .miningLevel(MiningLevels.DIAMOND)
            .miningSpeed(8)
            .createOre(true)
            .createArmor(true)
            .createTools(true)
            .createWeapons(true);

    public static AAMaterial get() {
        return config.build();
    }
}
