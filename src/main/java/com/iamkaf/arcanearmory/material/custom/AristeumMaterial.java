package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;

public class AristeumMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(BlackDiamondMaterial.config, "aristeum")
            .type(AAMaterialType.INGOT)
            .createOre(false)
            .spawnInOverworld(false)
            .spawnInTheNether(false)
            .spawnInTheEnd(false);

    public static AAMaterial get() {
        return config.build();
    }
}
