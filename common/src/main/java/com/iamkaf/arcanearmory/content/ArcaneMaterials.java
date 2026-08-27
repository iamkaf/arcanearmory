package com.iamkaf.arcanearmory.content;

import java.util.List;

public final class ArcaneMaterials {
    public static final List<ArcaneMaterial> ALL = List.of(
            equipment("ruby", "Ruby", false, true, true,
                    tool(1561, 2, 4, 4, 3, 8, 10),
                    armor(1815, 3, 8, 6, 3, 10, 0, 0)),
            equipment("sapphire", "Sapphire", false, true, false,
                    tool(1561, 2, 4, 4, 3, 8, 10),
                    armor(1815, 3, 8, 6, 3, 10, 0, 0)),
            resource("frost_diamond", "Frost Diamond", false, true),
            equipment("black_diamond", "Black Diamond", false, true, false,
                    tool(1800, 4, 6, 4, 3, 9, 10),
                    armor(2200, 3, 8, 6, 3, 10, 2, 0)),
            equipment("topaz", "Topaz", false, true, false,
                    tool(250, 2, 4, 4, 1, 6, 22),
                    armor(825, 1, 3, 2, 1, 22, 0, 0)),
            equipment("chrysoberyl", "Chrysoberyl", false, true, false,
                    tool(250, 2, 4, 2, 2, 6, 14),
                    armor(1200, 2, 4, 3, 2, 14, 0, 0)),
            equipment("aquamarine", "Aquamarine", false, true, false,
                    tool(128, 1, 2, 2, 2, 15, 22),
                    armor(800, 2, 8, 3, 1, 22, 0, 0)),
            equipment("star_corundum", "Star Corundum", false, true, false,
                    tool(2400, 3, 5, 4, 3, 8, 10),
                    armor(2200, 3, 8, 6, 3, 10, 2, 0)),
            resource("doom_fragment", "Doom Fragment", false, true),
            resource("void_obsidian_fragment", "Void Obsidian Fragment", false, true),
            resource("solarflare_gem", "Solarflare Gem", false, true),
            equipment("bloodfire_garnet", "Bloodfire Garnet", false, true, false,
                    tool(2800, 4, 6, 4, 4, 12, 10),
                    armor(2200, 3, 8, 6, 3, 10, 2, 0)),
            equipment("aetheric_crystal", "Aetheric Crystal", false, true, false,
                    tool(59, 1, 2, 3, 1, 2, 40),
                    armor(400, 1, 3, 2, 1, 40, 0, 0)),
            resource("shadow_crystal", "Shadow Crystal", false, true),
            equipment("coolpper", "Coolpper", true, true, true,
                    tool(250, 1, 2, 3, 1, 4, 22),
                    armor(825, 1, 3, 2, 1, 22, 0, 0)),
            equipment("titanium", "Titanium", true, true, true,
                    tool(1200, 1, 4, 5, 3, 7, 10),
                    armor(1400, 2, 8, 5, 2, 10, 2, 0)),
            equipment("amber", "Amber", false, true, false,
                    tool(250, 2, 4, 2, 2, 6, 14),
                    armor(1200, 2, 4, 3, 2, 14, 0, 0)),
            equipment("aristeum", "Aristeum", true, false, true,
                    tool(1800, 4, 6, 4, 3, 9, 10),
                    armor(2200, 3, 8, 6, 3, 10, 2, 0)),
            equipment("voidium", "Voidium", true, false, true,
                    tool(2000, 4, 6, 4, 5, 13, 15),
                    armor(3000, 4, 10, 8, 4, 15, 2, 0))
    );

    private ArcaneMaterials() {
    }

    private static ArcaneMaterial resource(String id, String displayName, boolean ingot, boolean ore) {
        return new ArcaneMaterial(id, displayName, ingot, ore, false, null, null);
    }

    private static ArcaneMaterial equipment(String id, String displayName, boolean ingot, boolean ore, boolean shield,
            ArcaneMaterial.ToolStats toolStats, ArcaneMaterial.ArmorStats armorStats) {
        return new ArcaneMaterial(id, displayName, ingot, ore, shield, toolStats, armorStats);
    }

    private static ArcaneMaterial.ToolStats tool(int durability, float swordDamage, float axeDamage, float attackDamageBonus,
            int miningLevel, float miningSpeed, int enchantmentValue) {
        return new ArcaneMaterial.ToolStats(durability, swordDamage, axeDamage, attackDamageBonus,
                miningLevel, miningSpeed, enchantmentValue);
    }

    private static ArcaneMaterial.ArmorStats armor(int baseDurability, int helmetProtection, int chestplateProtection,
            int leggingsProtection, int bootsProtection, int enchantmentValue, float toughness, float knockbackResistance) {
        return new ArcaneMaterial.ArmorStats(baseDurability, helmetProtection, chestplateProtection,
                leggingsProtection, bootsProtection, enchantmentValue, toughness, knockbackResistance);
    }
}
