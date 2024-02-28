package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import com.iamkaf.arcanearmory.material.util.MiningSpeed;
import net.fabricmc.yarn.constants.MiningLevels;

public class StarCorundumMaterial {
    public static final AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(BlackDiamondMaterial.config, "star_corundum")
            .miningLevelRequired(MiningLevels.DIAMOND)
            .veinsPerChunk(1)
            .veinSize(8)
            .toolDurability(2400)
            .miningSpeed(MiningSpeed.DIAMOND)
            .swordDamage(3)
            .axeDamage(5)
            .minYOffset(-64)
            .maxYOffset(24)
            .minDrops(1)
            .maxDrops(1)
            .type(AAMaterialType.GEM);

    public static AAMaterial get() {
        return config.build();
    }
}
