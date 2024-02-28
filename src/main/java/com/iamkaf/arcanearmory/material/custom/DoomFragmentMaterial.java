package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import net.fabricmc.yarn.constants.MiningLevels;

public class DoomFragmentMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .create(AAMaterialType.CRYSTAL, "doom_fragment")
            .miningLevelRequired(MiningLevels.DIAMOND)
            .veinsPerChunk(2)
            .veinSize(3)
            .minYOffset(0)
            .maxYOffset(64)
            .minDrops(1)
            .maxDrops(3)
            .spawnInOverworld(false)
            .spawnInTheNether(true)
            .spawnInTheEnd(false)
            .createOre(true)
            .createArmor(false)
            .createTools(false)
            .createWeapons(false);

    public static AAMaterial get() {
        return config.build();
    }
}
