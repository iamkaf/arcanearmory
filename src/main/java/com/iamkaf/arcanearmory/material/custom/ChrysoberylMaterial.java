package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import com.iamkaf.arcanearmory.material.util.Enchantability;
import com.iamkaf.arcanearmory.material.util.ToolDurability;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.sound.SoundEvents;

public class ChrysoberylMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .create(AAMaterialType.GEM, "chrysoberyl")
            .miningLevelRequired(MiningLevels.IRON)
            .veinsPerChunk(2)
            .veinSize(6)
            .minYOffset(-12)
            .maxYOffset(12)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(true)
            .spawnInTheNether(false)
            .spawnInTheEnd(false)
            .armorDurability(1200)
            .toolDurability(ToolDurability.IRON)
            .protection(new int[]{2,4,3,2})
            .toughness(0)
            .knockbackResistance(0f)
            .enchantability(Enchantability.IRON)
            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON)
            .swordDamage(2)
            .swordSwingSpeed(1.6f)
            .axeDamage(4)
            .axeSwingSpeed(1f)
            .bonusDamage(2)
            .miningLevel(MiningLevels.IRON)
            .miningSpeed(6)
            .createOre(true)
            .createArmor(true)
            .createTools(true)
            .createWeapons(true);

    public static AAMaterial get() {
        return config.build();
    }
}
