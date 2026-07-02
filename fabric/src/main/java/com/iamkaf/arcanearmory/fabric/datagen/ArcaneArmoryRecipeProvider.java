package com.iamkaf.arcanearmory.fabric.datagen;

//? if >=1.19.3 {
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import com.iamkaf.arcanearmory.content.ArcaneMaterial;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//?} else {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//?}
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ArcaneArmoryRecipeProvider implements DataProvider {
    private static final String MOD_ID = ArcaneArmoryConstants.MOD_ID;

    private final PackOutput.PathProvider recipePathProvider;

    //? if >=26.1 {
    public ArcaneArmoryRecipeProvider(FabricPackOutput output) {
    //?} else {
    public ArcaneArmoryRecipeProvider(FabricDataOutput output) {
    //?}
        //? if >=1.21 {
        this.recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
        //?} else {
        this.recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
        //?}
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        save(futures, cache, "ice", shapeless("minecraft:ice", 16, item(aa("frost_diamond"))));
        saveAlloys(futures, cache);
        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            ArcaneMaterial material = registered.material();
            String base = aa(material.materialItemId());
            saveCompression(futures, cache, material.id() + "_block", base, aa(material.id() + "_block"));
            if (material.ore()) {
                saveCompression(futures, cache, "raw_" + material.id() + "_block", aa(material.rawMaterialItemId()),
                        aa("raw_" + material.id() + "_block"));
                saveOreCooking(futures, cache, material);
            }
            if (material.tools()) {
                saveTools(futures, cache, material, base);
            }
            if (material.shield()) {
                save(futures, cache, material.id() + "_shield", shaped(aa(material.id() + "_shield"),
                        key("A", tag("minecraft:planks")),
                        key("B", item(base)),
                        pattern("ABA", "AAA", " A ")));
            }
            if (material.armor()) {
                saveArmor(futures, cache, material, base);
            }
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new));
    }

    @Override
    public String getName() {
        return "Arcane Armory recipes";
    }

    private void saveAlloys(List<CompletableFuture<?>> futures, CachedOutput cache) {
        save(futures, cache, "amber_from_alloying", shapeless(aa("amber"), 2,
                item("minecraft:iron_ingot"),
                item(aa("raw_amber"))));
        save(futures, cache, "aristeum_from_alloying", shapeless(aa("aristeum_ingot"), 2,
                item(aa("titanium_ingot")),
                item(aa("aetheric_crystal")),
                item(aa("aristea")),
                item("minecraft:pink_dye")));
        save(futures, cache, "coolpper_ingot_from_alloying", shapeless(aa("coolpper_ingot"), 2,
                item(aa("coolpper_ore"))));
        save(futures, cache, "doomflare_block_from_alloying", shapeless(aa("doomflare_block"), 1,
                item(aa("doom_fragment")),
                item(aa("solarflare_gem")),
                item(aa("shadow_crystal")),
                item(aa("aetheric_crystal")),
                item("minecraft:obsidian")));
        save(futures, cache, "voidium_from_alloying", shapeless(aa("voidium_ingot"), 2,
                item(aa("void_obsidian_fragment")),
                item(aa("bloodfire_garnet")),
                item("minecraft:netherite_ingot")));
    }

    private void saveCompression(List<CompletableFuture<?>> futures, CachedOutput cache, String blockId, String itemId, String blockItemId) {
        save(futures, cache, blockId, shaped(blockItemId,
                key("#", item(itemId)),
                pattern("###", "###", "###")));
        String decompressedId = itemId.substring(itemId.indexOf(':') + 1);
        save(futures, cache, decompressedId, shapeless(itemId, 9, item(blockItemId)));
    }

    private void saveOreCooking(List<CompletableFuture<?>> futures, CachedOutput cache, ArcaneMaterial material) {
        String result = aa(material.materialItemId());
        String group = material.id();
        String ore = aa(material.id() + "_ore");
        String deepslateOre = aa("deepslate_" + material.id() + "_ore");
        String raw = aa(material.rawMaterialItemId());
        saveCooking(futures, cache, material.materialItemId() + "_from_smelting_" + material.id() + "_ore", "minecraft:smelting", group, ore, result, 200);
        saveCooking(futures, cache, material.materialItemId() + "_from_smelting_deepslate_" + material.id() + "_ore", "minecraft:smelting", group, deepslateOre, result, 200);
        saveCooking(futures, cache, material.materialItemId() + "_from_smelting_raw_" + material.id(), "minecraft:smelting", group, raw, result, 200);
        saveCooking(futures, cache, material.materialItemId() + "_from_blasting_" + material.id() + "_ore", "minecraft:blasting", group, ore, result, 100);
        saveCooking(futures, cache, material.materialItemId() + "_from_blasting_deepslate_" + material.id() + "_ore", "minecraft:blasting", group, deepslateOre, result, 100);
        saveCooking(futures, cache, material.materialItemId() + "_from_blasting_raw_" + material.id(), "minecraft:blasting", group, raw, result, 100);
    }

    private void saveTools(List<CompletableFuture<?>> futures, CachedOutput cache, ArcaneMaterial material, String base) {
        save(futures, cache, material.id() + "_sword", shaped(aa(material.id() + "_sword"),
                key("O", item("minecraft:stick")),
                key("X", item(base)),
                pattern("X", "X", "O")));
        save(futures, cache, material.id() + "_shovel", shaped(aa(material.id() + "_shovel"),
                key("O", item("minecraft:stick")),
                key("X", item(base)),
                pattern("X", "O", "O")));
        save(futures, cache, material.id() + "_pickaxe", shaped(aa(material.id() + "_pickaxe"),
                key("O", item("minecraft:stick")),
                key("X", item(base)),
                pattern("XXX", " O ", " O ")));
        save(futures, cache, material.id() + "_axe", shaped(aa(material.id() + "_axe"),
                key("O", item("minecraft:stick")),
                key("X", item(base)),
                pattern(" XX", " OX", " O ")));
        save(futures, cache, material.id() + "_hoe", shaped(aa(material.id() + "_hoe"),
                key("O", item("minecraft:stick")),
                key("X", item(base)),
                pattern(" XX", " O ", " O ")));
        save(futures, cache, material.id() + "_hammer", shaped(aa(material.id() + "_hammer"),
                key("O", item("minecraft:stick")),
                key("X", item(base)),
                pattern("XXX", "XXX", " O ")));
        save(futures, cache, material.id() + "_bow", shaped(aa(material.id() + "_bow"),
                key("O", item("minecraft:string")),
                key("X", item(base)),
                pattern("OX ", "O X", "OX ")));
    }

    private void saveArmor(List<CompletableFuture<?>> futures, CachedOutput cache, ArcaneMaterial material, String base) {
        save(futures, cache, material.id() + "_helmet", shaped(aa(material.id() + "_helmet"),
                key("X", item(base)),
                pattern("XXX", "X X")));
        save(futures, cache, material.id() + "_chestplate", shaped(aa(material.id() + "_chestplate"),
                key("X", item(base)),
                pattern("X X", "XXX", "XXX")));
        save(futures, cache, material.id() + "_leggings", shaped(aa(material.id() + "_leggings"),
                key("X", item(base)),
                pattern("XXX", "X X", "X X")));
        save(futures, cache, material.id() + "_boots", shaped(aa(material.id() + "_boots"),
                key("X", item(base)),
                pattern("X X", "X X")));
    }

    private void saveCooking(List<CompletableFuture<?>> futures, CachedOutput cache, String id, String type, String group,
            String ingredient, String result, int cookingTime) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        //? if <26.1
        root.addProperty("category", "misc");
        root.addProperty("group", group);
        root.add("ingredient", item(ingredient));
        root.addProperty("result", result);
        root.addProperty("experience", 0.45D);
        root.addProperty("cookingtime", cookingTime);
        save(futures, cache, id, root);
    }

    private void save(List<CompletableFuture<?>> futures, CachedOutput cache, String id, JsonObject recipe) {
        futures.add(DataProvider.saveStable(cache, recipe, recipePathProvider.json(ArcaneArmoryConstants.resource(id))));
    }

    private static JsonObject shaped(String result, JsonObject... parts) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:crafting_shaped");
        //? if <26.1
        root.addProperty("category", "misc");
        JsonObject keys = new JsonObject();
        JsonArray patterns = new JsonArray();
        for (JsonObject part : parts) {
            if (part.has("_pattern")) {
                JsonArray values = part.getAsJsonArray("_pattern");
                for (int i = 0; i < values.size(); i++) {
                    patterns.add(values.get(i));
                }
            } else {
                for (String entry : part.keySet()) {
                    keys.add(entry, part.get(entry));
                }
            }
        }
        root.add("key", keys);
        root.add("pattern", patterns);
        root.add("result", result(result, 1));
        root.addProperty("show_notification", true);
        return root;
    }

    private static JsonObject shapeless(String result, int count, JsonObject... ingredients) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:crafting_shapeless");
        //? if <26.1
        root.addProperty("category", "misc");
        JsonArray items = new JsonArray();
        for (JsonObject ingredient : ingredients) {
            //? if >=1.21.2 {
            if (ingredient.has("item")) {
                items.add(ingredient.get("item").getAsString());
            } else if (ingredient.has("tag")) {
                items.add("#" + ingredient.get("tag").getAsString());
            } else {
                items.add(ingredient);
            }
            //?} else {
            items.add(ingredient);
            //?}
        }
        root.add("ingredients", items);
        root.add("result", result(result, count));
        return root;
    }

    private static JsonObject key(String symbol, JsonObject ingredient) {
        JsonObject root = new JsonObject();
        //? if >=1.21.2 {
        if (ingredient.has("item")) {
            root.addProperty(symbol, ingredient.get("item").getAsString());
        } else if (ingredient.has("tag")) {
            root.addProperty(symbol, "#" + ingredient.get("tag").getAsString());
        } else {
            root.add(symbol, ingredient);
        }
        //?} else {
        root.add(symbol, ingredient);
        //?}
        return root;
    }

    private static JsonObject pattern(String... patterns) {
        JsonObject root = new JsonObject();
        JsonArray values = new JsonArray();
        for (String pattern : patterns) {
            values.add(pattern);
        }
        root.add("_pattern", values);
        return root;
    }

    private static JsonObject item(String id) {
        JsonObject root = new JsonObject();
        root.addProperty("item", id);
        return root;
    }

    private static JsonObject tag(String id) {
        JsonObject root = new JsonObject();
        root.addProperty("tag", id);
        return root;
    }

    private static JsonObject result(String id, int count) {
        JsonObject root = new JsonObject();
        //? if >=1.21 {
        root.addProperty("id", id);
        //?} else {
        root.addProperty("item", id);
        //?}
        if (count != 1) {
            root.addProperty("count", count);
        }
        return root;
    }

    private static String aa(String id) {
        return MOD_ID + ":" + id;
    }
}
//?} else {
public final class ArcaneArmoryRecipeProvider {
    private ArcaneArmoryRecipeProvider() {
    }
}
//?}
