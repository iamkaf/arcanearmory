package com.iamkaf.arcanearmory.world.loot;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;

public class ModLootTableModifiers {
    private static final List<Identifier> MATERIAL_CHESTS = List.of(
            new Identifier("minecraft", "chests/abandoned_mineshaft"),
            new Identifier("minecraft", "chests/ancient_city"),
            new Identifier("minecraft", "chests/ancient_city_ice_box"),
            new Identifier("minecraft", "chests/bastion_bridge"),
            new Identifier("minecraft", "chests/bastion_hoglin_stable"),
            new Identifier("minecraft", "chests/bastion_other"),
            new Identifier("minecraft", "chests/bastion_treasure"),
            new Identifier("minecraft", "chests/buried_treasure"),
            new Identifier("minecraft", "chests/desert_pyramid"),
            new Identifier("minecraft", "chests/end_city_treasure"),
            new Identifier("minecraft", "chests/igloo_chest"),
            new Identifier("minecraft", "chests/jungle_temple"),
            new Identifier("minecraft", "chests/nether_bridge"),
            new Identifier("minecraft", "chests/pillager_outpost"),
//            new Identifier("minecraft", "chests/ruined_portal"),
            new Identifier("minecraft", "chests/shipwreck_map"),
            new Identifier("minecraft", "chests/shipwreck_supply"),
            new Identifier("minecraft", "chests/shipwreck_treasure"),
            new Identifier("minecraft", "chests/simple_dungeon"),
            new Identifier("minecraft", "chests/stronghold_corridor"),
            new Identifier("minecraft", "chests/stronghold_crossing"),
//            new Identifier("minecraft", "chests/stronghold_library"),
            new Identifier("minecraft", "chests/underwater_ruin_big"),
            new Identifier("minecraft", "chests/underwater_ruin_small"),
            new Identifier("minecraft", "chests/woodland_mansion"),
            new Identifier("minecraft", "chests/village/village_armorer"),
            new Identifier("minecraft", "chests/village/village_toolsmith"),
            new Identifier("minecraft", "chests/village/village_weaponsmith")
    );

    private static final List<Identifier> EPIC_LOOT_CHESTS = List.of(
            new Identifier("minecraft", "chests/ancient_city"),
            new Identifier("minecraft", "chests/ancient_city_ice_box"),
            new Identifier("minecraft", "chests/bastion_treasure"),
            new Identifier("minecraft", "chests/end_city_treasure")
    );

    public static void modifyLootTables() {

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            List<LootPool> pools = new ArrayList<>();
            MATERIAL_CHESTS.forEach((chestId) -> {
                if (id.equals(chestId)) {
                    ALL_MATERIALS.forEach((aaMaterial -> {
                        if (aaMaterial.blockConfiguration.spawnInOverworld) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.05f))
                                    .with(ItemEntry.builder(aaMaterial.MATERIAL))
                                    .apply(SetCountLootFunction
                                            .builder(UniformLootNumberProvider.create(1.0f, 4.0f))
                                            .build());
                            pools.add(poolBuilder.build());
                        }

                        float enchantedLootChance = 0.0005f;

                        if (aaMaterial.SWORD != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.SWORD))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.SWORD))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.PICKAXE != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.PICKAXE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.PICKAXE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.AXE != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.AXE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.AXE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.SHOVEL != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.SHOVEL))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.SHOVEL))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.HOE != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.HOE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.HOE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.HELMET != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.HELMET))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.HELMET))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.CHESTPLATE != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.CHESTPLATE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.CHESTPLATE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.LEGGINGS != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.LEGGINGS))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.LEGGINGS))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.BOOTS != null) {
                            LootPool.Builder poolBuilder = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(0.001f))
                                    .with(ItemEntry.builder(aaMaterial.BOOTS))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build())
                                    .apply(EnchantWithLevelsLootFunction
                                            .builder(UniformLootNumberProvider.create(20f, 50f))
                                            .build());
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.BOOTS))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder.build());
                            pools.add(poolBuilder2.build());
                        }
                    }));
                }
            });

            EPIC_LOOT_CHESTS.forEach((chestId) -> {
                if (id.equals(chestId)) {
                    ALL_MATERIALS.forEach((aaMaterial -> {
                        float enchantedLootChance = 0.05f;
                        if (aaMaterial.SWORD != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.SWORD))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.PICKAXE != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.PICKAXE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.AXE != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.AXE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.SHOVEL != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.SHOVEL))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.HOE != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.HOE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.HELMET != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.HELMET))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.CHESTPLATE != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.CHESTPLATE))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.LEGGINGS != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.LEGGINGS))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                        if (aaMaterial.BOOTS != null) {
                            LootPool.Builder poolBuilder2 = LootPool
                                    .builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .conditionally(RandomChanceLootCondition.builder(
                                            enchantedLootChance))
                                    .with(ItemEntry.builder(aaMaterial.BOOTS))
                                    .apply(SetCountLootFunction
                                            .builder(ConstantLootNumberProvider.create(1))
                                            .build());
                            pools.add(poolBuilder2.build());
                        }
                    }));
                }
            });

            tableBuilder.pools(pools);
        });
    }
}
