package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.*;
import com.iamkaf.arcanearmory.material.util.MiningSpeed;
import com.iamkaf.arcanearmory.material.util.ToolDurability;

public class AethericCrystalMaterial {
    public static final AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(CoolpperMaterial.config, "aetheric_crystal")
            .veinsPerChunk(6)
            .type(AAMaterialType.CRYSTAL)
            .armorDurability(400)
            .toolDurability(ToolDurability.WOOD)
            .miningSpeed(MiningSpeed.WOOD)
            .enchantability(40);

    public static AAMaterial get() {
        return config.build();
    }
}
