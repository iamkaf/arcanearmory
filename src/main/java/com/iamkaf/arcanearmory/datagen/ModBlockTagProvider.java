package com.iamkaf.arcanearmory.datagen;

import com.iamkaf.arcanearmory.material.AAMaterialAutoload;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        for (AAMaterialAutoload material : ALL_MATERIALS) {
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                    .add(material.ORE)
                    .add(material.DEEPSLATE_ORE)
                    .add(material.BLOCK)
                    .add(material.RAW_BLOCK);

            getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                    .add(material.BLOCK)
                    .add(material.RAW_BLOCK);

            if (material.blockConfiguration.miningLevelRequired > 3) {
                getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric",
                        "needs_tool_level_" + material.blockConfiguration.miningLevelRequired
                ))).add(material.ORE).add(material.DEEPSLATE_ORE);
            }

            switch (material.blockConfiguration.miningLevelRequired) {
                case 1 -> {
                    getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                            .add(material.ORE)
                            .add(material.DEEPSLATE_ORE);
                }
                case 2 -> {
                    getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                            .add(material.ORE)
                            .add(material.DEEPSLATE_ORE);
                }
                case 3 -> {
                    getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                            .add(material.ORE)
                            .add(material.DEEPSLATE_ORE);
                }
            }
        }
    }
}
