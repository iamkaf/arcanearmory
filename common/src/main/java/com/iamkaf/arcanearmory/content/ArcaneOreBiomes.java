package com.iamkaf.arcanearmory.content;

import java.util.List;
import java.util.Map;

/**
 * Overworld ores that generate only in some biomes. This is the one list behind the generated biome
 * tags that the Forge and NeoForge biome modifiers read, Fabric's biome selection, and the Forge
 * 1.18.2 world generation hook. Every biome here exists from 1.18.2 on.
 */
public final class ArcaneOreBiomes {
    public static final Map<String, List<String>> BY_ORE = Map.of(
            "frost_diamond", List.of(
                    "minecraft:snowy_plains",
                    "minecraft:ice_spikes",
                    "minecraft:snowy_taiga",
                    "minecraft:snowy_beach",
                    "minecraft:grove",
                    "minecraft:snowy_slopes",
                    "minecraft:jagged_peaks",
                    "minecraft:frozen_peaks",
                    "minecraft:frozen_river",
                    "minecraft:frozen_ocean",
                    "minecraft:deep_frozen_ocean"
            ),
            "solarflare_gem", List.of(
                    "minecraft:desert",
                    "minecraft:badlands",
                    "minecraft:wooded_badlands",
                    "minecraft:eroded_badlands"
            ),
            "aquamarine", List.of(
                    "minecraft:ocean",
                    "minecraft:deep_ocean",
                    "minecraft:warm_ocean",
                    "minecraft:lukewarm_ocean",
                    "minecraft:deep_lukewarm_ocean",
                    "minecraft:cold_ocean",
                    "minecraft:deep_cold_ocean",
                    "minecraft:frozen_ocean",
                    "minecraft:deep_frozen_ocean",
                    "minecraft:beach",
                    "minecraft:snowy_beach",
                    "minecraft:lush_caves"
            )
    );

    private ArcaneOreBiomes() {
    }

    /** The biome tag, in this mod's namespace, that lists where an ore may generate. */
    public static String tag(String ore) {
        return "has_" + ore + "_ore";
    }

    /** The placed feature that generates an ore in the Overworld. */
    public static String placedFeature(String ore) {
        return ore + "_ore_placed";
    }
}
