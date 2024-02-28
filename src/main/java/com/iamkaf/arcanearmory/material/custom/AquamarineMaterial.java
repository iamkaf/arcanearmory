package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import com.iamkaf.arcanearmory.material.util.Enchantability;
import com.iamkaf.arcanearmory.material.util.ToolDurability;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.sound.SoundEvents;

public class AquamarineMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .create(AAMaterialType.GEM, "aquamarine")
            .miningLevelRequired(MiningLevels.STONE)
            .veinsPerChunk(2)
            .veinSize(6)
            .minYOffset(-22)
            .maxYOffset(32)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(true)
            .spawnInTheNether(false)
            .spawnInTheEnd(false)
            .armorDurability(800)
            .toolDurability(128)
            .protection(new int[]{2,8,3,1})
            .toughness(0)
            .knockbackResistance(0f)
            .enchantability(Enchantability.GOLD)
            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON)
            .swordDamage(1)
            .swordSwingSpeed(1.6f)
            .axeDamage(2)
            .axeSwingSpeed(1f)
            .bonusDamage(2)
            .miningLevel(MiningLevels.IRON)
            .miningSpeed(15)
            .createOre(true)
            .createArmor(true)
            .createTools(true)
            .createWeapons(true);

    public static AAMaterial get() {
        return config.build();
    }
}
