package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import net.fabricmc.yarn.constants.MiningLevels;

public class TopazMaterial {
    public static final AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(CoolpperMaterial.config, "topaz")
            .miningLevelRequired(MiningLevels.IRON)
            .veinsPerChunk(2)
            .veinSize(6)
            .minYOffset(-64)
            .maxYOffset(12)
            .minDrops(1)
            .maxDrops(1)
            .swordDamage(2)
            .axeDamage(4)
            .bonusDamage(4)
            .miningSpeed(6)
            .type(AAMaterialType.GEM);

    public static AAMaterial get() {
        return config.build();
    }
}
