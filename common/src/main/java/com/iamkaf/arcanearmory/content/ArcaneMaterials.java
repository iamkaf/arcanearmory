package com.iamkaf.arcanearmory.content;

import java.util.List;

public final class ArcaneMaterials {
    public static final List<ArcaneMaterial> ALL = List.of(
            gem("ruby", "Ruby", true, true, 1561),
            gem("sapphire", "Sapphire", true, false, 1561),
            gem("frost_diamond", "Frost Diamond", false, false, 1561),
            gem("black_diamond", "Black Diamond", true, false, 1561),
            gem("topaz", "Topaz", true, false, 1561),
            gem("chrysoberyl", "Chrysoberyl", true, false, 250),
            gem("aquamarine", "Aquamarine", true, false, 32),
            gem("star_corundum", "Star Corundum", true, false, 1561),
            gem("doom_fragment", "Doom Fragment", false, false, 1561),
            gem("void_obsidian_fragment", "Void Obsidian Fragment", false, false, 1561),
            gem("solarflare_gem", "Solarflare Gem", false, false, 1561),
            gem("bloodfire_garnet", "Bloodfire Garnet", true, false, 1561),
            crystal("aetheric_crystal", "Aetheric Crystal", true, 1561),
            crystal("shadow_crystal", "Shadow Crystal", false, 1561),
            ingot("coolpper", "Coolpper", true, true, true, 131),
            ingot("titanium", "Titanium", true, true, true, 1561),
            gem("amber", "Amber", true, false, 250),
            ingot("aristeum", "Aristeum", false, true, true, 1561),
            ingot("voidium", "Voidium", false, true, true, 2000)
    );

    private ArcaneMaterials() {
    }

    private static ArcaneMaterial gem(String id, String displayName, boolean tools, boolean shield, int toolDurability) {
        boolean armor = tools;
        return new ArcaneMaterial(id, displayName, false, true, tools, armor, shield, toolDurability);
    }

    private static ArcaneMaterial crystal(String id, String displayName, boolean tools, int toolDurability) {
        return new ArcaneMaterial(id, displayName, false, true, tools, tools, false, toolDurability);
    }

    private static ArcaneMaterial ingot(String id, String displayName, boolean ore, boolean tools, boolean shield, int toolDurability) {
        return new ArcaneMaterial(id, displayName, true, ore, tools, tools, shield, toolDurability);
    }
}
