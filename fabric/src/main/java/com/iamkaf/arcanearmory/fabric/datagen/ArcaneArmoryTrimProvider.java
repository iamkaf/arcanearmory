package com.iamkaf.arcanearmory.fabric.datagen;

//? if >=1.20 {
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import com.iamkaf.arcanearmory.content.ArcaneTrimMaterials;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//?} else {
/*import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
*///?}
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * Registers each Arcane trim material and teaches the trim atlases its palette, so trims made from it render
 * on worn armor and on armor icons.
 */
public final class ArcaneArmoryTrimProvider implements DataProvider {
    //? if >=1.21 {
    private static final List<String> TRIM_PATTERNS = List.of("coast", "sentry", "dune", "wild", "ward", "eye", "vex", "tide",
            "snout", "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host", "flow", "bolt");
    //?} else {
    /*private static final List<String> TRIM_PATTERNS = List.of("coast", "sentry", "dune", "wild", "ward", "eye", "vex", "tide",
            "snout", "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host");
    *///?}
    private static final List<String> ARMOR_PIECES = List.of("helmet", "chestplate", "leggings", "boots");

    private final PackOutput.PathProvider trimMaterialPathProvider;
    private final PackOutput.PathProvider itemTagPathProvider;
    private final PackOutput.PathProvider atlasPathProvider;

    //? if >=26.1 {
    public ArcaneArmoryTrimProvider(FabricPackOutput output) {
    //?} else {
    /*public ArcaneArmoryTrimProvider(FabricDataOutput output) {
    *///?}
        this.trimMaterialPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "trim_material");
        //? if >=1.21 {
        this.itemTagPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "tags/item");
        //?} else {
        /*this.itemTagPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "tags/items");
        *///?}
        this.atlasPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }

    /** The legacy trim_type model predicate value for a trim. Vanilla's start at 0.1, so these stay below. */
    public static double itemModelIndex(ArcaneTrimMaterials.Trim trim) {
        return (11 + ArcaneTrimMaterials.ALL.indexOf(trim)) / 1000.0D;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        JsonArray ingredients = new JsonArray();
        for (ArcaneTrimMaterials.Trim trim : ArcaneTrimMaterials.ALL) {
            futures.add(DataProvider.saveStable(cache, trimMaterial(trim),
                    trimMaterialPathProvider.json(ArcaneArmoryConstants.resource(trim.id()))));
            ingredients.add(ArcaneArmoryConstants.MOD_ID + ":" + trim.ingredientId());
        }
        JsonObject tag = new JsonObject();
        tag.add("values", ingredients);
        futures.add(DataProvider.saveStable(cache, tag, itemTagPathProvider.json(vanilla("trim_materials"))));

        List<String> armorTextures = new ArrayList<>();
        for (String pattern : TRIM_PATTERNS) {
            //? if >=1.21.5 {
            armorTextures.add("minecraft:trims/entity/humanoid/" + pattern);
            armorTextures.add("minecraft:trims/entity/humanoid_leggings/" + pattern);
            //?} else {
            /*armorTextures.add("minecraft:trims/models/armor/" + pattern);
            armorTextures.add("minecraft:trims/models/armor/" + pattern + "_leggings");
            *///?}
        }
        List<String> iconTextures = ARMOR_PIECES.stream().map(piece -> "minecraft:trims/items/" + piece + "_trim").toList();
        // From 26.3 worn trims recolor at render time from the trim material's palette, so only icons need an atlas.
        //? if <26.3
        futures.add(DataProvider.saveStable(cache, atlas(armorTextures), atlasPathProvider.json(vanilla("armor_trims"))));
        //? if >=1.21.5 {
        futures.add(DataProvider.saveStable(cache, atlas(iconTextures), atlasPathProvider.json(vanilla("items"))));
        //?} else {
        /*futures.add(DataProvider.saveStable(cache, atlas(iconTextures), atlasPathProvider.json(vanilla("blocks"))));
        *///?}
        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new));
    }

    @Override
    public String getName() {
        return "Arcane Armory trim materials";
    }

    private static JsonObject trimMaterial(ArcaneTrimMaterials.Trim trim) {
        JsonObject root = new JsonObject();
        //? if >=26.3 {
        /*root.addProperty("palette_id", paletteId(trim));
        *///?} else {
        root.addProperty("asset_name", trim.assetName());
        //?}
        //? if <1.21.5 {
        /*root.addProperty("ingredient", ArcaneArmoryConstants.MOD_ID + ":" + trim.ingredientId());
        root.addProperty("item_model_index", itemModelIndex(trim));
        *///?}
        JsonObject description = new JsonObject();
        description.addProperty("translate", "trim_material." + ArcaneArmoryConstants.MOD_ID + "." + trim.id());
        description.addProperty("color", String.format(Locale.ROOT, "#%06X", trim.color()));
        root.add("description", description);
        return root;
    }

    private static JsonObject atlas(List<String> textures) {
        JsonObject source = new JsonObject();
        source.addProperty("type", "minecraft:paletted_permutations");
        JsonArray textureArray = new JsonArray();
        textures.forEach(textureArray::add);
        source.add("textures", textureArray);
        //? if >=26.3 {
        /*source.addProperty("palette_key", "minecraft:trim_base");
        *///?} else {
        source.addProperty("palette_key", "minecraft:trims/color_palettes/trim_palette");
        //?}
        JsonObject permutations = new JsonObject();
        for (ArcaneTrimMaterials.Trim trim : ArcaneTrimMaterials.ALL) {
            //? if >=26.3 {
            /*permutations.addProperty(trim.assetName(), paletteId(trim));
            *///?} else {
            permutations.addProperty(trim.assetName(), ArcaneArmoryConstants.MOD_ID + ":palettes/trim/" + trim.id());
            //?}
        }
        source.add("permutations", permutations);
        JsonArray sources = new JsonArray();
        sources.add(source);
        JsonObject root = new JsonObject();
        root.add("sources", sources);
        return root;
    }

    //? if >=26.3 {
    /*private static String paletteId(ArcaneTrimMaterials.Trim trim) {
        return ArcaneArmoryConstants.MOD_ID + ":trim/" + trim.id();
    }
    *///?}

    //? if >=1.21.11 {
    private static net.minecraft.resources.Identifier vanilla(String path) {
        return net.minecraft.resources.Identifier.withDefaultNamespace(path);
    }
    //?} else if >=1.21 {
    /*private static net.minecraft.resources.ResourceLocation vanilla(String path) {
        return net.minecraft.resources.ResourceLocation.withDefaultNamespace(path);
    }
    *///?} else {
    /*private static net.minecraft.resources.ResourceLocation vanilla(String path) {
        return new net.minecraft.resources.ResourceLocation(path);
    }
    *///?}
}
//?}
