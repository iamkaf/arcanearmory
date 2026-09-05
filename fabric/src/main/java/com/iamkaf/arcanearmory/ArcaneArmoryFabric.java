package com.iamkaf.arcanearmory;

import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import com.iamkaf.arcanearmory.content.ArcaneArmoryFuels;
//? if <26
import com.iamkaf.arcanearmory.content.ArcaneArmoryTradeOffers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
//? if <26
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
//? if >=26 {
import net.fabricmc.fabric.api.registry.FuelValueEvents;
//?} else if >=1.21.11 {
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
//?} else {
import net.fabricmc.fabric.api.registry.FuelRegistry;
//?}
//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
import net.minecraft.core.Registry;
//?}
import net.minecraft.resources.ResourceKey;
//? if >=1.21.11 && <26 {
import net.minecraft.world.entity.npc.villager.VillagerProfession;
//?} else if <26 {
import net.minecraft.world.entity.npc.VillagerProfession;
//?}
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ArcaneArmoryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ArcaneArmoryMod.init();
        registerFuels();
        registerTrades();
        registerWorldgen();
    }

    private static void registerTrades() {
        //? if >=1.21.11 && <26 {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 1,
                factories -> factories.add((level, entity, random) -> ArcaneArmoryTradeOffers.coolpperAxeForEmeralds()));
        //?} else if <26 {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.WEAPONSMITH, 1,
                factories -> factories.add((entity, random) -> ArcaneArmoryTradeOffers.coolpperAxeForEmeralds()));
        //?}
    }

    private static void registerFuels() {
        //? if >=26 {
        FuelValueEvents.BUILD.register((builder, context) -> {
            builder.add(
                    ArcaneArmoryContent.item("solarflare_gem").orElseThrow().get(),
                    ArcaneArmoryFuels.SOLARFLARE_GEM_BURN_TIME
            );
            builder.add(
                    ArcaneArmoryContent.item("solarflare_gem_block").orElseThrow().get(),
                    ArcaneArmoryFuels.SOLARFLARE_GEM_BLOCK_BURN_TIME
            );
        });
        //?} else if >=1.21.11 {
        FuelRegistryEvents.BUILD.register((builder, context) -> {
            builder.add(
                    ArcaneArmoryContent.item("solarflare_gem").orElseThrow().get(),
                    ArcaneArmoryFuels.SOLARFLARE_GEM_BURN_TIME
            );
            builder.add(
                    ArcaneArmoryContent.item("solarflare_gem_block").orElseThrow().get(),
                    ArcaneArmoryFuels.SOLARFLARE_GEM_BLOCK_BURN_TIME
            );
        });
        //?} else {
        /*
        FuelRegistry.INSTANCE.add(
                ArcaneArmoryContent.item("solarflare_gem").orElseThrow().get(),
                ArcaneArmoryFuels.SOLARFLARE_GEM_BURN_TIME
        );
        FuelRegistry.INSTANCE.add(
                ArcaneArmoryContent.item("solarflare_gem_block").orElseThrow().get(),
                ArcaneArmoryFuels.SOLARFLARE_GEM_BLOCK_BURN_TIME
        );
        */
        //?}
    }

    private static void registerWorldgen() {
        for (String feature : OVERWORLD_FEATURES) {
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, placed(feature));
        }
        for (String feature : NETHER_FEATURES) {
            BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Decoration.UNDERGROUND_ORES, placed(feature));
        }
        for (String feature : END_FEATURES) {
            BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Decoration.UNDERGROUND_ORES, placed(feature));
        }
    }

    private static ResourceKey<PlacedFeature> placed(String id) {
        //? if >=1.19.3 {
        return ResourceKey.create(Registries.PLACED_FEATURE, ArcaneArmoryConstants.resource(id));
        //?} else {
        /*return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, ArcaneArmoryConstants.resource(id));*/
        //?}
    }

    private static final String[] OVERWORLD_FEATURES = {
            "aetheric_crystal_ore_placed",
            "amber_geode_placed",
            "aquamarine_ore_placed",
            "black_diamond_ore_placed",
            "chrysoberyl_ore_placed",
            "coolpper_ore_placed",
            "frost_diamond_ore_placed",
            "ruby_ore_placed",
            "sapphire_ore_placed",
            "solarflare_gem_ore_placed",
            "star_corundum_ore_placed",
            "titanium_ore_placed",
            "topaz_ore_placed"
    };

    private static final String[] NETHER_FEATURES = {
            "nether_bloodfire_garnet_ore_placed",
            "nether_doom_fragment_ore_placed",
            "nether_shadow_crystal_ore_placed"
    };

    private static final String[] END_FEATURES = {
            "end_void_obsidian_fragment_ore_placed"
    };
}
