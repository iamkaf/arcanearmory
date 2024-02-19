package com.iamkaf.arcanearmory.datagen;

import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.AAMaterialDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.MISC, ModItems.SPECIAL_CHOCOLATE_COOKIE, 1)
                .pattern("XXX")
                .pattern("XOX")
                .pattern("XXX")
                .input('X', Items.SUGAR)
                .input('O', Items.COOKIE)
                .criterion(hasItem(Items.COOKIE), conditionsFromItem(Items.COOKIE))
                .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
                .offerTo(exporter,
                        new Identifier(getRecipeName(ModItems.SPECIAL_CHOCOLATE_COOKIE))
                );

        AAMaterialDatagen.generateOreRecipes(exporter);

//        ShapedRecipeJsonBuilder
//                .create(RecipeCategory.MISC, ModBlocks.LOVEY_DOVEY_INFUSER, 1)
//                .pattern("OOO")
//                .pattern("XXX")
//                .pattern("X X")
//                .input('X', Items.IRON_INGOT)
//                .input('O', ModItems.COTTON_CANDY)
//                .criterion(hasItem(ModItems.COTTON_CANDY),
//                        conditionsFromItem(ModItems.COTTON_CANDY)
//                )
//                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(ModItems.COTTON_CANDY))
//                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.LOVEY_DOVEY_INFUSER)));
    }
}
