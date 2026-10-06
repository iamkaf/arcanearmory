package com.iamkaf.arcanearmory.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import net.minecraft.data.DataProvider;
//? if >=1.19.3 {
import net.minecraft.data.CachedOutput;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//?} else {
/*import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
*///?}
//?} else {
/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.HashCache;
*///?}

//? if <1.19.3
/*import java.io.IOException;*/
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//? if >=1.19.3
import java.util.concurrent.CompletableFuture;

/**
 * Writes the Arcane Compendium for Modonomicon, plus a book recipe for each loader that only loads when
 * Modonomicon does. Modonomicon's book format changed between its releases, so the version guards here
 * follow the Modonomicon build for each Minecraft line.
 */
public final class ArcaneArmoryBookProvider implements DataProvider {
    //? if <1.19.3
    /*private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();*/
    private static final String MODONOMICON = "modonomicon";
    private static final int ENTRIES_PER_ROW = 5;

    private final Path output;
    private final Path root;
    private final String minecraftVersion;

    //? if >=26.1 {
    public ArcaneArmoryBookProvider(FabricPackOutput output) {
        this(output.getOutputFolder());
    }
    //?} else if >=1.19.3 {
    /*public ArcaneArmoryBookProvider(FabricDataOutput output) {
        this(output.getOutputFolder());
    }
    *///?} else {
    /*public ArcaneArmoryBookProvider(FabricDataGenerator dataGenerator) {
        this(dataGenerator.getOutputFolder());
    }
    *///?}

    private ArcaneArmoryBookProvider(Path output) {
        this.output = output;
        this.root = ArcaneArmoryDataProvider.findRepositoryRoot(output);
        this.minecraftVersion = ArcaneArmoryDataProvider.findMinecraftVersion(output);
    }

    //? if >=1.19.3 {
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        files().forEach((path, json) -> futures.add(DataProvider.saveStable(cache, json, path)));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new));
    }
    //?} else {
    /*@Override
    public void run(HashCache cache) throws IOException {
        for (Map.Entry<Path, JsonObject> file : files().entrySet()) {
            DataProvider.save(GSON, cache, file.getValue(), file.getKey());
        }
    }
    *///?}

    @Override
    public String getName() {
        return "Arcane Armory guide book";
    }

    private Map<Path, JsonObject> files() {
        Map<Path, JsonObject> files = new LinkedHashMap<>();
        Path book = output.resolve("data").resolve(ArcaneArmoryConstants.MOD_ID).resolve("modonomicon/books").resolve(ArcaneCompendium.ID);
        files.put(book.resolve("book.json"), book());
        List<ArcaneCompendium.Category> categories = ArcaneCompendium.categories();
        for (int index = 0; index < categories.size(); index++) {
            ArcaneCompendium.Category category = categories.get(index);
            files.put(book.resolve("categories").resolve(category.id() + ".json"), category(category, index));
            for (int entryIndex = 0; entryIndex < category.entries().size(); entryIndex++) {
                ArcaneCompendium.Entry entry = category.entries().get(entryIndex);
                files.put(book.resolve("entries").resolve(category.id()).resolve(entry.id() + ".json"), entry(entry, entryIndex));
            }
        }
        for (String loader : List.of("fabric", "forge", "neoforge")) {
            if (hasBookRecipe(loader)) {
                files.put(root.resolve("versions").resolve(minecraftVersion).resolve(loader)
                        .resolve("src/main/generated/data").resolve(ArcaneArmoryConstants.MOD_ID)
                        .resolve(recipeFolder()).resolve(ArcaneCompendium.ID + ".json"), bookRecipe(loader));
            }
        }
        return files;
    }

    private static JsonObject book() {
        JsonObject book = new JsonObject();
        book.addProperty("name", ArcaneCompendium.KEY + ".name");
        book.addProperty("tooltip", ArcaneCompendium.KEY + ".tooltip");
        book.addProperty("description", ArcaneCompendium.KEY + ".description");
        book.addProperty("model", "modonomicon:modonomicon_purple");
        book.addProperty("generate_book_item", true);
        //? if >=1.19.3 {
        book.addProperty("creative_tab", ArcaneArmoryConstants.MOD_ID + ":" + ArcaneArmoryConstants.MOD_ID);
        //?} else {
        /*// Before registered creative tabs, Modonomicon matches the tab's label.
        book.addProperty("creative_tab", ArcaneArmoryConstants.MOD_ID);
        *///?}
        // Modonomicon reads index mode from its 1.21.1 builds on; older builds lay entries out as nodes.
        book.addProperty("display_mode", "index");
        return book;
    }

    private static JsonObject category(ArcaneCompendium.Category category, int sortNumber) {
        JsonObject json = new JsonObject();
        json.addProperty("name", category.key() + ".name");
        json.addProperty("description", category.key() + ".description");
        json.addProperty("icon", category.icon());
        json.addProperty("sort_number", sortNumber);
        json.addProperty("display_mode", "index");
        return json;
    }

    private static JsonObject entry(ArcaneCompendium.Entry entry, int sortNumber) {
        JsonObject json = new JsonObject();
        // Modonomicon 2 requires the type and id; older builds infer both.
        json.addProperty("type", "modonomicon:content");
        json.addProperty("id", ArcaneArmoryConstants.MOD_ID + ":" + entry.category() + "/" + entry.id());
        json.addProperty("category", ArcaneArmoryConstants.MOD_ID + ":" + entry.category());
        json.addProperty("name", entry.key() + ".name");
        json.addProperty("description", entry.key() + ".description");
        json.addProperty("icon", entry.icon());
        // Node maps (Modonomicon before its 1.21.1 builds) center on 0, 0: rows of five, two cells apart.
        json.addProperty("x", sortNumber % ENTRIES_PER_ROW * 2 - 4);
        json.addProperty("y", sortNumber / ENTRIES_PER_ROW * 2 - 2);
        json.addProperty("sort_number", sortNumber);
        JsonArray pages = new JsonArray();
        for (ArcaneCompendium.Page page : entry.pages()) {
            pages.add(page(entry, page));
        }
        json.add("pages", pages);
        return json;
    }

    private static JsonObject page(ArcaneCompendium.Entry entry, ArcaneCompendium.Page page) {
        JsonObject json = new JsonObject();
        json.addProperty("type", switch (page.type()) {
            case TEXT -> "modonomicon:text";
            case SPOTLIGHT -> "modonomicon:spotlight";
            case CRAFTING -> "modonomicon:crafting_recipe";
            //? if >=1.19.4 && <1.20 {
            /*// 1.19.4 keeps legacy smithing beside the template-based kind.
            case SMITHING -> "modonomicon:legacy_smithing_recipe";
            *///?} else {
            case SMITHING -> "modonomicon:smithing_recipe";
            //?}
        });
        json.addProperty("id", page.id());
        switch (page.type()) {
            case TEXT, SPOTLIGHT -> {
                json.addProperty("title", page.key(entry) + ".title");
                json.addProperty("text", page.key(entry) + ".text");
            }
            case CRAFTING, SMITHING -> {
                for (int index = 0; index < page.recipes().size(); index++) {
                    json.addProperty("recipe_id_" + (index + 1), page.recipes().get(index));
                }
            }
        }
        if (page.type() == ArcaneCompendium.Type.TEXT) {
            json.addProperty("show_title_separator", true);
        }
        if (page.type() == ArcaneCompendium.Type.SPOTLIGHT) {
            json.add("item", spotlightItem(page.item()));
        }
        return json;
    }

    private static JsonElement spotlightItem(String item) {
        //? if >=1.21.2 {
        return new com.google.gson.JsonPrimitive(item);
        //?} else {
        /*JsonObject json = new JsonObject();
        json.addProperty("item", item);
        return json;
        *///?}
    }

    private boolean hasBookRecipe(String loader) {
        if (!Files.isDirectory(root.resolve("versions").resolve(minecraftVersion).resolve(loader))) {
            return false;
        }
        //? if <1.20 {
        /*// Modonomicon has no Fabric build before 1.20.
        return !loader.equals("fabric");
        *///?} else {
        return true;
        //?}
    }

    private static String recipeFolder() {
        //? if >=1.21 {
        return "recipe";
        //?} else {
        /*return "recipes";
        *///?}
    }

    /** A book and an Aetheric Crystal make the Compendium, but only where Modonomicon is loaded. */
    private static JsonObject bookRecipe(String loader) {
        JsonObject recipe = new JsonObject();
        switch (loader) {
            case "fabric" -> {
                JsonObject condition = new JsonObject();
                condition.addProperty("condition", "fabric:all_mods_loaded");
                JsonArray mods = new JsonArray();
                mods.add(MODONOMICON);
                condition.add("values", mods);
                JsonArray conditions = new JsonArray();
                conditions.add(condition);
                recipe.add("fabric:load_conditions", conditions);
            }
            case "forge" -> {
                JsonObject condition = new JsonObject();
                condition.addProperty("type", "forge:mod_loaded");
                condition.addProperty("modid", MODONOMICON);
                //? if >=1.20.5 {
                recipe.add("forge:condition", condition);
                //?} else {
                /*JsonArray conditions = new JsonArray();
                conditions.add(condition);
                recipe.add("conditions", conditions);
                *///?}
            }
            case "neoforge" -> {
                JsonObject condition = new JsonObject();
                condition.addProperty("type", "neoforge:mod_loaded");
                condition.addProperty("modid", MODONOMICON);
                JsonArray conditions = new JsonArray();
                conditions.add(condition);
                recipe.add("neoforge:conditions", conditions);
            }
            default -> throw new IllegalArgumentException("Unknown loader " + loader);
        }
        //? if >=1.20 && <1.20.5 {
        /*// Vanilla shapeless recipes drop result NBT before 1.20.5; Forge keeps it, Fabric needs this serializer.
        recipe.addProperty("type", loader.equals("fabric") ? ArcaneArmoryConstants.MOD_ID + ":crafting_shapeless_with_nbt" : "minecraft:crafting_shapeless");
        *///?} else {
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        //?}
        //? if <26.1
        /*recipe.addProperty("category", "misc");*/
        JsonArray ingredients = new JsonArray();
        ingredients.add(ingredient("minecraft:book"));
        ingredients.add(ingredient(ArcaneArmoryConstants.MOD_ID + ":aetheric_crystal"));
        recipe.add("ingredients", ingredients);
        JsonObject result = new JsonObject();
        //? if >=1.20.5 {
        result.addProperty("id", "modonomicon:modonomicon");
        JsonObject components = new JsonObject();
        components.addProperty("modonomicon:book_id", ArcaneCompendium.BOOK_ID);
        result.add("components", components);
        //?} else {
        /*result.addProperty("item", "modonomicon:modonomicon");
        JsonObject nbt = new JsonObject();
        nbt.addProperty("modonomicon:book_id", ArcaneCompendium.BOOK_ID);
        result.add("nbt", nbt);
        *///?}
        recipe.add("result", result);
        return recipe;
    }

    private static JsonElement ingredient(String item) {
        //? if >=1.21.2 {
        return new com.google.gson.JsonPrimitive(item);
        //?} else {
        /*JsonObject json = new JsonObject();
        json.addProperty("item", item);
        return json;
        *///?}
    }
}
