package com.iamkaf.arcanearmory;

//? if >=1.20 && <1.20.5 {
/*import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

// Vanilla shapeless crafting that keeps its result's "nbt", which Fabric drops before 1.20.5. The guide book
// recipe needs it to name the book. The recipe it builds is vanilla, so clients sync it as vanilla.
public final class ShapelessNbtRecipeSerializer implements RecipeSerializer<ShapelessRecipe> {
    public static final ShapelessNbtRecipeSerializer INSTANCE = new ShapelessNbtRecipeSerializer();

    private ShapelessNbtRecipeSerializer() {
    }

    @Override
    public ShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
        ShapelessRecipe recipe = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
        JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
        ItemStack result = ShapedRecipe.itemStackFromJson(resultJson);
        try {
            result.setTag(TagParser.parseTag(GsonHelper.getAsJsonObject(resultJson, "nbt").toString()));
        } catch (CommandSyntaxException exception) {
            throw new JsonParseException("Invalid result nbt in recipe " + id, exception);
        }
        return new ShapelessRecipe(id, recipe.getGroup(), recipe.category(), result, recipe.getIngredients());
    }

    @Override
    public ShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        return RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ShapelessRecipe recipe) {
        RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
    }
}
*///?}
