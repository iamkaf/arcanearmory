package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.AAMaterialBuilder;

public class AmberMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(ChrysoberylMaterial.config, "amber")
            .createOre(true)
            .spawnInOverworld(false)
            .spawnInTheNether(false)
            .spawnInTheEnd(false);

    public static AAMaterial get() {
        return config.build();
    }
}
