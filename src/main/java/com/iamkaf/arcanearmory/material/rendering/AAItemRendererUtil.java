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
