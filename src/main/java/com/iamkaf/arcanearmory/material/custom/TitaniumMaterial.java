package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import com.iamkaf.arcanearmory.material.util.Enchantability;
import com.iamkaf.arcanearmory.material.util.ToolDurability;
import com.iamkaf.arcanearmory.material.util.Toughness;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.sound.SoundEvents;

public class TitaniumMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .create(AAMaterialType.INGOT, "titanium")
            .miningLevelRequired(MiningLevels.IRON)
            .veinsPerChunk(4)
            .veinSize(6)
            .minYOffset(-64)
            .maxYOffset(42)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(true)
            .spawnInTheNether(false)
            .spawnInTheEnd(false)
            .armorDurability(1400)
            .protection(new int[]{2,8,5,2})
            .toughness(Toughness.DIAMOND)
            .knockbackResistance(0f)
            .enchantability(Enchantability.DIAMOND)
            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON)
            .toolDurability(1200)
            .swordDamage(1)
            .swordSwingSpeed(1.6f)
            .axeDamage(4)
            .axeSwingSpeed(1f)
            .bonusDamage(5)
            .miningLevel(MiningLevels.DIAMOND)
            .miningSpeed(7)
            .createOre(true)
            .createArmor(true)
            .createTools(true)
            .createWeapons(true);

    public static AAMaterial get() {
        return config.build();
    }
}
