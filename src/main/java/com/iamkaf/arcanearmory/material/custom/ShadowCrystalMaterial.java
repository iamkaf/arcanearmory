package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;

public class ShadowCrystalMaterial {
    public static final AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(AethericCrystalMaterial.config, "shadow_crystal")
            .type(AAMaterialType.CRYSTAL)
            .veinsPerChunk(3)
            .minYOffset(32)
            .maxYOffset(64)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(false)
            .spawnInTheNether(true)
            .spawnInTheEnd(false)
            .createArmor(false)
            .createTools(false)
            .createWeapons(false);

    public static AAMaterial get() {
        return config.build();
    }
}
