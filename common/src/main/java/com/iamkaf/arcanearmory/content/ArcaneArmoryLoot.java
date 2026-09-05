package com.iamkaf.arcanearmory.content;

import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
*///?} else {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
//?}

import java.util.List;

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

    private ArcaneArmoryLoot() {
    }

    public static void init() {
        LootEvents.MODIFY.register((lootTable, addPool) -> {
            String key = lootTable.toString();
            if (MATERIAL_CHESTS.contains(key)) {
                for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
                    ArcaneMaterial material = registered.material();
                    addMaterialPool(material, addPool);
                    addRareGearPools(material, 0.001F, addPool);
                }
            }
            if (EPIC_CHESTS.contains(key)) {
                for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
                    addRareGearPools(registered.material(), 0.05F, addPool);
                }
            }
        });
    }

    private static void addMaterialPool(ArcaneMaterial material, java.util.function.Consumer<LootPool.Builder> addPool) {
        ArcaneArmoryContent.item(material.materialItemId()).ifPresent(item -> addPool.accept(itemPool(item.get(), 0.05F, 1, 4)));
    }

    private static void addRareGearPools(
            ArcaneMaterial material,
            float chance,
            java.util.function.Consumer<LootPool.Builder> addPool
    ) {
        for (String itemId : material.itemIds()) {
            if (isGear(itemId)) {
                ArcaneArmoryContent.item(itemId).ifPresent(item -> addPool.accept(itemPool(item.get(), chance, 1, 1)));
            }
        }
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

    private static boolean isGear(String itemId) {
        return itemId.endsWith("_sword")
                || itemId.endsWith("_shovel")
                || itemId.endsWith("_pickaxe")
                || itemId.endsWith("_axe")
                || itemId.endsWith("_hoe")
                || itemId.endsWith("_hammer")
                || itemId.endsWith("_bow")
                || itemId.endsWith("_shield")
                || itemId.endsWith("_helmet")
                || itemId.endsWith("_chestplate")
                || itemId.endsWith("_leggings")
                || itemId.endsWith("_boots");
    }
}
