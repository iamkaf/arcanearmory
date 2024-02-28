package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import com.iamkaf.arcanearmory.material.util.Enchantability;
import com.iamkaf.arcanearmory.material.util.MiningSpeed;
import com.iamkaf.arcanearmory.material.util.ToolDurability;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.sound.SoundEvents;

public class CoolpperMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .create(AAMaterialType.INGOT, "coolpper")
            .miningLevelRequired(MiningLevels.WOOD)
            .veinsPerChunk(8)
            .veinSize(8)
            .minYOffset(0)
            .maxYOffset(64)
            .minDrops(1)
            .maxDrops(3)
            .spawnInOverworld(true)
            .spawnInTheNether(false)
            .spawnInTheEnd(false)
            .armorDurability(825)
            .toolDurability(ToolDurability.IRON)
            .protection(new int[]{1, 3, 2, 1})
            .toughness(0)
            .knockbackResistance(0f)
            .enchantability(Enchantability.GOLD)
            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON)
            .swordDamage(1)
            .swordSwingSpeed(1.6f)
            .axeDamage(2)
            .axeSwingSpeed(1f)
            .bonusDamage(3)
            .miningLevel(MiningLevels.STONE)
            .miningSpeed(MiningSpeed.STONE)
            .createOre(true)
            .createArmor(true)
            .createTools(true)
            .createWeapons(true);

    public static AAMaterial get() {
        return config.build();
    }
}
