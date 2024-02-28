package com.iamkaf.arcanearmory.world;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.AAMaterial;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;

public class ModPlacedFeatures {

    // ! This code assumes an ore cannot generate in more than one dimension.

    public static final HashMap<AAMaterial, RegistryKey<PlacedFeature>> ALL_KEYS = makeAllKeys();

    private static HashMap<AAMaterial, RegistryKey<PlacedFeature>> makeAllKeys() {
        HashMap<AAMaterial, RegistryKey<PlacedFeature>> allKeys = new HashMap<>();

        for (AAMaterial material : ALL_MATERIALS) {
            if (material.generate.ore) {
                if (material.blockConfiguration.spawnInOverworld) {
                    allKeys.put(material, registerKey(material.name + "_ore_placed"));
                }
                if (material.blockConfiguration.spawnInTheNether) {
                    allKeys.put(material, registerKey("nether_" + material.name + "_ore_placed"));
                }
                if (material.blockConfiguration.spawnInTheEnd) {
                    allKeys.put(material, registerKey("end_" + material.name + "_ore_placed"));
                }
            }
        }

        return allKeys;
    }

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        // HeightRangePlacementModifier.uniform defines where the highest concentration of ore is
        // placed. Uniform here means evenly distributed, trapezoid would have more ore in the
        // middle.

        ALL_KEYS.forEach((material, key) -> {
            register(
                    context,
                    key,
                    configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.ALL_KEYS.get(
                            material)),
                    ModOrePlacement.modifiersWithCount(
                            material.blockConfiguration.veinsPerChunk,
                            HeightRangePlacementModifier.uniform(
                                    YOffset.fixed(material.blockConfiguration.minYOffset),
                                    YOffset.fixed(material.blockConfiguration.maxYOffset)
                            )
                    )
            );
        });
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, ArcaneArmory.ID(name));
    }

    private static void register(
            @NotNull Registerable<PlacedFeature> context,
            RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers
    ) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
