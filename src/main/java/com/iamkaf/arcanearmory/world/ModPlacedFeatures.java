package com.iamkaf.arcanearmory.world;

import com.iamkaf.arcanearmory.ArcaneArmory;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

import java.util.List;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> RUBY_ORE_PLACED_KEY = registerKey(
            "ruby_ore_placed");
    public static final RegistryKey<PlacedFeature> NETHER_RUBY_ORE_PLACED_KEY = registerKey(
            "nether_ruby_ore_placed");
    public static final RegistryKey<PlacedFeature> END_RUBY_ORE_PLACED_KEY = registerKey(
            "end_ruby_ore_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        // HeightRangePlacementModifier.uniform defines where the highest concentration of ore is
        // placed. Uniform here means evenly distributed, trapezoid would have more ore in the
        // middle.

        register(
                context,
                RUBY_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.RUBY_ORE_KEY),
                ModOrePlacement.modifiersWithCount(
                        12, // veins per chunk
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))
                )
        );
        register(
                context,
                NETHER_RUBY_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.NETHER_RUBY_ORE_KEY),
                ModOrePlacement.modifiersWithCount(
                        12, // veins per chunk
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))
                )
        );
        register(
                context,
                END_RUBY_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.END_RUBY_ORE_KEY),
                ModOrePlacement.modifiersWithCount(
                        12, // veins per chunk
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))
                )
        );
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, ArcaneArmory.ID(name));
    }

    private static void register(
            Registerable<PlacedFeature> context,
            RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers
    ) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
