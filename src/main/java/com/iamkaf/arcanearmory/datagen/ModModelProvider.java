package com.iamkaf.arcanearmory.datagen;


import com.iamkaf.arcanearmory.block.ModBlocks;
import com.iamkaf.arcanearmory.block.custom.CottonCandyCropBlock;
import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.AAMaterialDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerCrop(ModBlocks.COTTON_CANDY_CROP,
                CottonCandyCropBlock.AGE,
                0,
                1,
                2,
                3,
                4,
                5
        );

        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.ARISTEA,
                ModBlocks.POTTED_ARISTEA,
                BlockStateModelGenerator.TintType.NOT_TINTED
        );

//        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.LOVEY_DOVEY_INFUSER);

        AAMaterialDatagen.generateModels(blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//        itemModelGenerator.register(ModItems.SPECIAL_CHOCOLATE_COOKIE, Models.GENERATED);

        AAMaterialDatagen.generateModels(itemModelGenerator);
    }
}
