package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import net.fabricmc.yarn.constants.MiningLevels;

public class VoidObsidianFragmentMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(DoomFragmentMaterial.config, "void_obsidian_fragment")
            .miningLevelRequired(MiningLevels.NETHERITE)
            .veinsPerChunk(8)
            .veinSize(4)
            .minYOffset(0)
            .maxYOffset(64)
            .minDrops(1)
            .maxDrops(1)
            .spawnInOverworld(false)
            .spawnInTheNether(false)
            .spawnInTheEnd(true)
            .createOre(true)
            .createArmor(false)
            .createTools(false)
            .createWeapons(false);

    public static AAMaterial get() {
        return config.build();
    }
}
