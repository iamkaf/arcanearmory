package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.material.rendering.AAItemRendererUtil;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;

import java.util.function.Consumer;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;

public class AAMaterialDatagen {
    public static void generateModels(ItemModelGenerator generator) {
        for (AAMaterialAutoload material : ALL_MATERIALS) {
            generator.register(material.MATERIAL, Models.GENERATED);
            generator.register(material.RAW_MATERIAL, Models.GENERATED);
            if (material.NUGGET != null) {
                generator.register(material.NUGGET, Models.GENERATED);
            }
            generator.registerArmor(material.HELMET);
            generator.registerArmor(material.CHESTPLATE);
            generator.registerArmor(material.LEGGINGS);
            generator.registerArmor(material.BOOTS);
            AAItemRendererUtil.registerHandheldTwoLayers(material.SWORD, generator);
            AAItemRendererUtil.registerHandheldTwoLayers(material.SHOVEL, generator);
            AAItemRendererUtil.registerHandheldTwoLayers(material.PICKAXE, generator);
            AAItemRendererUtil.registerHandheldTwoLayers(material.AXE, generator);
            AAItemRendererUtil.registerHandheldTwoLayers(material.HOE, generator);
        }
    }

    public static void generateModels(BlockStateModelGenerator generator) {
        forEachMaterial((material) -> {
            generator.registerSimpleCubeAll(material.ORE);
            generator.registerSimpleCubeAll(material.BLOCK);
            generator.registerSimpleCubeAll(material.RAW_BLOCK);
        });
    }

    public static void tagTrimmableArmors(FabricTagProvider<Item>.FabricTagBuilder builder) {
        forEachMaterial((material) -> {
            builder.add(material.HELMET, material.CHESTPLATE, material.LEGGINGS, material.BOOTS);
        });
    }

    public static void addItemsToItemGroup(ItemGroup.Entries entries) {
        forEachMaterial((material) -> {
            ItemConvertible[] items = material.getItemsToAddToItemGroup();

            for (ItemConvertible item : items) {
                if (item == null) continue;
                entries.add(item);
            }
        });
    }

    private static void forEachMaterial(Consumer<AAMaterialAutoload> useMaterial) {
        for (AAMaterialAutoload material : ALL_MATERIALS) {
            useMaterial.accept(material);
        }
    }
}
