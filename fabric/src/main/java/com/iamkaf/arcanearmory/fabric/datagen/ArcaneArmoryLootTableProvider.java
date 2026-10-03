package com.iamkaf.arcanearmory.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import com.iamkaf.arcanearmory.content.ArcaneMaterial;
import net.minecraft.data.DataProvider;
//? if >=1.19.3 {
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//?} else {
/*import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
*///?}

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
//?} else {
/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.HashCache;

import java.io.IOException;
import java.nio.file.Path;
*///?}

import java.util.LinkedHashMap;
import java.util.Map;

/** Writes block loot tables in each Minecraft line's own format, following vanilla's ore and flower tables. */
public final class ArcaneArmoryLootTableProvider implements DataProvider {
    private static final String MOD_ID = ArcaneArmoryConstants.MOD_ID;

    //? if >=1.19.3 {
    private final PackOutput.PathProvider pathProvider;

    //? if >=26.1 {
    public ArcaneArmoryLootTableProvider(FabricPackOutput output) {
    //?} else {
    /*public ArcaneArmoryLootTableProvider(FabricDataOutput output) {
    *///?}
        //? if >=1.21 {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        //?} else {
        /*this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
        *///?}
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        tables().forEach((id, table) -> futures.add(
                DataProvider.saveStable(cache, table, pathProvider.json(ArcaneArmoryConstants.resource("blocks/" + id)))));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new));
    }
    //?} else {
    /*private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path output;

    public ArcaneArmoryLootTableProvider(FabricDataGenerator dataGenerator) {
        this.output = dataGenerator.getOutputFolder();
    }

    @Override
    public void run(HashCache cache) throws IOException {
        for (Map.Entry<String, JsonObject> table : tables().entrySet()) {
            DataProvider.save(GSON, cache, table.getValue(),
                    output.resolve("data/" + MOD_ID + "/loot_tables/blocks/" + table.getKey() + ".json"));
        }
    }
    *///?}

    @Override
    public String getName() {
        return "Arcane Armory block loot tables";
    }

    private static Map<String, JsonObject> tables() {
        Map<String, JsonObject> tables = new LinkedHashMap<>();
        tables.put("doomflare_block", dropSelf("doomflare_block"));
        tables.put("arcanthe", dropSelf("arcanthe"));
        tables.put("meteorite", dropSelf("meteorite"));
        tables.put("potted_arcanthe", table("potted_arcanthe", survivingDrop("minecraft:flower_pot"), survivingDrop(aa("arcanthe"))));
        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            ArcaneMaterial material = registered.material();
            tables.put(material.id() + "_block", dropSelf(material.id() + "_block"));
            if (material.ore()) {
                String drop = oreDrop(material);
                tables.put(material.id() + "_ore", oreDrops(material.id() + "_ore", drop));
                tables.put("deepslate_" + material.id() + "_ore", oreDrops("deepslate_" + material.id() + "_ore", drop));
                tables.put("raw_" + material.id() + "_block", dropSelf("raw_" + material.id() + "_block"));
            }
        }
        return tables;
    }

    // Metal ores drop their raw form, gem ores their gem. Amber ore drops raw amber, the alloy ingredient.
    private static String oreDrop(ArcaneMaterial material) {
        boolean raw = material.ingot() || material.id().equals("amber");
        return aa(raw ? material.rawMaterialItemId() : material.materialItemId());
    }

    private static JsonObject dropSelf(String id) {
        return table(id, survivingDrop(aa(id)));
    }

    private static JsonObject oreDrops(String id, String drop) {
        JsonObject silkTouch = item(aa(id));
        //? if >=26.3 {
        /*silkTouch.addProperty("condition", "minecraft:tool/can_silk_touch");
        *///?} else {
        JsonArray conditions = new JsonArray();
        conditions.add(silkTouchCondition());
        silkTouch.add("conditions", conditions);
        //?}

        JsonObject fortune = item(drop);
        JsonArray functions = new JsonArray();
        functions.add(function("minecraft:apply_bonus", bonus -> {
            bonus.addProperty("enchantment", "minecraft:fortune");
            bonus.addProperty("formula", "minecraft:ore_drops");
        }));
        functions.add(function("minecraft:explosion_decay", decay -> { }));
        //? if >=26.3 {
        /*fortune.add("modifier", functions);
        *///?} else {
        fortune.add("functions", functions);
        //?}

        JsonObject alternatives = new JsonObject();
        alternatives.addProperty("type", "minecraft:alternatives");
        JsonArray children = new JsonArray();
        children.add(silkTouch);
        children.add(fortune);
        alternatives.add("children", children);
        return table(id, pool(alternatives, false));
    }

    //? if <26.3 {
    private static JsonObject silkTouchCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:match_tool");
        JsonObject levels = new JsonObject();
        levels.addProperty("min", 1);
        JsonObject enchantment = new JsonObject();
        //? if >=1.20.5 {
        enchantment.addProperty("enchantments", "minecraft:silk_touch");
        enchantment.add("levels", levels);
        JsonArray enchantments = new JsonArray();
        enchantments.add(enchantment);
        JsonObject components = new JsonObject();
        components.add("minecraft:enchantments", enchantments);
        JsonObject predicate = new JsonObject();
        predicate.add("predicates", components);
        //?} else {
        /*enchantment.addProperty("enchantment", "minecraft:silk_touch");
        enchantment.add("levels", levels);
        JsonArray enchantments = new JsonArray();
        enchantments.add(enchantment);
        JsonObject predicate = new JsonObject();
        predicate.add("enchantments", enchantments);
        *///?}
        condition.add("predicate", predicate);
        return condition;
    }
    //?}

    private static JsonObject survivingDrop(String itemId) {
        return pool(item(itemId), true);
    }

    private static JsonObject pool(JsonObject entry, boolean survivesExplosion) {
        JsonObject pool = new JsonObject();
        JsonArray entries = new JsonArray();
        entries.add(entry);
        pool.add("entries", entries);
        //? if >=26.3 {
        /*pool.addProperty("rolls", 1);
        if (survivesExplosion) {
            JsonObject condition = new JsonObject();
            condition.addProperty("type", "minecraft:survives_explosion");
            pool.add("condition", condition);
        }
        *///?} else {
        pool.addProperty("rolls", 1.0D);
        pool.addProperty("bonus_rolls", 0.0D);
        if (survivesExplosion) {
            JsonObject condition = new JsonObject();
            condition.addProperty("condition", "minecraft:survives_explosion");
            JsonArray conditions = new JsonArray();
            conditions.add(condition);
            pool.add("conditions", conditions);
        }
        //?}
        return pool;
    }

    private static JsonObject table(String id, JsonObject... pools) {
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        JsonArray poolArray = new JsonArray();
        for (JsonObject pool : pools) {
            poolArray.add(pool);
        }
        table.add("pools", poolArray);
        //? if >=1.20
        table.addProperty("random_sequence", MOD_ID + ":blocks/" + id);
        return table;
    }

    private static JsonObject item(String itemId) {
        JsonObject item = new JsonObject();
        item.addProperty("type", "minecraft:item");
        item.addProperty("name", itemId);
        return item;
    }

    private static JsonObject function(String type, java.util.function.Consumer<JsonObject> configure) {
        JsonObject function = new JsonObject();
        //? if >=26.3 {
        /*function.addProperty("type", type);
        *///?} else {
        function.addProperty("function", type);
        //?}
        configure.accept(function);
        return function;
    }

    private static String aa(String id) {
        return MOD_ID + ":" + id;
    }
}
