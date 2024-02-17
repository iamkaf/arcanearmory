package com.iamkaf.arcanearmory.datagen;

import com.iamkaf.arcanearmory.block.ModBlocks;
import com.iamkaf.arcanearmory.block.custom.CottonCandyCropBlock;
import com.iamkaf.arcanearmory.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
//        addDrop(ModBlocks.LOVEY_DOVEY_INFUSER);

        BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition
                .builder(ModBlocks.COTTON_CANDY_CROP)
                .properties(StatePredicate.Builder
                        .create()
                        .exactMatch(CottonCandyCropBlock.AGE, 5));
//        addDrop(ModBlocks.COTTON_CANDY_CROP, cropDrops(ModBlocks.COTTON_CANDY_CROP,
//                ModItems.COTTON_CANDY,
//                ModItems.COTTON_CANDY_SEEDS,
//                builder
//        ));

        addDrop(ModBlocks.ARISTEA);
        addPottedPlantDrops(ModBlocks.POTTED_ARISTEA);
    }
}
