package com.iamkaf.arcanearmory.world;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.ModMaterials;
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

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> RUBY_ORE_KEY = registerKey("ruby_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_RUBY_ORE_KEY = registerKey(
            "nether_ruby_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> END_RUBY_ORE_KEY = registerKey(
            "end_ruby_ore");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplacables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplacables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplacables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
        RuleTest endReplacables = new BlockMatchRuleTest(Blocks.END_STONE);

        List<OreFeatureConfig.Target> overworldRubyOres = List.of(OreFeatureConfig.createTarget(
                        stoneReplacables,
                        ModMaterials.RUBY.ORE.getDefaultState()
                ),
                OreFeatureConfig.createTarget(deepslateReplacables,
                        ModMaterials.RUBY.DEEPSLATE_ORE.getDefaultState()
                )
        );

        List<OreFeatureConfig.Target> netherRubyOres = List.of(OreFeatureConfig.createTarget(netherReplacables,
                ModMaterials.RUBY.ORE.getDefaultState()
        ));

        List<OreFeatureConfig.Target> endRubyOres = List.of(OreFeatureConfig.createTarget(endReplacables,
                ModMaterials.RUBY.ORE.getDefaultState()
        ));

        register(context, RUBY_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldRubyOres, 12));
        register(context,
                NETHER_RUBY_ORE_KEY,
                Feature.ORE,
                new OreFeatureConfig(netherRubyOres, 12)
        );
        register(context, END_RUBY_ORE_KEY, Feature.ORE, new OreFeatureConfig(endRubyOres, 12));
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
