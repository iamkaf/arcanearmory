package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;

public class BlackDiamondMaterial {
    public static final AAMaterialBuilder config = AAMaterialBuilder
            .copyOf(RubyMaterial.config, "black_diamond")
            .veinSize(4)
            .swordDamage(4)
            .axeDamage(6)
            .armorDurability(2200)
            .toolDurability(1800)
            .protection(new int[]{3,8,6,3})
            .toughness(2f)
            .enchantability(10)
            .miningSpeed(9)
            .type(AAMaterialType.GEM);

    public static AAMaterial get() {
        return config.build();
    }
}
