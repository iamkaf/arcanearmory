package com.iamkaf.arcanearmory.material.rendering;

import com.iamkaf.arcanearmory.material.config.AAMaterialConfiguration;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class AAItemRendererUtil {
    // for generating items with 2 layers that are of parent minecraft:handheld
    private static final Model HANDHELD_TWO_LAYERS = new Model(Optional.of(new Identifier("minecraft",
            "item/handheld"
    )), Optional.empty(), TextureKey.LAYER0, TextureKey.LAYER1);

    public static void registerArmorTints(AAMaterialConfiguration config, Item... items) {
        for (Item item : items) {
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                if (tintIndex == 0) return config.baseTint;
                if (tintIndex == 1) return config.overlayTint;
                return -1;
            }, item);
        }
    }

    public static void registerBlockTints(AAMaterialConfiguration config, Block... blocks) {
        for (Block block : blocks) {
            ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
                return config.baseTint;
            }, block);
        }
    }

    public static void registerToolTints(AAMaterialConfiguration config, Item... items) {
        for (Item item : items) {
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                // In tools layer0 will be the handle
                if (tintIndex == 0) return -1;
                if (tintIndex == 1) return config.baseTint;
                return -1;
            }, item);
        }
    }

    public static void registerHandheldTwoLayers(Item item, ItemModelGenerator generator) {
        Identifier identifier = ModelIds.getItemModelId(item);
        Identifier identifier2 = TextureMap.getId(item);
        Identifier identifier3 = TextureMap.getSubId(item, "_overlay");
        Models.GENERATED_TWO_LAYERS.upload(identifier,
                TextureMap.layered(identifier2, identifier3),
                generator.writer,
                HANDHELD_TWO_LAYERS::createJson
        );
    }
}
