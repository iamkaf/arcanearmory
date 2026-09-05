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

public final class ArcaneArmoryModelProvider implements DataProvider {
    private static final String MOD_ID = ArcaneArmoryConstants.MOD_ID;

    private final PackOutput.PathProvider blockstatePathProvider;
    private final PackOutput.PathProvider blockModelPathProvider;
    private final PackOutput.PathProvider itemModelPathProvider;
    //? if >=1.21.2
    private final PackOutput.PathProvider itemDefinitionPathProvider;
    //? if >=1.21.5
    private final PackOutput.PathProvider equipmentPathProvider;

    //? if >=26.1 {
    public ArcaneArmoryModelProvider(FabricPackOutput output) {
    //?} else {
    public ArcaneArmoryModelProvider(FabricDataOutput output) {
    //?}
        this.blockstatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.blockModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        this.itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
        //? if >=1.21.2
        this.itemDefinitionPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        //? if >=1.21.5
        this.equipmentPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        block(futures, cache, "doomflare_block", blockModel("doomflare_block"));
        block(futures, cache, "aristea", crossModel("aristea"));
        block(futures, cache, "potted_aristea", pottedModel("aristea"));
        blockItem(futures, cache, "doomflare_block");
        blockItem(futures, cache, "aristea");

        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            ArcaneMaterial material = registered.material();
            for (String blockId : material.blockIds()) {
                block(futures, cache, blockId, blockModel(blockId));
                blockItem(futures, cache, blockId);
            }
            for (String itemId : material.itemIds()) {
                if (itemId.endsWith("_bow")) {
                    bow(futures, cache, itemId);
                } else if (itemId.endsWith("_shield")) {
                    shield(futures, cache, itemId);
                } else {
                    item(futures, cache, itemId, regularItemModel(itemId), itemId);
                }
            }
            //? if >=1.21.5 {
            if (material.armor()) {
                futures.add(DataProvider.saveStable(
                        cache,
                        equipmentDefinition(material.id()),
                        equipmentPathProvider.json(ArcaneArmoryConstants.resource(material.id()))
                ));
            }
            //?}
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new));
    }

    @Override
    public String getName() {
        return "Arcane Armory models and item definitions";
    }

    private void block(List<CompletableFuture<?>> futures, CachedOutput cache, String id, JsonObject model) {
        futures.add(DataProvider.saveStable(cache, simpleBlockstate(id), blockstatePathProvider.json(ArcaneArmoryConstants.resource(id))));
        futures.add(DataProvider.saveStable(cache, model, blockModelPathProvider.json(ArcaneArmoryConstants.resource(id))));
    }

    private void blockItem(List<CompletableFuture<?>> futures, CachedOutput cache, String id) {
        item(futures, cache, id, blockItemModel(id), id);
    }

    private void item(List<CompletableFuture<?>> futures, CachedOutput cache, String id, JsonObject model, String modelId) {
        futures.add(DataProvider.saveStable(cache, model, itemModelPathProvider.json(ArcaneArmoryConstants.resource(id))));
        //? if >=1.21.2
        futures.add(DataProvider.saveStable(cache, itemDefinition(modelId), itemDefinitionPathProvider.json(ArcaneArmoryConstants.resource(id))));
    }

    private void bow(List<CompletableFuture<?>> futures, CachedOutput cache, String id) {
        futures.add(DataProvider.saveStable(cache, bowModel(id, id), itemModelPathProvider.json(ArcaneArmoryConstants.resource(id))));
        for (int pull = 0; pull < 3; pull++) {
            String pullId = id + "_pulling_" + pull;
            futures.add(DataProvider.saveStable(cache, bowModel(pullId, pullId), itemModelPathProvider.json(ArcaneArmoryConstants.resource(pullId))));
        }
        //? if >=1.21.2
        futures.add(DataProvider.saveStable(cache, bowItemDefinition(id), itemDefinitionPathProvider.json(ArcaneArmoryConstants.resource(id))));
    }

    private void shield(List<CompletableFuture<?>> futures, CachedOutput cache, String id) {
        futures.add(DataProvider.saveStable(cache, shieldModel(id, false), itemModelPathProvider.json(ArcaneArmoryConstants.resource(id))));
        futures.add(DataProvider.saveStable(cache, shieldModel(id, true), itemModelPathProvider.json(ArcaneArmoryConstants.resource(id + "_blocking"))));
        //? if >=1.21.2
        futures.add(DataProvider.saveStable(cache, shieldItemDefinition(id), itemDefinitionPathProvider.json(ArcaneArmoryConstants.resource(id))));
    }

    private static JsonObject simpleBlockstate(String id) {
        JsonObject root = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject variant = new JsonObject();
        variant.addProperty("model", MOD_ID + ":block/" + id);
        variants.add("", variant);
        root.add("variants", variants);
        return root;
    }

    private static JsonObject blockModel(String id) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", MOD_ID + ":block/" + id);
        root.add("textures", textures);
        return root;
    }

    private static JsonObject crossModel(String plant) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/cross");
        JsonObject textures = new JsonObject();
        textures.addProperty("cross", MOD_ID + ":block/" + plant);
        root.add("textures", textures);
        return root;
    }

    private static JsonObject pottedModel(String plant) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/flower_pot_cross");
        JsonObject textures = new JsonObject();
        textures.addProperty("plant", MOD_ID + ":block/" + plant);
        root.add("textures", textures);
        return root;
    }

    private static JsonObject blockItemModel(String id) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", MOD_ID + ":block/" + id);
        return root;
    }

    private static JsonObject regularItemModel(String id) {
        if (isHandheld(id)) {
            return layeredItemModel("minecraft:item/handheld", id, true);
        }
        return flatItemModel(id);
    }

    private static JsonObject flatItemModel(String id) {
        return layeredItemModel("minecraft:item/generated", id, hasOverlay(id));
    }

    private static JsonObject bowModel(String id, String texture) {
        JsonObject root = layeredItemModel(id.contains("_pulling_") ? MOD_ID + ":item/" + id.substring(0, id.indexOf("_pulling_")) : "minecraft:item/generated",
                texture, true);
        if (!id.contains("_pulling_")) {
            root.add("display", bowDisplay());
        }
        //? if <1.21.2 {
        if (!id.contains("_pulling_")) {
            JsonArray overrides = new JsonArray();
            bowOverride(overrides, id + "_pulling_0", null);
            bowOverride(overrides, id + "_pulling_1", 0.65D);
            bowOverride(overrides, id + "_pulling_2", 0.9D);
            root.add("overrides", overrides);
        }
        //?}
        return root;
    }

    private static JsonObject shieldModel(String id, boolean blocking) {
        JsonObject root = shieldGeometryModel(id, blocking);
        if (!blocking) {
            JsonArray overrides = new JsonArray();
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("blocking", 1);
            override.add("predicate", predicate);
            override.addProperty("model", MOD_ID + ":item/" + id + "_blocking");
            overrides.add(override);
            root.add("overrides", overrides);
        }
        return root;
    }

    private static JsonObject shieldGeometryModel(String id, boolean blocking) {
        JsonObject root = new JsonObject();
        root.addProperty("gui_light", "front");

        JsonObject textures = new JsonObject();
        textures.addProperty("shield", MOD_ID + ":item/" + id);
        textures.addProperty("particle", "#shield");
        root.add("textures", textures);

        JsonArray elements = new JsonArray();
        elements.add(shieldPlateElement());
        elements.add(shieldHandleElement());
        root.add("elements", elements);
        root.add("display", shieldDisplay(blocking));
        return root;
    }

    private static JsonObject shieldPlateElement() {
        JsonObject element = new JsonObject();
        element.add("from", array(0, 0, 11));
        element.add("to", array(12, 22, 12));
        JsonObject faces = new JsonObject();
        faces.add("down", shieldFace(3.25D, 0, 6.25D, 0.25D));
        faces.add("up", shieldFace(0.25D, 0, 3.25D, 0.25D));
        faces.add("north", shieldFace(3.5D, 0.25D, 6.5D, 5.75D));
        faces.add("south", shieldFace(0.25D, 0.25D, 3.25D, 5.75D));
        faces.add("west", shieldFace(0, 0.25D, 0.25D, 5.75D));
        faces.add("east", shieldFace(3.25D, 0.25D, 3.5D, 5.75D));
        element.add("faces", faces);
        return element;
    }

    private static JsonObject shieldHandleElement() {
        JsonObject element = new JsonObject();
        element.add("from", array(5, 8, 5));
        element.add("to", array(7, 14, 11));
        JsonObject faces = new JsonObject();
        faces.add("down", shieldFace(8.5D, 0, 9, 1.5D));
        faces.add("up", shieldFace(8, 0, 8.5D, 1.5D));
        faces.add("north", shieldFace(8, 1.5D, 8.5D, 3));
        faces.add("west", shieldFace(8.5D, 1.5D, 10, 3));
        faces.add("east", shieldFace(6.5D, 1.5D, 8, 3));
        element.add("faces", faces);
        return element;
    }

    private static JsonObject shieldFace(double uvMinX, double uvMinY, double uvMaxX, double uvMaxY) {
        JsonObject face = new JsonObject();
        face.add("uv", array(uvMinX, uvMinY, uvMaxX, uvMaxY));
        face.addProperty("texture", "#shield");
        return face;
    }

    private static JsonObject layeredItemModel(String parent, String texture, boolean overlay) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", parent);
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", MOD_ID + ":item/" + texture);
        if (overlay) {
            textures.addProperty("layer1", MOD_ID + ":item/" + texture + "_overlay");
        }
        root.add("textures", textures);
        return root;
    }

    //? if <1.21.2 {
    private static void bowOverride(JsonArray overrides, String modelId, Double pull) {
        JsonObject override = new JsonObject();
        JsonObject predicate = new JsonObject();
        predicate.addProperty("pulling", 1);
        if (pull != null) {
            predicate.addProperty("pull", pull);
        }
        override.add("predicate", predicate);
        override.addProperty("model", MOD_ID + ":item/" + modelId);
        overrides.add(override);
    }
    //?}

    private static JsonObject bowDisplay() {
        JsonObject display = new JsonObject();
        display.add("thirdperson_righthand", transform(-80, 260, -40, -1, -2, 2.5D, 0.9D));
        display.add("thirdperson_lefthand", transform(-80, -280, 40, -1, -2, 2.5D, 0.9D));
        display.add("firstperson_righthand", transform(0, -90, 25, 1.13D, 3.2D, 1.13D, 0.68D));
        display.add("firstperson_lefthand", transform(0, 90, -25, 1.13D, 3.2D, 1.13D, 0.68D));
        return display;
    }

    private static JsonObject transform(double rotationX, double rotationY, double rotationZ,
            double translationX, double translationY, double translationZ, double scale) {
        JsonObject transform = new JsonObject();
        transform.add("rotation", array(rotationX, rotationY, rotationZ));
        transform.add("translation", array(translationX, translationY, translationZ));
        transform.add("scale", array(scale, scale, scale));
        return transform;
    }

    private static JsonArray array(double... values) {
        JsonArray array = new JsonArray();
        for (double value : values) {
            array.add(value);
        }
        return array;
    }

    private static boolean isHandheld(String id) {
        return id.endsWith("_sword")
                || id.endsWith("_shovel")
                || id.endsWith("_pickaxe")
                || id.endsWith("_axe")
                || id.endsWith("_hoe")
                || id.endsWith("_hammer");
    }

    private static boolean hasOverlay(String id) {
        return isHandheld(id)
                || id.endsWith("_bow")
                || id.contains("_bow_pulling_")
                || id.endsWith("_helmet")
                || id.endsWith("_chestplate")
                || id.endsWith("_leggings")
                || id.endsWith("_boots");
    }

    //? if >=1.21.5 {
    private static JsonObject equipmentDefinition(String id) {
        JsonObject root = new JsonObject();
        JsonObject layers = new JsonObject();
        layers.add("humanoid", equipmentLayers(id));
        layers.add("humanoid_leggings", equipmentLayers(id));
        root.add("layers", layers);
        return root;
    }

    private static JsonArray equipmentLayers(String id) {
        JsonArray layers = new JsonArray();
        JsonObject base = new JsonObject();
        base.addProperty("texture", MOD_ID + ":" + id);
        layers.add(base);
        JsonObject overlay = new JsonObject();
        overlay.addProperty("texture", MOD_ID + ":" + id + "_overlay");
        layers.add(overlay);
        return layers;
    }
    //?}

    //? if >=1.21.2 {
    private static JsonObject itemDefinition(String modelId) {
        JsonObject root = new JsonObject();
        root.add("model", itemModel(modelId));
        return root;
    }

    private static JsonObject bowItemDefinition(String id) {
        JsonObject root = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:condition");
        model.addProperty("property", "minecraft:using_item");
        model.add("on_false", itemModel(id));

        JsonObject dispatch = new JsonObject();
        dispatch.addProperty("type", "minecraft:range_dispatch");
        dispatch.addProperty("property", "minecraft:use_duration");
        dispatch.addProperty("scale", 0.05D);
        dispatch.add("fallback", itemModel(id + "_pulling_0"));
        JsonArray entries = new JsonArray();
        rangeEntry(entries, 0.65D, id + "_pulling_1");
        rangeEntry(entries, 0.9D, id + "_pulling_2");
        dispatch.add("entries", entries);
        model.add("on_true", dispatch);
        root.add("model", model);
        return root;
    }

    private static JsonObject shieldItemDefinition(String id) {
        JsonObject root = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:condition");
        model.addProperty("property", "minecraft:using_item");
        model.add("on_false", itemModel(id));
        model.add("on_true", itemModel(id + "_blocking"));
        root.add("model", model);
        return root;
    }

    private static void rangeEntry(JsonArray entries, double threshold, String modelId) {
        JsonObject entry = new JsonObject();
        entry.addProperty("threshold", threshold);
        entry.add("model", itemModel(modelId));
        entries.add(entry);
    }

    private static JsonObject itemModel(String modelId) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", MOD_ID + ":item/" + modelId);
        return model;
    }
    //?}

    private static JsonObject shieldDisplay(boolean blocking) {
        JsonObject display = new JsonObject();
        if (blocking) {
            display.add("thirdperson_righthand", transform(40, 135, 5, -1, -4, -2, 1));
            display.add("thirdperson_lefthand", transform(45, 135, 0, 1.5D, -5.5D, 0, 1));
            display.add("firstperson_righthand", transform(0, 180, -5, -5.4D, -13.35D, -2, 1.65D));
            display.add("firstperson_lefthand", transform(0, 180, -5, 4, -13.25D, -2, 1.65D));
        } else {
            display.add("thirdperson_righthand", transform(0, 90, 0, 0, -5, 2, 1));
            display.add("thirdperson_lefthand", transform(0, 90, 0, 0, -5, 6, 1));
            display.add("firstperson_righthand", transform(0, 180, 5, -2, -19, 0, 1.65D));
            display.add("firstperson_lefthand", transform(0, 180, 5, 4.5D, -19.5D, 2, 1.65D));
            display.add("gui", transform(15, -25, -5, 2, -2, 0, 0.65D));
            display.add("fixed", transform(0, 180, 0, 1, -1.5D, 0, 0.5D));
            display.add("ground", transform(0, 0, 0, 0, 3, 0, 0.25D));
        }
        return display;
    }

}
//?} else {
public final class ArcaneArmoryModelProvider implements net.minecraft.data.DataProvider {
    private static final com.google.gson.Gson GSON = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
    private static final String MOD_ID = com.iamkaf.arcanearmory.ArcaneArmoryConstants.MOD_ID;

    private final java.nio.file.Path output;

    public ArcaneArmoryModelProvider(net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator dataGenerator) {
        this.output = dataGenerator.getOutputFolder();
    }

    @Override
    public void run(net.minecraft.data.HashCache cache) throws java.io.IOException {
        for (com.iamkaf.arcanearmory.content.ArcaneArmoryContent.RegisteredMaterial registered :
                com.iamkaf.arcanearmory.content.ArcaneArmoryContent.registeredMaterials()) {
            com.iamkaf.arcanearmory.content.ArcaneMaterial material = registered.material();
            if (material.shield()) {
                shield(cache, material.id() + "_shield");
            }
        }
    }

    @Override
    public String getName() {
        return "Arcane Armory legacy shield models";
    }

    private void shield(net.minecraft.data.HashCache cache, String id) throws java.io.IOException {
        net.minecraft.data.DataProvider.save(GSON, cache, shieldModel(id, false), itemModelPath(id));
        net.minecraft.data.DataProvider.save(GSON, cache, shieldModel(id, true), itemModelPath(id + "_blocking"));
    }

    private java.nio.file.Path itemModelPath(String id) {
        return output.resolve("assets/" + MOD_ID + "/models/item/" + id + ".json");
    }

    private static com.google.gson.JsonObject shieldModel(String id, boolean blocking) {
        com.google.gson.JsonObject root = shieldGeometryModel(id, blocking);
        if (!blocking) {
            com.google.gson.JsonArray overrides = new com.google.gson.JsonArray();
            com.google.gson.JsonObject override = new com.google.gson.JsonObject();
            com.google.gson.JsonObject predicate = new com.google.gson.JsonObject();
            predicate.addProperty("blocking", 1);
            override.add("predicate", predicate);
            override.addProperty("model", MOD_ID + ":item/" + id + "_blocking");
            overrides.add(override);
            root.add("overrides", overrides);
        }
        return root;
    }

    private static com.google.gson.JsonObject shieldGeometryModel(String id, boolean blocking) {
        com.google.gson.JsonObject root = new com.google.gson.JsonObject();
        root.addProperty("gui_light", "front");

        com.google.gson.JsonObject textures = new com.google.gson.JsonObject();
        textures.addProperty("shield", MOD_ID + ":item/" + id);
        textures.addProperty("particle", "#shield");
        root.add("textures", textures);

        com.google.gson.JsonArray elements = new com.google.gson.JsonArray();
        elements.add(shieldPlateElement());
        elements.add(shieldHandleElement());
        root.add("elements", elements);
        root.add("display", shieldDisplay(blocking));
        return root;
    }

    private static com.google.gson.JsonObject shieldPlateElement() {
        com.google.gson.JsonObject element = new com.google.gson.JsonObject();
        element.add("from", array(0, 0, 11));
        element.add("to", array(12, 22, 12));
        com.google.gson.JsonObject faces = new com.google.gson.JsonObject();
        faces.add("down", shieldFace(3.25D, 0, 6.25D, 0.25D));
        faces.add("up", shieldFace(0.25D, 0, 3.25D, 0.25D));
        faces.add("north", shieldFace(3.5D, 0.25D, 6.5D, 5.75D));
        faces.add("south", shieldFace(0.25D, 0.25D, 3.25D, 5.75D));
        faces.add("west", shieldFace(0, 0.25D, 0.25D, 5.75D));
        faces.add("east", shieldFace(3.25D, 0.25D, 3.5D, 5.75D));
        element.add("faces", faces);
        return element;
    }

    private static com.google.gson.JsonObject shieldHandleElement() {
        com.google.gson.JsonObject element = new com.google.gson.JsonObject();
        element.add("from", array(5, 8, 5));
        element.add("to", array(7, 14, 11));
        com.google.gson.JsonObject faces = new com.google.gson.JsonObject();
        faces.add("down", shieldFace(8.5D, 0, 9, 1.5D));
        faces.add("up", shieldFace(8, 0, 8.5D, 1.5D));
        faces.add("north", shieldFace(8, 1.5D, 8.5D, 3));
        faces.add("west", shieldFace(8.5D, 1.5D, 10, 3));
        faces.add("east", shieldFace(6.5D, 1.5D, 8, 3));
        element.add("faces", faces);
        return element;
    }

    private static com.google.gson.JsonObject shieldFace(double uvMinX, double uvMinY, double uvMaxX, double uvMaxY) {
        com.google.gson.JsonObject face = new com.google.gson.JsonObject();
        face.add("uv", array(uvMinX, uvMinY, uvMaxX, uvMaxY));
        face.addProperty("texture", "#shield");
        return face;
    }

    private static com.google.gson.JsonObject shieldDisplay(boolean blocking) {
        com.google.gson.JsonObject display = new com.google.gson.JsonObject();
        if (blocking) {
            display.add("thirdperson_righthand", transform(40, 135, 5, -1, -4, -2, 1));
            display.add("thirdperson_lefthand", transform(45, 135, 0, 1.5D, -5.5D, 0, 1));
            display.add("firstperson_righthand", transform(0, 180, -5, -5.4D, -13.35D, -2, 1.65D));
            display.add("firstperson_lefthand", transform(0, 180, -5, 4, -13.25D, -2, 1.65D));
        } else {
            display.add("thirdperson_righthand", transform(0, 90, 0, 0, -5, 2, 1));
            display.add("thirdperson_lefthand", transform(0, 90, 0, 0, -5, 6, 1));
            display.add("firstperson_righthand", transform(0, 180, 5, -2, -19, 0, 1.65D));
            display.add("firstperson_lefthand", transform(0, 180, 5, 4.5D, -19.5D, 2, 1.65D));
            display.add("gui", transform(15, -25, -5, 2, -2, 0, 0.65D));
            display.add("fixed", transform(0, 180, 0, 1, -1.5D, 0, 0.5D));
            display.add("ground", transform(0, 0, 0, 0, 3, 0, 0.25D));
        }
        return display;
    }

    private static com.google.gson.JsonObject transform(double rotationX, double rotationY, double rotationZ,
            double translationX, double translationY, double translationZ, double scale) {
        com.google.gson.JsonObject transform = new com.google.gson.JsonObject();
        transform.add("rotation", array(rotationX, rotationY, rotationZ));
        transform.add("translation", array(translationX, translationY, translationZ));
        transform.add("scale", array(scale, scale, scale));
        return transform;
    }

    private static com.google.gson.JsonArray array(double... values) {
        com.google.gson.JsonArray array = new com.google.gson.JsonArray();
        for (double value : values) {
            array.add(value);
        }
        return array;
    }
}
//?}
