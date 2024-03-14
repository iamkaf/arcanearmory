package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import net.fabricmc.yarn.constants.MiningLevels;

public class VoidiumMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(AristeumMaterial.config, "voidium")
            .type(AAMaterialType.INGOT)
            .miningLevel(MiningLevels.NETHERITE + 1)
            .protection(new int[]{4,10,8,4})
            .armorDurability(3000)
            .toolDurability(2000)
            .miningSpeed(13)
            .enchantability(15)
            .createOre(false)
            .spawnInOverworld(false)
            .spawnInTheNether(false)
            .spawnInTheEnd(false);

    public static AAMaterial get() {
        return config.build();
    }
}
