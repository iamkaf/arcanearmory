package com.iamkaf.arcanearmory.world;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.AAMaterialAutoload;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.HashMap;
import java.util.List;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;

public class ModConfiguredFeatures {

    // ! This code assumes an ore cannot generate in more than one dimension.

    public static final HashMap<AAMaterialAutoload, RegistryKey<ConfiguredFeature<?, ?>>> ALL_KEYS = makeKeys();

    private static HashMap<AAMaterialAutoload, RegistryKey<ConfiguredFeature<?, ?>>> makeKeys() {
        HashMap<AAMaterialAutoload, RegistryKey<ConfiguredFeature<?, ?>>> allKeys = new HashMap<>();

        for (AAMaterialAutoload material : ALL_MATERIALS) {
            if (material.blockConfiguration.spawnInOverworld) {
                allKeys.put(material, registerKey(material.name + "_ore"));
            }
            if (material.blockConfiguration.spawnInTheNether) {
                allKeys.put(material, registerKey("nether_" + material.name + "_ore"));
            }
            if (material.blockConfiguration.spawnInTheEnd) {
                allKeys.put(material, registerKey("end_" + material.name + "_ore"));
            }
        }

        return allKeys;
    }

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplacables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplacables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplacables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
        RuleTest endReplacables = new BlockMatchRuleTest(Blocks.END_STONE);

        ALL_KEYS.forEach((material, key) -> {
            if (material.blockConfiguration.spawnInOverworld) {
                List<OreFeatureConfig.Target> ores = List.of(OreFeatureConfig.createTarget(stoneReplacables,
                                material.ORE.getDefaultState()
                        ),
                        OreFeatureConfig.createTarget(deepslateReplacables,
                                material.DEEPSLATE_ORE.getDefaultState()
                        )
                );
                register(context,
                        key,
                        Feature.ORE,
                        new OreFeatureConfig(ores, material.blockConfiguration.veinSize)
                );
            }
            if (material.blockConfiguration.spawnInTheNether) {
                List<OreFeatureConfig.Target> ores = List.of(OreFeatureConfig.createTarget(netherReplacables,
                        material.ORE.getDefaultState()
                ));
                register(context,
                        key,
                        Feature.ORE,
                        new OreFeatureConfig(ores, material.blockConfiguration.veinSize)
                );
            }
            if (material.blockConfiguration.spawnInTheEnd) {
                List<OreFeatureConfig.Target> ores = List.of(OreFeatureConfig.createTarget(endReplacables,
                        material.ORE.getDefaultState()
                ));
                register(context,
                        key,
                        Feature.ORE,
                        new OreFeatureConfig(ores, material.blockConfiguration.veinSize)
                );
            }
        });

    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, ArcaneArmory.ID(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> context,
            RegistryKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
