package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import net.fabricmc.yarn.constants.MiningLevels;

public class SolarflareGemMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(RubyMaterial.config, "solarflare_gem")
            .miningLevelRequired(MiningLevels.DIAMOND)
            .veinsPerChunk(3)
            .veinSize(3)
            .minYOffset(-64)
            .maxYOffset(-32)
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
