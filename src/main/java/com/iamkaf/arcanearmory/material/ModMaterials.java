package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.custom.*;

import java.util.ArrayList;
import java.util.List;

public class ModMaterials {
    public static final List<AAMaterial> ALL_MATERIALS = new ArrayList<>();

    // GEMS
    public static final AAMaterial RUBY = register(RubyMaterial.get());
    public static final AAMaterial SAPPHIRE = register(SapphireMaterial.get());
    public static final AAMaterial FROST_DIAMOND = register(FrostDiamondMaterial.get());
    public static final AAMaterial BLACK_DIAMOND = register(BlackDiamondMaterial.get());
    public static final AAMaterial TOPAZ = register(TopazMaterial.get());
    public static final AAMaterial CHRYSOBERYL = register(ChrysoberylMaterial.get());
    public static final AAMaterial AQUAMARINE = register(AquamarineMaterial.get());
    public static final AAMaterial STAR_CORUNDUM = register(StarCorundumMaterial.get());
    public static final AAMaterial DOOM_FRAGMENT = register(DoomFragmentMaterial.get());
    public static final AAMaterial VOID_OBSIDIAN_FRAGMENT = register(VoidObsidianFragmentMaterial.get());
    public static final AAMaterial SOLARFLARE_GEM = register(SolarflareGemMaterial.get());
    public static final AAMaterial BLOODFIRE_GARNET = register(BloodfireGarnetMaterial.get());

    // CRYSTALS
    public static final AAMaterial AETHERIC_CRYSTAL = register(AethericCrystalMaterial.get());
    public static final AAMaterial SHADOW_CRYSTAL = register(ShadowCrystalMaterial.get());

    // METALS
    public static final AAMaterial COOLPPER = register(CoolpperMaterial.get());
    public static final AAMaterial TITANIUM = register(TitaniumMaterial.get());

    // ALLOYS
    public static final AAMaterial AMBER = register(AmberMaterial.get());
    public static final AAMaterial ARISTEUM = register(AristeumMaterial.get());
    public static final AAMaterial VOIDIUM = register(VoidiumMaterial.get());

    private static AAMaterial register(AAMaterial material) {
        ALL_MATERIALS.add(material);
        return material;
    }

    public static void initializeArcaneArmoryMaterials() {
        ArcaneArmory.LOGGER.info("Initializing materials for " + ArcaneArmory.MOD_ID);
    }
}
