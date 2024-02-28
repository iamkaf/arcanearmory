package com.iamkaf.arcanearmory.material.custom;

import com.iamkaf.arcanearmory.material.AAMaterialBuilder;
import com.iamkaf.arcanearmory.material.AAMaterial;

public class SapphireMaterial {
    public final static AAMaterialBuilder config = AAMaterialBuilder.copyOf(
            RubyMaterial.config,
            "sapphire"
    );

    public static AAMaterial get() {
        return config.build();
    }
}
