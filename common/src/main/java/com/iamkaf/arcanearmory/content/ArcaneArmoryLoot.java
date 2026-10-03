package com.iamkaf.arcanearmory.content;

import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
*///?} else {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
//?}

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/** Adds Arcane Armory materials and gear to vanilla chests with the original release's odds. */
public final class ArcaneArmoryLoot {
    private static final List<String> MATERIAL_CHESTS = List.of(
            "minecraft:chests/abandoned_mineshaft",
            "minecraft:chests/ancient_city",
            "minecraft:chests/ancient_city_ice_box",
            "minecraft:chests/bastion_bridge",
            "minecraft:chests/bastion_hoglin_stable",
            "minecraft:chests/bastion_other",
            "minecraft:chests/bastion_treasure",
            "minecraft:chests/buried_treasure",
            "minecraft:chests/desert_pyramid",
            "minecraft:chests/end_city_treasure",
            "minecraft:chests/igloo_chest",
            "minecraft:chests/jungle_temple",
            "minecraft:chests/nether_bridge",
            "minecraft:chests/pillager_outpost",
            "minecraft:chests/shipwreck_map",
            "minecraft:chests/shipwreck_supply",
            "minecraft:chests/shipwreck_treasure",
            "minecraft:chests/simple_dungeon",
            "minecraft:chests/stronghold_corridor",
            "minecraft:chests/stronghold_crossing",
            "minecraft:chests/underwater_ruin_big",
            "minecraft:chests/underwater_ruin_small",
            "minecraft:chests/woodland_mansion",
            "minecraft:chests/village/village_armorer",
            "minecraft:chests/village/village_toolsmith",
            "minecraft:chests/village/village_weaponsmith"
    );

    private static final List<String> EPIC_CHESTS = List.of(
            "minecraft:chests/ancient_city",
            "minecraft:chests/ancient_city_ice_box",
            "minecraft:chests/bastion_treasure",
            "minecraft:chests/end_city_treasure"
    );

    // Only materials whose ores generate in the Overworld appear as raw chest loot.
    private static final Set<String> OVERWORLD_ORE_MATERIALS = Set.of(
            "ruby", "sapphire", "frost_diamond", "black_diamond", "topaz", "chrysoberyl", "aquamarine",
            "star_corundum", "solarflare_gem", "aetheric_crystal", "coolpper", "titanium"
    );

    // Vanilla chests hold tools, weapons, and armor, but not bows or shields, and nothing like a hammer.
    private static final List<String> GEAR = List.of(
            "sword", "pickaxe", "axe", "shovel", "hoe", "helmet", "chestplate", "leggings", "boots"
    );

    private static final String END_CITY_TREASURE = "minecraft:chests/end_city_treasure";

    private static final float MATERIAL_CHANCE = 0.05F;
    private static final float VOIDIUM_TEMPLATE_CHANCE = 0.15F;
    private static final float ENCHANTED_GEAR_CHANCE = 0.001F;
    private static final float PLAIN_GEAR_CHANCE = 0.0005F;
    private static final float EPIC_GEAR_CHANCE = 0.05F;
    private static final int MIN_ENCHANT_LEVELS = 20;
    private static final int MAX_ENCHANT_LEVELS = 50;

    private ArcaneArmoryLoot() {
    }

    public static void init() {
        LootEvents.MODIFY.register((lootTable, addPool) -> {
            String key = lootTable.toString();
            if (MATERIAL_CHESTS.contains(key)) {
                for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
                    ArcaneMaterial material = registered.material();
                    if (OVERWORLD_ORE_MATERIALS.contains(material.id())) {
                        addPool.accept(itemPool(item(material.materialItemId()), MATERIAL_CHANCE, 1, 4));
                    }
                    forEachGear(material, gear -> {
                        addPool.accept(enchantedPool(gear, ENCHANTED_GEAR_CHANCE));
                        addPool.accept(itemPool(gear, PLAIN_GEAR_CHANCE, 1, 1));
                    });
                }
            }
            if (EPIC_CHESTS.contains(key)) {
                for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
                    forEachGear(registered.material(), gear -> addPool.accept(itemPool(gear, EPIC_GEAR_CHANCE, 1, 1)));
                }
            }
            //? if >=1.20 {
            if (key.equals(END_CITY_TREASURE)) {
                addPool.accept(itemPool(item(VoidiumUpgrade.TEMPLATE_ID), VOIDIUM_TEMPLATE_CHANCE, 1, 1));
            }
            //?}
        });
    }

    private static void forEachGear(ArcaneMaterial material, Consumer<Item> action) {
        for (String kind : GEAR) {
            ArcaneArmoryContent.item(material.id() + "_" + kind).ifPresent(item -> action.accept(item.get()));
        }
    }

    private static Item item(String id) {
        return ArcaneArmoryContent.item(id)
                .orElseThrow(() -> new IllegalStateException("Missing Arcane Armory item " + id))
                .get();
    }

    private static LootPool.Builder itemPool(Item item, float chance, int min, int max) {
        return LootPool.lootPool()
                //? if >=26.3
                /*.setRolls(ContextIntProviders.exactly(1))*/
                //? if <26.3
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(item))
                //? if >=26.3
                /*.apply(SetItemCountFunction.setCount(ContextIntProviders.between(min, max)));*/
                //? if <26.3
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
    }

    // Enchants like an enchanting table spending 20 to 50 levels, without treasure enchantments.
    private static LootPool.Builder enchantedPool(Item item, float chance) {
        return itemPool(item, chance, 1, 1)
                //? if >=26.3 {
                /*.apply(new EnchantWithLevelsFunction.Builder(ContextIntProviders.between(MIN_ENCHANT_LEVELS, MAX_ENCHANT_LEVELS)));
                *///?} else if >=1.20.5 {
                .apply(new EnchantWithLevelsFunction.Builder(UniformGenerator.between(MIN_ENCHANT_LEVELS, MAX_ENCHANT_LEVELS)));
                //?} else {
                /*.apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(MIN_ENCHANT_LEVELS, MAX_ENCHANT_LEVELS)));
                *///?}
    }
}
