package com.iamkaf.arcanearmory.content;

import com.iamkaf.arcanearmory.ArcaneArmoryConstants;

import java.util.List;

/**
 * Arcane materials that work as armor trim materials from 1.20. Each trim's palette lives at
 * {@code textures/palettes/trim/<id>.png}, ordered lightest first like vanilla's.
 */
public final class ArcaneTrimMaterials {
    public static final List<Trim> ALL = List.of(
            trim("ruby", 0xFC6B77),
            trim("sapphire", 0x43B7F3),
            trim("frost_diamond", 0xB6EAD9),
            trim("black_diamond", 0x5E5E5E),
            trim("topaz", 0x88BCF4),
            trim("chrysoberyl", 0xADC058),
            trim("aquamarine", 0x7EC1D3),
            trim("star_corundum", 0x0074C3),
            trim("solarflare_gem", 0xD47A28),
            trim("bloodfire_garnet", 0xAD2623),
            trim("aetheric_crystal", 0xC3DFD6),
            trim("shadow_crystal", 0x5B5B5B),
            trim("coolpper", 0xD5944D),
            trim("titanium", 0xB7B18E),
            trim("amber", 0xF2C378),
            trim("arcanthium", 0xF27AA4),
            trim("voidium", 0xBC00E0)
    );

    private ArcaneTrimMaterials() {
    }

    private static Trim trim(String materialId, int color) {
        ArcaneMaterial material = ArcaneMaterials.ALL.stream()
                .filter(candidate -> candidate.id().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown Arcane material " + materialId));
        return new Trim(material, color);
    }

    /** One trim material, named after its Arcane material and made from that material's gem or ingot. */
    public record Trim(ArcaneMaterial material, int color) {
        public String id() {
            return material.id();
        }

        public String ingredientId() {
            return material.materialItemId();
        }

        /** Vanilla names trim sprites after this suffix, so it carries the mod id to stay unique. */
        public String assetName() {
            return ArcaneArmoryConstants.MOD_ID + "_" + material.id();
        }
    }
}
