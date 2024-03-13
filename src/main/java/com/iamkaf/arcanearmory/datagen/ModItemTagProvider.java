package com.iamkaf.arcanearmory.datagen;

import com.iamkaf.arcanearmory.block.ModBlocks;
import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.AAMaterialDatagen;
import com.iamkaf.arcanearmory.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture
    ) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        var tools = getOrCreateTagBuilder(ModTags.Items.TOOLS);
        var minecraftTools = getOrCreateTagBuilder(ItemTags.TOOLS);
        var swords = getOrCreateTagBuilder(ModTags.Items.SWORDS);
        var minecraftSwords = getOrCreateTagBuilder(ItemTags.SWORDS);
        var toolSwords = getOrCreateTagBuilder(ModTags.Items.TOOL_SWORDS);
        var shovels = getOrCreateTagBuilder(ModTags.Items.SHOVELS);
        var minecraftShovels = getOrCreateTagBuilder(ItemTags.SHOVELS);
        var toolShovels = getOrCreateTagBuilder(ModTags.Items.TOOL_SHOVELS);
        var axes = getOrCreateTagBuilder(ModTags.Items.AXES);
        var minecraftAxes = getOrCreateTagBuilder(ItemTags.AXES);
        var toolAxes = getOrCreateTagBuilder(ModTags.Items.TOOL_AXES);
        var pickaxes = getOrCreateTagBuilder(ModTags.Items.PICKAXES);
        var minecraftPickaxes = getOrCreateTagBuilder(ItemTags.PICKAXES);
        var toolPickaxes = getOrCreateTagBuilder(ModTags.Items.TOOL_PICKAXES);
        var hoes = getOrCreateTagBuilder(ModTags.Items.HOES);
        var minecraftHoes = getOrCreateTagBuilder(ItemTags.HOES);
        var toolHoes = getOrCreateTagBuilder(ModTags.Items.TOOL_HOES);
        var trimmables = getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR);
        var armors = getOrCreateTagBuilder(ModTags.Items.ARMORS);
        var armor = getOrCreateTagBuilder(ModTags.Items.ARMOR);
        var helmets = getOrCreateTagBuilder(ModTags.Items.HELMETS);
        var chestplates = getOrCreateTagBuilder(ModTags.Items.CHESTPLATES);
        var leggings = getOrCreateTagBuilder(ModTags.Items.LEGGINGS);
        var boots = getOrCreateTagBuilder(ModTags.Items.BOOTS);
        AAMaterialDatagen.tagArmors(trimmables,
                armors,
                armor,
                helmets,
                chestplates,
                leggings,
                boots,
                tools,
                minecraftTools,
                swords,
                minecraftSwords,
                toolSwords,
                shovels,
                minecraftShovels,
                toolShovels,
                axes,
                minecraftAxes,
                toolAxes,
                pickaxes,
                minecraftPickaxes,
                toolPickaxes,
                hoes,
                minecraftHoes,
                toolHoes
        );

        getOrCreateTagBuilder(ModTags.Items.ARISTEAS).add(ModBlocks.ARISTEA.asItem());

        getOrCreateTagBuilder(ModTags.Items.SHIELDS).add(ModItems.RUBY_SHIELD);
        getOrCreateTagBuilder(ModTags.Items.SHIELDS).add(ModItems.COOLPPER_SHIELD);
        getOrCreateTagBuilder(ModTags.Items.SHIELDS).add(ModItems.TITANIUM_SHIELD);
        getOrCreateTagBuilder(ModTags.Items.SHIELDS).add(ModItems.ARISTEUM_SHIELD);
    }
}
