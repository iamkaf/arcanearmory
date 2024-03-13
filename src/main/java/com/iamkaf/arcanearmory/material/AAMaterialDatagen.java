package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.material.rendering.AAItemRendererUtil;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;
import static net.minecraft.data.server.recipe.RecipeProvider.*;

public class AAMaterialDatagen {
    public static void generateModels(ItemModelGenerator generator) {
        for (AAMaterial material : ALL_MATERIALS) {
            generator.register(material.MATERIAL, Models.GENERATED);
            generator.register(material.RAW_MATERIAL, Models.GENERATED);
            if (material.NUGGET != null) {
                generator.register(material.NUGGET, Models.GENERATED);
            }
            if (material.generate.armor) {
                generator.registerArmor(material.HELMET);
                generator.registerArmor(material.CHESTPLATE);
                generator.registerArmor(material.LEGGINGS);
                generator.registerArmor(material.BOOTS);
            }
            if (material.generate.weapons) {
                AAItemRendererUtil.registerHandheldTwoLayers(material.SWORD, generator);
            }
            if (material.generate.tools) {
                AAItemRendererUtil.registerHandheldTwoLayers(material.SHOVEL, generator);
                AAItemRendererUtil.registerHandheldTwoLayers(material.PICKAXE, generator);
                AAItemRendererUtil.registerHandheldTwoLayers(material.AXE, generator);
                AAItemRendererUtil.registerHandheldTwoLayers(material.HOE, generator);
            }
        }
    }

    public static void generateModels(BlockStateModelGenerator generator) {
        forEachMaterial((material) -> {
            generator.registerSimpleCubeAll(material.BLOCK);
            if (material.generate.ore) {
                generator.registerSimpleCubeAll(material.ORE);
                generator.registerSimpleCubeAll(material.DEEPSLATE_ORE);
                generator.registerSimpleCubeAll(material.RAW_BLOCK);
            }
        });
    }

    public static void tagArmors(
            FabricTagProvider<Item>.FabricTagBuilder trimmables,
            FabricTagProvider<Item>.FabricTagBuilder armors,
            FabricTagProvider<Item>.FabricTagBuilder armor,
            FabricTagProvider<Item>.FabricTagBuilder helmets,
            FabricTagProvider<Item>.FabricTagBuilder chestplates,
            FabricTagProvider<Item>.FabricTagBuilder leggings,
            FabricTagProvider<Item>.FabricTagBuilder boots,
            FabricTagProvider<Item>.FabricTagBuilder tools,
            FabricTagProvider<Item>.FabricTagBuilder minecraftTools,
            FabricTagProvider<Item>.FabricTagBuilder swords,
            FabricTagProvider<Item>.FabricTagBuilder minecraftSwords,
            FabricTagProvider<Item>.FabricTagBuilder toolSwords,
            FabricTagProvider<Item>.FabricTagBuilder shovels,
            FabricTagProvider<Item>.FabricTagBuilder minecraftShovels,
            FabricTagProvider<Item>.FabricTagBuilder toolShovels,
            FabricTagProvider<Item>.FabricTagBuilder axes,
            FabricTagProvider<Item>.FabricTagBuilder minecraftAxes,
            FabricTagProvider<Item>.FabricTagBuilder toolAxes,
            FabricTagProvider<Item>.FabricTagBuilder pickaxes,
            FabricTagProvider<Item>.FabricTagBuilder minecraftPickaxes,
            FabricTagProvider<Item>.FabricTagBuilder toolPickaxes,
            FabricTagProvider<Item>.FabricTagBuilder hoes,
            FabricTagProvider<Item>.FabricTagBuilder minecraftHoes,
            FabricTagProvider<Item>.FabricTagBuilder toolHoes
    ) {
        forEachMaterial((material) -> {
            if (material.generate.armor) {
                trimmables.add(material.HELMET,
                        material.CHESTPLATE,
                        material.LEGGINGS,
                        material.BOOTS
                );
                armors.add(material.HELMET, material.CHESTPLATE, material.LEGGINGS, material.BOOTS);
                armor.add(material.HELMET, material.CHESTPLATE, material.LEGGINGS, material.BOOTS);
                helmets.add(material.HELMET);
                chestplates.add(material.CHESTPLATE);
                leggings.add(material.LEGGINGS);
                boots.add(material.BOOTS);
            }
            if (material.generate.tools && material.generate.weapons) {
                tools.add(material.SWORD,
                        material.SHOVEL,
                        material.AXE,
                        material.PICKAXE,
                        material.HOE
                );
                minecraftTools.add(material.SWORD,
                        material.SHOVEL,
                        material.AXE,
                        material.PICKAXE,
                        material.HOE
                );

                swords.add(material.SWORD);
                minecraftSwords.add(material.SWORD);
                toolSwords.add(material.SWORD);

                shovels.add(material.SHOVEL);
                minecraftShovels.add(material.SHOVEL);
                toolShovels.add(material.SHOVEL);

                axes.add(material.AXE);
                minecraftAxes.add(material.AXE);
                toolAxes.add(material.AXE);

                pickaxes.add(material.PICKAXE);
                minecraftPickaxes.add(material.PICKAXE);
                toolPickaxes.add(material.PICKAXE);

                hoes.add(material.HOE);
                minecraftHoes.add(material.HOE);
                toolHoes.add(material.HOE);
            }
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

    private static void forEachMaterial(Consumer<AAMaterial> useMaterial) {
        for (AAMaterial material : ALL_MATERIALS) {
            useMaterial.accept(material);
        }
    }

    public static void generateOreRecipes(Consumer<RecipeJsonProvider> exporter) {
        for (AAMaterial material : ALL_MATERIALS) {
            if (material.generate.ore) {
                assert material.ORE != null;
                assert material.DEEPSLATE_ORE != null;
                assert material.RAW_BLOCK != null;

                List<ItemConvertible> smeltables = List.of(material.ORE,
                        material.DEEPSLATE_ORE,
                        material.RAW_MATERIAL
                );
                RecipeProvider.offerSmelting(exporter,
                        smeltables,
                        RecipeCategory.MISC,
                        material.MATERIAL,
                        0.45f,
                        200,
                        material.name
                );
                RecipeProvider.offerBlasting(exporter,
                        smeltables,
                        RecipeCategory.MISC,
                        material.MATERIAL,
                        0.45f,
                        100,
                        material.name
                );
                RecipeProvider.offerReversibleCompactingRecipes(exporter,
                        RecipeCategory.BUILDING_BLOCKS,
                        material.RAW_MATERIAL,
                        RecipeCategory.DECORATIONS,
                        material.RAW_BLOCK
                );
            }
            RecipeProvider.offerReversibleCompactingRecipes(exporter,
                    RecipeCategory.BUILDING_BLOCKS,
                    material.MATERIAL,
                    RecipeCategory.DECORATIONS,
                    material.BLOCK
            );

            if (material.generate.armor) {
                helmetRecipe(material, exporter);
                chestplateRecipe(material, exporter);
                leggingsRecipe(material, exporter);
                bootsRecipe(material, exporter);
                swordRecipe(material, exporter);
                shovelRecipe(material, exporter);
                pickaxeRecipe(material, exporter);
                axeRecipe(material, exporter);
                hoeRecipe(material, exporter);
            }
        }
    }

    private static void helmetRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.HELMET, 1)
                .pattern("XXX")
                .pattern("X X")
                .input('X', material.MATERIAL)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .offerTo(exporter, new Identifier(getRecipeName(material.HELMET)));
    }

    private static void chestplateRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.CHESTPLATE, 1)
                .pattern("X X")
                .pattern("XXX")
                .pattern("XXX")
                .input('X', material.MATERIAL)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .offerTo(exporter, new Identifier(getRecipeName(material.CHESTPLATE)));
    }

    private static void leggingsRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.LEGGINGS, 1)
                .pattern("XXX")
                .pattern("X X")
                .pattern("X X")
                .input('X', material.MATERIAL)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .offerTo(exporter, new Identifier(getRecipeName(material.LEGGINGS)));
    }

    private static void bootsRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.BOOTS, 1)
                .pattern("X X")
                .pattern("X X")
                .input('X', material.MATERIAL)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .offerTo(exporter, new Identifier(getRecipeName(material.BOOTS)));
    }

    private static void swordRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.SWORD, 1)
                .pattern("X")
                .pattern("X")
                .pattern("O")
                .input('X', material.MATERIAL)
                .input('O', Items.STICK)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(material.SWORD)));
    }

    private static void shovelRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.SHOVEL, 1)
                .pattern("X")
                .pattern("O")
                .pattern("O")
                .input('X', material.MATERIAL)
                .input('O', Items.STICK)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(material.SHOVEL)));
    }

    private static void pickaxeRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.PICKAXE, 1)
                .pattern("XXX")
                .pattern(" O ")
                .pattern(" O ")
                .input('X', material.MATERIAL)
                .input('O', Items.STICK)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(material.PICKAXE)));
    }

    private static void axeRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.AXE, 1)
                .pattern(" XX")
                .pattern(" OX")
                .pattern(" O ")
                .input('X', material.MATERIAL)
                .input('O', Items.STICK)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(material.AXE)));
    }

    private static void hoeRecipe(
            AAMaterial material, Consumer<RecipeJsonProvider> exporter
    ) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, material.HOE, 1)
                .pattern(" XX")
                .pattern(" O ")
                .pattern(" O ")
                .input('X', material.MATERIAL)
                .input('O', Items.STICK)
                .criterion(hasItem(material.MATERIAL), conditionsFromItem(material.MATERIAL))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(material.HOE)));
    }
}
