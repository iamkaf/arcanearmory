package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import net.fabricmc.yarn.constants.MiningLevels;

public class BloodfireGarnetMaterial {
    public static final AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(BlackDiamondMaterial.config, "bloodfire_garnet")
            .miningLevelRequired(MiningLevels.DIAMOND)
            .miningLevel(MiningLevels.NETHERITE)
            .veinsPerChunk(6)
            .veinSize(4)
            .toolDurability(2800)
            .miningSpeed(12)
            .minYOffset(-64)
            .maxYOffset(64)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(false)
            .spawnInTheNether(true)
            .spawnInTheEnd(false)
            .type(AAMaterialType.GEM);

    public static AAMaterial get() {
        return config.build();
    }
}
