package com.iamkaf.arcanearmory.fabric;

import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryBookProvider;
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryLanguageProvider;
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryLootTableProvider;
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryDataProvider;
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryModelProvider;
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryRecipeProvider;
//? if >=1.20
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryTrimProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class ArcaneArmoryDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        //? if >=1.19.3 {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ArcaneArmoryModelProvider::new);
        pack.addProvider(ArcaneArmoryDataProvider::new);
        pack.addProvider(ArcaneArmoryRecipeProvider::new);
        pack.addProvider(ArcaneArmoryLootTableProvider::new);
        pack.addProvider(ArcaneArmoryLanguageProvider::new);
        //? if >=1.20
        pack.addProvider(ArcaneArmoryTrimProvider::new);
        pack.addProvider(ArcaneArmoryBookProvider::new);
        //?} else {
        fabricDataGenerator.addProvider(ArcaneArmoryModelProvider::new);
        fabricDataGenerator.addProvider(ArcaneArmoryDataProvider::new);
        fabricDataGenerator.addProvider(ArcaneArmoryRecipeProvider::new);
        fabricDataGenerator.addProvider(ArcaneArmoryLootTableProvider::new);
        fabricDataGenerator.addProvider(ArcaneArmoryLanguageProvider::new);
        fabricDataGenerator.addProvider(ArcaneArmoryBookProvider::new);
        //?}
    }
}
