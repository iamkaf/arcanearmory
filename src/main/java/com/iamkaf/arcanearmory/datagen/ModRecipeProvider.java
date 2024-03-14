package com.iamkaf.arcanearmory.datagen;

import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.AAMaterialDatagen;
import com.iamkaf.arcanearmory.material.ModMaterials;
import com.iamkaf.arcanearmory.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        AAMaterialDatagen.generateOreRecipes(exporter);

        ShapelessRecipeJsonBuilder
                .create(RecipeCategory.DECORATIONS, Blocks.ICE, 16)
                .input(ModMaterials.FROST_DIAMOND.MATERIAL)
                .criterion(
                        hasItem(ModMaterials.FROST_DIAMOND.MATERIAL),
                        conditionsFromItem(ModMaterials.FROST_DIAMOND.MATERIAL)
                )
                .offerTo(exporter, new Identifier(getRecipeName(Blocks.ICE)));

        ShapedRecipeJsonBuilder
                .create(RecipeCategory.FOOD, ModItems.COOLPPER_SHIELD, 1)
                .pattern("ABA")
                .pattern("AAA")
                .pattern(" A ")
                .input('A', ModTags.Items.MINECRAFT_PLANKS)
                .input('B', ModMaterials.COOLPPER.MATERIAL)
                .criterion(hasItem(Blocks.OAK_PLANKS), conditionsFromItem(Blocks.OAK_PLANKS))
                .criterion(
                        hasItem(ModMaterials.COOLPPER.MATERIAL),
                        conditionsFromItem(ModMaterials.COOLPPER.MATERIAL)
                )
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.COOLPPER_SHIELD)));
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.FOOD, ModItems.TITANIUM_SHIELD, 1)
                .pattern("ABA")
                .pattern("AAA")
                .pattern(" A ")
                .input('A', ModTags.Items.MINECRAFT_PLANKS)
                .input('B', ModMaterials.TITANIUM.MATERIAL)
                .criterion(hasItem(Blocks.OAK_PLANKS), conditionsFromItem(Blocks.OAK_PLANKS))
                .criterion(
                        hasItem(ModMaterials.TITANIUM.MATERIAL),
                        conditionsFromItem(ModMaterials.TITANIUM.MATERIAL)
                )
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.TITANIUM_SHIELD)));
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.FOOD, ModItems.ARISTEUM_SHIELD, 1)
                .pattern("ABA")
                .pattern("AAA")
                .pattern(" A ")
                .input('A', ModTags.Items.MINECRAFT_PLANKS)
                .input('B', ModMaterials.ARISTEUM.MATERIAL)
                .criterion(hasItem(Blocks.OAK_PLANKS), conditionsFromItem(Blocks.OAK_PLANKS))
                .criterion(
                        hasItem(ModMaterials.ARISTEUM.MATERIAL),
                        conditionsFromItem(ModMaterials.ARISTEUM.MATERIAL)
                )
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.ARISTEUM_SHIELD)));
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.FOOD, ModItems.VOIDIUM_SHIELD, 1)
                .pattern("ABA")
                .pattern("AAA")
                .pattern(" A ")
                .input('A', ModTags.Items.MINECRAFT_PLANKS)
                .input('B', ModMaterials.VOIDIUM.MATERIAL)
                .criterion(hasItem(Blocks.OAK_PLANKS), conditionsFromItem(Blocks.OAK_PLANKS))
                .criterion(
                        hasItem(ModMaterials.VOIDIUM.MATERIAL),
                        conditionsFromItem(ModMaterials.VOIDIUM.MATERIAL)
                )
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.VOIDIUM_SHIELD)));
    }
}
