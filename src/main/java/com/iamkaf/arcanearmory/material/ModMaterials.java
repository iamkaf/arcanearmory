package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.custom.*;

import java.util.ArrayList;
import java.util.List;

public class ModMaterials {
    public static final List<AAMaterialAutoload> ALL_MATERIALS = new ArrayList<>();
    public static final AAMaterialAutoload COOLPPER = register(new AAMaterialAutoload(
            CoolpperMaterialAutoload.configuration));
    public static final AAMaterialAutoload RUBY = register(new AAMaterialAutoload(
            RubyMaterialAutoload.configuration));
    public static final AAMaterialAutoload SAPPHIRE = register(new AAMaterialAutoload(
            SapphireMaterialAutoload.configuration));
    public static final AAMaterialAutoload FROST_DIAMOND = register(new AAMaterialAutoload(
            FrostDiamondMaterialAutoload.configuration));
    public static final AAMaterialAutoload BLACK_DIAMOND = register(AAAutoloadMaterialBuilder
            .copyOf(RubyMaterialAutoload.configuration, "black_diamond")
            .knockbackResistance(0.1f)
            .build());
    public static final AAMaterialAutoload AETHERIC_CRYSTAL = register(new AAMaterialAutoload(
            AethericCrystalMaterialAutoload.configuration));
    public static final AAMaterialAutoload TITANIUM = register(new AAMaterialAutoload(
            TitaniumMaterialAutoload.configuration));

    private static AAMaterialAutoload register(AAMaterialAutoload material) {
        ALL_MATERIALS.add(material);
        return material;
    }

    public static void initializeArcaneArmoryMaterials() {
        ArcaneArmory.LOGGER.info("Initializing armor materials for " + ArcaneArmory.MOD_ID);
    }
}
