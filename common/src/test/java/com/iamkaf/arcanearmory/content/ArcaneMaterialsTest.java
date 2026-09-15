package com.iamkaf.arcanearmory.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArcaneMaterialsTest {
    @Test
    void representativeMaterialsPreserveTheirOriginalToolStats() {
        assertToolStats("aetheric_crystal", 59, 1, 2, 3, 1, 2, 40, 47, 5);
        assertToolStats("ruby", 1561, 2, 4, 4, 3, 8, 10, 1248, 8);
        assertToolStats("voidium", 2000, 4, 6, 4, 5, 13, 15, 1600, 10);
    }

    @Test
    void representativeMaterialsPreserveTheirOriginalArmorStats() {
        assertArmorStats("aetheric_crystal", 400, 1, 3, 2, 1, 40, 0, 80, 116, 109, 94);
        assertArmorStats("ruby", 1815, 3, 8, 6, 3, 10, 0, 363, 528, 495, 429);
        assertArmorStats("voidium", 3000, 4, 10, 8, 4, 15, 2, 600, 872, 818, 709);
    }

    @Test
    void shieldsAndRepairMaterialsFollowTheMaterialDefinition() {
        ArcaneMaterial aetheric = material("aetheric_crystal");
        ArcaneMaterial ruby = material("ruby");
        ArcaneMaterial voidium = material("voidium");

        assertFalse(aetheric.shield());
        assertEquals("aetheric_crystal", aetheric.materialItemId());

        assertTrue(ruby.shield());
        assertEquals(1404, ruby.shieldDurability());
        assertEquals("ruby", ruby.materialItemId());

        assertTrue(voidium.shield());
        assertEquals(1800, voidium.shieldDurability());
        assertEquals("voidium_ingot", voidium.materialItemId());
    }

    private static void assertToolStats(String id, int durability, float swordDamage, float axeDamage,
            float attackDamageBonus, int miningLevel, float miningSpeed, int enchantmentValue,
            int bowDurability, float bowDamage) {
        ArcaneMaterial material = material(id);

        assertEquals(new ArcaneMaterial.ToolStats(
                durability,
                swordDamage,
                axeDamage,
                attackDamageBonus,
                miningLevel,
                miningSpeed,
                enchantmentValue
        ), material.requireToolStats());
        assertEquals(bowDurability, material.bowDurability());
        assertEquals(bowDamage, material.bowDamage());
    }

    private static void assertArmorStats(String id, int baseDurability, int helmetProtection,
            int chestplateProtection, int leggingsProtection, int bootsProtection, int enchantmentValue,
            float toughness, int helmetDurability, int chestplateDurability, int leggingsDurability,
            int bootsDurability) {
        ArcaneMaterial material = material(id);
        ArcaneMaterial.ArmorStats stats = material.requireArmorStats();

        assertEquals(new ArcaneMaterial.ArmorStats(
                baseDurability,
                helmetProtection,
                chestplateProtection,
                leggingsProtection,
                bootsProtection,
                enchantmentValue,
                toughness,
                0
        ), stats);
        assertEquals(helmetDurability, stats.durability(ArcaneMaterial.ArmorPiece.HELMET));
        assertEquals(chestplateDurability, stats.durability(ArcaneMaterial.ArmorPiece.CHESTPLATE));
        assertEquals(leggingsDurability, stats.durability(ArcaneMaterial.ArmorPiece.LEGGINGS));
        assertEquals(bootsDurability, stats.durability(ArcaneMaterial.ArmorPiece.BOOTS));
    }

    private static ArcaneMaterial material(String id) {
        return ArcaneMaterials.ALL.stream()
                .filter(material -> material.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing material " + id));
    }
}
