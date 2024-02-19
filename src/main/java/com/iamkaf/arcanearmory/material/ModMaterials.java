package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.custom.*;

import java.util.ArrayList;
import java.util.List;

public class ModMaterials {
    public static final List<AAMaterialAutoload> ALL_MATERIALS = new ArrayList<>();
    public static final AAMaterialAutoload COOLPPER = register(new CoolpperMaterialAutoload());
    public static final AAMaterialAutoload RUBY = register(new RubyMaterialAutoload());
    public static final AAMaterialAutoload SAPPHIRE = register(new SapphireMaterialAutoload());
    public static final AAMaterialAutoload AETHERIC_CRYSTAL = register(new AethericCrystalMaterialAutoload());
    public static final AAMaterialAutoload TITANIUM = register(new TitaniumMaterialAutoload());

    private static AAMaterialAutoload register(AAMaterialAutoload material) {
        ALL_MATERIALS.add(material);
        return material;
    }

    public static void initializeArcaneArmoryMaterials() {
        ArcaneArmory.LOGGER.info("Initializing armor materials for " + ArcaneArmory.MOD_ID);
    }
}
