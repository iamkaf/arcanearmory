package com.iamkaf.arcanearmory.fabric.datagen;

import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import com.iamkaf.arcanearmory.content.ArcaneArmoryCreativeTab;
import com.iamkaf.arcanearmory.content.DoomflareBlockItem;
import com.iamkaf.arcanearmory.content.ArcaneMaterial;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//? if >=1.21
import net.minecraft.core.HolderLookup;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//?} else if >=1.19.3 {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//?} else {
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
//?}

//? if >=1.21
import java.util.concurrent.CompletableFuture;

public final class ArcaneArmoryLanguageProvider extends FabricLanguageProvider {
    //? if >=26.1 {
    public ArcaneArmoryLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }
    //?} else if >=1.21 {
    public ArcaneArmoryLanguageProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }
    //?} else if >=1.19.3 {
    public ArcaneArmoryLanguageProvider(FabricDataOutput output) {
        super(output);
    }
    //?} else {
    public ArcaneArmoryLanguageProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }
    //?}

    //? if >=1.21 {
    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
        addTranslations(translationBuilder);
    }
    //?} else {
    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        addTranslations(translationBuilder);
    }
    //?}

    private static void addTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ArcaneArmoryCreativeTab.TITLE_KEY, ArcaneArmoryConstants.MOD_NAME);
        // Fabric 1.18.2 names its creative tab after the tab id.
        translationBuilder.add(ArcaneArmoryCreativeTab.TITLE_KEY + "." + ArcaneArmoryCreativeTab.ID, ArcaneArmoryConstants.MOD_NAME);
        translationBuilder.add("block." + ArcaneArmoryConstants.MOD_ID + ".doomflare_block", "Doomflare Block");
        translationBuilder.add(DoomflareBlockItem.TOOLTIP_KEY,
                "If destroyed by an explosion, this block will explode with the force of 10 TNT.");
        translationBuilder.add(itemKey("amber_ingot"), "Amber Ingot");
        translationBuilder.add("block." + ArcaneArmoryConstants.MOD_ID + ".arcanthe", "Arcanthe");
        translationBuilder.add("block." + ArcaneArmoryConstants.MOD_ID + ".potted_arcanthe", "Potted Arcanthe");

        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            ArcaneMaterial material = registered.material();
            translationBuilder.add(itemKey(material.materialItemId()), material.displayName() + (material.ingot() ? " Ingot" : ""));
            translationBuilder.add(itemKey(material.rawMaterialItemId()), "Raw " + material.displayName());
            if (material.ingot()) {
                translationBuilder.add(itemKey(material.nuggetItemId()), material.displayName() + " Nugget");
            }
            for (String blockId : material.blockIds()) {
                translationBuilder.add(blockKey(blockId), blockName(material, blockId));
                translationBuilder.add(itemKey(blockId), blockName(material, blockId));
            }
            if (material.tools()) {
                tool(translationBuilder, material, "sword", "Sword");
                tool(translationBuilder, material, "shovel", "Shovel");
                tool(translationBuilder, material, "pickaxe", "Pickaxe");
                tool(translationBuilder, material, "axe", "Axe");
                tool(translationBuilder, material, "hoe", "Hoe");
                tool(translationBuilder, material, "hammer", "Hammer");
                tool(translationBuilder, material, "bow", "Bow");
            }
            if (material.shield()) {
                tool(translationBuilder, material, "shield", "Shield");
            }
            if (material.armor()) {
                tool(translationBuilder, material, "helmet", "Helmet");
                tool(translationBuilder, material, "chestplate", "Chestplate");
                tool(translationBuilder, material, "leggings", "Leggings");
                tool(translationBuilder, material, "boots", "Boots");
            }
        }
    }

    private static void tool(TranslationBuilder translationBuilder, ArcaneMaterial material, String idSuffix, String nameSuffix) {
        translationBuilder.add(itemKey(material.id() + "_" + idSuffix), material.displayName() + " " + nameSuffix);
    }

    private static String blockName(ArcaneMaterial material, String blockId) {
        if (blockId.equals(material.id() + "_block")) {
            return material.displayName() + " Block";
        }
        if (blockId.equals(material.id() + "_ore")) {
            return material.displayName() + " Ore";
        }
        if (blockId.equals("deepslate_" + material.id() + "_ore")) {
            return "Deepslate " + material.displayName() + " Ore";
        }
        if (blockId.equals("raw_" + material.id() + "_block")) {
            return "Raw " + material.displayName() + " Block";
        }
        return titleCase(blockId);
    }

    private static String itemKey(String id) {
        return "item." + ArcaneArmoryConstants.MOD_ID + "." + id;
    }

    private static String blockKey(String id) {
        return "block." + ArcaneArmoryConstants.MOD_ID + "." + id;
    }

    private static String titleCase(String id) {
        String[] parts = id.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return result.toString();
    }
}
