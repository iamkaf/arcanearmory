package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.*;
import net.fabricmc.yarn.constants.MiningLevels;

public class FrostDiamondMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .create(AAMaterialType.GEM, "frost_diamond")
            .miningLevelRequired(MiningLevels.IRON)
            .veinsPerChunk(2)
            .veinSize(3)
            .minYOffset(-64)
            .maxYOffset(16)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(true)
            .createOre(true)
            .createArmor(false)
            .createTools(false)
            .createWeapons(false);

    public static AAMaterial get() {
        return config.build();
    }
}
