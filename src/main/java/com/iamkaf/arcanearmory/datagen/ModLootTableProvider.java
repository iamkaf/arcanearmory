package com.iamkaf.arcanearmory.datagen;

import com.iamkaf.arcanearmory.block.ModBlocks;
import com.iamkaf.arcanearmory.block.custom.CottonCandyCropBlock;
import com.iamkaf.arcanearmory.material.AAMaterialAutoload;
import com.iamkaf.arcanearmory.material.config.AAMaterialType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition
                .builder(ModBlocks.COTTON_CANDY_CROP)
                .properties(StatePredicate.Builder
                        .create()
                        .exactMatch(CottonCandyCropBlock.AGE, 5));

        addDrop(ModBlocks.ARISTEA);
        addPottedPlantDrops(ModBlocks.POTTED_ARISTEA);

        for (AAMaterialAutoload material : ALL_MATERIALS) {
            if (material.type.equals(AAMaterialType.INGOT) && material.RAW_MATERIAL != null) {
                addDrop(material.ORE, lapisLikeOreDrops(material.ORE,
                        material.RAW_MATERIAL,
                        material.blockConfiguration.minDrops,
                        material.blockConfiguration.maxDrops
                ));
                addDrop(material.DEEPSLATE_ORE, lapisLikeOreDrops(material.DEEPSLATE_ORE,
                        material.RAW_MATERIAL,
                        material.blockConfiguration.minDrops,
                        material.blockConfiguration.maxDrops
                ));
            }
            if (material.type.equals(AAMaterialType.GEM)) {
                addDrop(material.ORE, lapisLikeOreDrops(material.ORE,
                        material.MATERIAL,
                        material.blockConfiguration.minDrops,
                        material.blockConfiguration.maxDrops
                ));
                addDrop(material.DEEPSLATE_ORE, lapisLikeOreDrops(material.DEEPSLATE_ORE,
                        material.MATERIAL,
                        material.blockConfiguration.minDrops,
                        material.blockConfiguration.maxDrops
                ));
            }
            if (material.type.equals(AAMaterialType.CRYSTAL)) {
                addDrop(material.ORE, material.MATERIAL);
                addDrop(material.DEEPSLATE_ORE, material.MATERIAL);
                addDropWithSilkTouch(material.ORE, material.ORE);
                addDropWithSilkTouch(material.DEEPSLATE_ORE, material.DEEPSLATE_ORE);
            }
            // these drop the same for all types
            addDrop(material.BLOCK);
            addDrop(material.RAW_BLOCK);
        }
    }

    public LootTable.Builder lapisLikeOreDrops(Block drop, Item item, float min, float max) {
        return BlockLootTableGenerator.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop,
                (ItemEntry
                        .builder(item)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min,
                                max
                        )))).apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
        ));
    }
}
