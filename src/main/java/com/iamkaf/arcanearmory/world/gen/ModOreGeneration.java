package com.iamkaf.arcanearmory.world.gen;

import com.iamkaf.arcanearmory.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class ModOreGeneration {

    // ! This code assumes an ore cannot generate in more than one dimension.

    public static void generateOres() {
        ModPlacedFeatures.ALL_KEYS.forEach((material, key) -> {
            if (material.blockConfiguration.spawnInOverworld) {
                BiomeModifications.addFeature(
                        BiomeSelectors.foundInOverworld(),
                        GenerationStep.Feature.UNDERGROUND_ORES,
                        key
                );
            }
            if (material.blockConfiguration.spawnInTheNether) {
                BiomeModifications.addFeature(
                        BiomeSelectors.foundInTheNether(),
                        GenerationStep.Feature.UNDERGROUND_ORES,
                        key
                );
            }
            if (material.blockConfiguration.spawnInTheEnd) {
                BiomeModifications.addFeature(
                        BiomeSelectors.foundInTheEnd(),
                        GenerationStep.Feature.UNDERGROUND_ORES,
                        key
                );
            }
        });
    }
}
